package com.ai.pr.reviewer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 主配置类
 */
public class ReviewConfig {
    @JsonProperty("github")
    private GithubConfig github = new GithubConfig();

    @JsonProperty("ai")
    private AiConfig ai = new AiConfig();

    @JsonProperty("categories")
    private Map<ReviewCategory, CategoryConfig> categories = new EnumMap<>(ReviewCategory.class);

    @JsonProperty("ignorePaths")
    private List<String> ignorePaths = new ArrayList<>();

    @JsonProperty("ignorePatterns")
    private List<String> ignorePatterns = new ArrayList<>();

    @JsonProperty("maxFileSizeKB")
    private Integer maxFileSizeKB = 100;

    @JsonProperty("languageRules")
    private Map<ProgrammingLanguage, Map<String, Object>> languageRules = new EnumMap<>(ProgrammingLanguage.class);

    public ReviewConfig() {
        initDefaultCategories();
        initDefaultIgnorePaths();
    }

    private void initDefaultCategories() {
        categories.put(ReviewCategory.BUG, new CategoryConfig(true, Severity.HIGH));
        categories.put(ReviewCategory.SECURITY, new CategoryConfig(true, Severity.CRITICAL));
        categories.put(ReviewCategory.PERFORMANCE, new CategoryConfig(true, Severity.MEDIUM));
        categories.put(ReviewCategory.STYLE, new CategoryConfig(true, Severity.LOW));
    }

    private void initDefaultIgnorePaths() {
        ignorePaths.add("node_modules/");
        ignorePaths.add("target/");
        ignorePaths.add("build/");
        ignorePaths.add(".git/");
        ignorePatterns.add("*.min.js");
        ignorePatterns.add("*.min.css");
        ignorePatterns.add("*.lock");
    }

    public GithubConfig getGithub() {
        return github;
    }

    public void setGithub(GithubConfig github) {
        this.github = github;
    }

    public AiConfig getAi() {
        return ai;
    }

    public void setAi(AiConfig ai) {
        this.ai = ai;
    }

    public Map<ReviewCategory, CategoryConfig> getCategories() {
        return categories;
    }

    public void setCategories(Map<ReviewCategory, CategoryConfig> categories) {
        this.categories = categories;
    }

    public List<String> getIgnorePaths() {
        return ignorePaths;
    }

    public void setIgnorePaths(List<String> ignorePaths) {
        this.ignorePaths = ignorePaths;
    }

    public List<String> getIgnorePatterns() {
        return ignorePatterns;
    }

    public void setIgnorePatterns(List<String> ignorePatterns) {
        this.ignorePatterns = ignorePatterns;
    }

    public Integer getMaxFileSizeKB() {
        return maxFileSizeKB;
    }

    public void setMaxFileSizeKB(Integer maxFileSizeKB) {
        this.maxFileSizeKB = maxFileSizeKB;
    }

    public Map<ProgrammingLanguage, Map<String, Object>> getLanguageRules() {
        return languageRules;
    }

    public void setLanguageRules(Map<ProgrammingLanguage, Map<String, Object>> languageRules) {
        this.languageRules = languageRules;
    }

    public boolean isCategoryEnabled(ReviewCategory category) {
        CategoryConfig config = categories.get(category);
        return config != null && config.isEnabled();
    }

    public boolean shouldIgnoreFile(String filePath) {
        String normalizedPath = filePath.replace("\\", "/");
        for (String ignorePath : ignorePaths) {
            if (normalizedPath.contains(ignorePath)) {
                return true;
            }
        }
        for (String pattern : ignorePatterns) {
            if (matchesPattern(normalizedPath, pattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesPattern(String filePath, String pattern) {
        String regex = pattern
            .replace(".", "\\.")
            .replace("*", ".*")
            .replace("?", ".");
        return filePath.matches(".*" + regex + ".*");
    }
}
