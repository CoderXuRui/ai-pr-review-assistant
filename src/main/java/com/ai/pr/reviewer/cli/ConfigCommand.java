package com.ai.pr.reviewer.cli;

import com.ai.pr.reviewer.config.ConfigLoader;
import com.ai.pr.reviewer.config.ReviewConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "config",
    description = "管理配置",
    mixinStandardHelpOptions = true,
    subcommands = {
        ConfigCommand.InitCommand.class,
        ConfigCommand.ShowCommand.class
    }
)
public class ConfigCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        System.out.println("使用 'config --help' 查看可用命令");
        return 0;
    }

    @CommandLine.Command(
        name = "init",
        description = "初始化配置文件",
        mixinStandardHelpOptions = true
    )
    public static class InitCommand implements Callable<Integer> {
        private static final Logger logger = LoggerFactory.getLogger(InitCommand.class);

        @CommandLine.Option(names = {"--force", "-f"}, description = "覆盖现有配置文件")
        private boolean force = false;

        @CommandLine.Option(names = {"--output", "-o"}, description = "输出路径", defaultValue = ".pr-reviewer.yml")
        private String outputPath;

        @Override
        public Integer call() {
            try {
                Path path = Paths.get(outputPath);
                if (Files.exists(path) && !force) {
                    System.err.println("配置文件已存在: " + outputPath);
                    System.err.println("使用 --force 覆盖");
                    return 1;
                }

                Path examplePath = Paths.get(".pr-reviewer.example.yml");
                if (Files.exists(examplePath)) {
                    Files.copy(examplePath, path);
                    System.out.println("配置文件已创建: " + outputPath);
                    System.out.println("请编辑文件并填入您的 API 密钥");
                } else {
                    ReviewConfig config = new ReviewConfig();
                    ConfigLoader loader = new ConfigLoader();
                    loader.saveConfig(config, outputPath);
                    System.out.println("配置文件已创建: " + outputPath);
                }
                return 0;
            } catch (IOException e) {
                logger.error("Failed to create config file", e);
                System.err.println("创建配置文件失败: " + e.getMessage());
                return 1;
            }
        }
    }

    @CommandLine.Command(
        name = "show",
        description = "显示当前配置",
        mixinStandardHelpOptions = true
    )
    public static class ShowCommand implements Callable<Integer> {

        @CommandLine.Option(names = {"--config", "-c"}, description = "配置文件路径")
        private String configFile;

        @Override
        public Integer call() {
            ConfigLoader loader = new ConfigLoader();
            ReviewConfig config = configFile != null
                ? loader.loadConfigFromFile(configFile)
                : loader.loadConfig();

            System.out.println("当前配置:");
            System.out.println();
            System.out.println("GitHub:");
            System.out.println("  API URL: " + config.getGithub().getApiUrl());
            System.out.println("  Token: " + (config.getGithub().getToken() != null ? "***" : "未设置"));
            System.out.println("  Post Comment: " + config.getGithub().isPostComment());
            System.out.println("  Set Status: " + config.getGithub().isSetStatusCheck());
            System.out.println();
            System.out.println("AI:");
            System.out.println("  Model: " + config.getAi().getModel());
            System.out.println("  API Key: " + (config.getAi().getApiKey() != null ? "***" : "未设置"));
            System.out.println("  Temperature: " + config.getAi().getTemperature());
            System.out.println("  Max Tokens: " + config.getAi().getMaxTokens());
            System.out.println("  Chunk Size: " + config.getAi().getChunkSize());
            System.out.println();
            System.out.println("Max File Size: " + config.getMaxFileSizeKB() + " KB");

            return 0;
        }
    }
}
