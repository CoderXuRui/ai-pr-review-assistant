package com.ai.pr.reviewer.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 单个审查类别的配置
 */
public class CategoryConfig {
    @JsonProperty("enabled")
    private boolean enabled = true;

    @JsonProperty("severity")
    private Severity defaultSeverity;

    public CategoryConfig() {}

    public CategoryConfig(boolean enabled, Severity defaultSeverity) {
        this.enabled = enabled;
        this.defaultSeverity = defaultSeverity;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Severity getDefaultSeverity() {
        return defaultSeverity;
    }

    public void setDefaultSeverity(Severity defaultSeverity) {
        this.defaultSeverity = defaultSeverity;
    }
}
