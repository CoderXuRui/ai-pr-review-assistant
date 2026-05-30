package com.ai.pr.reviewer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 配置加载器
 */
public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String CONFIG_FILE_NAME = ".pr-reviewer.yml";

    private final ObjectMapper objectMapper;

    public ConfigLoader() {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * 加载配置：默认配置 + 项目配置合并
     */
    public ReviewConfig loadConfig() {
        ReviewConfig config = new ReviewConfig();

        Path configPath = findConfigFile();
        if (configPath != null && Files.exists(configPath)) {
            logger.info("Loading config from: {}", configPath);
            try {
                ReviewConfig projectConfig = objectMapper.readValue(configPath.toFile(), ReviewConfig.class);
                mergeConfig(config, projectConfig);
            } catch (IOException e) {
                logger.warn("Failed to load config file: {}, using defaults", configPath, e);
            }
        } else {
            logger.info("No config file found, using defaults");
        }

        return config;
    }

    /**
     * 从指定文件加载配置
     */
    public ReviewConfig loadConfigFromFile(String filePath) {
        try {
            return objectMapper.readValue(new File(filePath), ReviewConfig.class);
        } catch (IOException e) {
            logger.error("Failed to load config from: {}", filePath, e);
            return new ReviewConfig();
        }
    }

    /**
     * 保存配置到文件
     */
    public void saveConfig(ReviewConfig config, String filePath) throws IOException {
        objectMapper.writeValue(new File(filePath), config);
    }

    /**
     * 查找配置文件：从当前目录向上查找
     */
    private Path findConfigFile() {
        Path current = Paths.get(".").toAbsolutePath().normalize();
        while (current != null) {
            Path configPath = current.resolve(CONFIG_FILE_NAME);
            if (Files.exists(configPath)) {
                return configPath;
            }
            current = current.getParent();
        }
        return null;
    }

    /**
     * 合并项目配置到默认配置
     */
    private void mergeConfig(ReviewConfig defaultConfig, ReviewConfig projectConfig) {
        if (projectConfig.getGithub() != null) {
            mergeGithubConfig(defaultConfig.getGithub(), projectConfig.getGithub());
        }
        if (projectConfig.getAi() != null) {
            mergeAiConfig(defaultConfig.getAi(), projectConfig.getAi());
        }
        if (projectConfig.getCategories() != null && !projectConfig.getCategories().isEmpty()) {
            defaultConfig.getCategories().putAll(projectConfig.getCategories());
        }
        if (projectConfig.getIgnorePaths() != null && !projectConfig.getIgnorePaths().isEmpty()) {
            defaultConfig.getIgnorePaths().addAll(projectConfig.getIgnorePaths());
        }
        if (projectConfig.getIgnorePatterns() != null && !projectConfig.getIgnorePatterns().isEmpty()) {
            defaultConfig.getIgnorePatterns().addAll(projectConfig.getIgnorePatterns());
        }
        if (projectConfig.getMaxFileSizeKB() != null) {
            defaultConfig.setMaxFileSizeKB(projectConfig.getMaxFileSizeKB());
        }
        if (projectConfig.getLanguageRules() != null && !projectConfig.getLanguageRules().isEmpty()) {
            defaultConfig.getLanguageRules().putAll(projectConfig.getLanguageRules());
        }
    }

    private void mergeGithubConfig(GithubConfig defaultConfig, GithubConfig projectConfig) {
        if (projectConfig.getToken() != null) {
            defaultConfig.setToken(projectConfig.getToken());
        }
        if (projectConfig.getApiUrl() != null) {
            defaultConfig.setApiUrl(projectConfig.getApiUrl());
        }
        defaultConfig.setPostComment(projectConfig.isPostComment());
        defaultConfig.setUpdateExistingComment(projectConfig.isUpdateExistingComment());
        defaultConfig.setSetStatusCheck(projectConfig.isSetStatusCheck());
    }

    private void mergeAiConfig(AiConfig defaultConfig, AiConfig projectConfig) {
        if (projectConfig.getProvider() != null) {
            defaultConfig.setProvider(projectConfig.getProvider());
        }
        if (projectConfig.getBaseUrl() != null) {
            defaultConfig.setBaseUrl(projectConfig.getBaseUrl());
        }
        if (projectConfig.getModel() != null) {
            defaultConfig.setModel(projectConfig.getModel());
        }
        if (projectConfig.getApiKey() != null) {
            defaultConfig.setApiKey(projectConfig.getApiKey());
        }
        if (projectConfig.getTemperature() != null) {
            defaultConfig.setTemperature(projectConfig.getTemperature());
        }
        if (projectConfig.getMaxTokens() != null) {
            defaultConfig.setMaxTokens(projectConfig.getMaxTokens());
        }
        if (projectConfig.getChunkSize() != null) {
            defaultConfig.setChunkSize(projectConfig.getChunkSize());
        }
    }
}
