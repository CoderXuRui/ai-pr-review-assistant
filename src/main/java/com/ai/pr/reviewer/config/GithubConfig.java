package com.ai.pr.reviewer.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GitHub配置
 */
public class GithubConfig {
    @JsonProperty("token")
    private String token;

    @JsonProperty("apiUrl")
    private String apiUrl = "https://api.github.com";

    @JsonProperty("postComment")
    private boolean postComment = true;

    @JsonProperty("updateExistingComment")
    private boolean updateExistingComment = true;

    @JsonProperty("setStatusCheck")
    private boolean setStatusCheck = true;

    public GithubConfig() {}

    public String getToken() {
        if (token == null || token.isBlank()) {
            return System.getenv("GITHUB_TOKEN");
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public boolean isPostComment() {
        return postComment;
    }

    public void setPostComment(boolean postComment) {
        this.postComment = postComment;
    }

    public boolean isUpdateExistingComment() {
        return updateExistingComment;
    }

    public void setUpdateExistingComment(boolean updateExistingComment) {
        this.updateExistingComment = updateExistingComment;
    }

    public boolean isSetStatusCheck() {
        return setStatusCheck;
    }

    public void setSetStatusCheck(boolean setStatusCheck) {
        this.setStatusCheck = setStatusCheck;
    }
}
