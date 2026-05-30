package com.ai.pr.reviewer.config;

/**
 * 问题严重程度
 */
public enum Severity {
    CRITICAL("Critical", "🔴"),
    HIGH("High", "🟠"),
    MEDIUM("Medium", "🟡"),
    LOW("Low", "🟢");

    private final String displayName;
    private final String emoji;

    Severity(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }
}
