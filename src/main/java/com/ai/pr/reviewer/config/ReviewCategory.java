package com.ai.pr.reviewer.config;

/**
 * 审查类别
 */
public enum ReviewCategory {
    BUG("Bug", "潜在Bug检测"),
    SECURITY("Security", "安全问题检测"),
    PERFORMANCE("Performance", "性能问题检测"),
    STYLE("Style", "代码风格与最佳实践");

    private final String displayName;
    private final String description;

    ReviewCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
