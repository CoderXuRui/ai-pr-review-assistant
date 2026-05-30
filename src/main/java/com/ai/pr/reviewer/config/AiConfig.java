package com.ai.pr.reviewer.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI模型配置
 */
public class AiConfig {
    @JsonProperty("provider")
    private String provider = "anthropic";

    @JsonProperty("baseUrl")
    private String baseUrl;

    @JsonProperty("model")
    private String model = "claude-3-5-sonnet-20241022";

    @JsonProperty("apiKey")
    private String apiKey;

    @JsonProperty("temperature")
    private Double temperature = 0.7;

    @JsonProperty("maxTokens")
    private Integer maxTokens = 4096;

    @JsonProperty("chunkSize")
    private Integer chunkSize = 3000;

    public AiConfig() {}

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getBaseUrl() {
        if (baseUrl != null && !baseUrl.isBlank()) {
            return baseUrl;
        }
        if ("deepseek".equals(provider)) {
            return "https://api.deepseek.com";
        }
        return null;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            if ("deepseek".equals(provider)) {
                return System.getenv("DEEPSEEK_API_KEY");
            }
            return System.getenv("ANTHROPIC_API_KEY");
        }
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }
}
