package com.ai.pr.reviewer.github;

import java.util.List;

/**
 * PR 信息
 */
public class PullRequestInfo {
    private final String repository;
    private final int number;
    private final String title;
    private final String body;
    private final String author;
    private final String url;
    private final String headSha;
    private final String baseSha;
    private final List<String> labels;

    public PullRequestInfo(String repository, int number, String title, String body,
                          String author, String url, String headSha, String baseSha,
                          List<String> labels) {
        this.repository = repository;
        this.number = number;
        this.title = title;
        this.body = body;
        this.author = author;
        this.url = url;
        this.headSha = headSha;
        this.baseSha = baseSha;
        this.labels = labels;
    }

    public String getRepository() { return repository; }
    public int getNumber() { return number; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getAuthor() { return author; }
    public String getUrl() { return url; }
    public String getHeadSha() { return headSha; }
    public String getBaseSha() { return baseSha; }
    public List<String> getLabels() { return labels; }
}
