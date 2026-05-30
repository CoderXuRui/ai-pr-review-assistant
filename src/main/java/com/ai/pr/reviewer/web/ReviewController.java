package com.ai.pr.reviewer.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ai.pr.reviewer.config.ConfigLoader;
import com.ai.pr.reviewer.config.ReviewConfig;
import com.ai.pr.reviewer.github.FileChange;
import com.ai.pr.reviewer.github.GithubClient;
import com.ai.pr.reviewer.github.PullRequestInfo;
import com.ai.pr.reviewer.review.CodeReviewEngine;
import com.ai.pr.reviewer.review.ReviewFinding;
import com.ai.pr.reviewer.review.ReviewResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewConfig config;
    private final ConfigLoader configLoader;

    public ReviewController() {
        this.configLoader = new ConfigLoader();
        this.config = configLoader.loadConfig();
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("aiProvider", config.getAi().getProvider());
        result.put("model", config.getAi().getModel());
        result.put("githubToken", config.getGithub().getToken() != null ? "configured" : "not set");
        return result;
    }

    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("aiProvider", config.getAi().getProvider());
        result.put("model", config.getAi().getModel());
        result.put("baseUrl", config.getAi().getBaseUrl());
        result.put("githubApiUrl", config.getGithub().getApiUrl());
        result.put("postComment", config.getGithub().isPostComment());
        result.put("setStatusCheck", config.getGithub().isSetStatusCheck());
        result.put("maxFileSizeKB", config.getMaxFileSizeKB());
        return result;
    }

    @PostMapping("/review")
    public ResponseEntity<Map<String, Object>> review(@RequestBody ReviewRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String repoOwner = request.getRepoOwner();
            String repoName = request.getRepoName();
            int prNumber = request.getPrNumber();

            result.put("status", "started");
            result.put("repo", repoOwner + "/" + repoName);
            result.put("prNumber", prNumber);

            GithubClient github = new GithubClient(config);
            PullRequestInfo prInfo = github.getPullRequestInfo(repoOwner, repoName, prNumber);
            List<FileChange> changes = github.getPullRequestChanges(repoOwner, repoName, prNumber);

            result.put("prTitle", prInfo.getTitle());
            result.put("prBody", prInfo.getBody());
            result.put("prAuthor", prInfo.getAuthor());
            result.put("prUrl", prInfo.getUrl());
            result.put("fileCount", changes.size());

            List<Map<String, String>> fileList = new ArrayList<>();
            for (FileChange change : changes) {
                Map<String, String> fileInfo = new HashMap<>();
                fileInfo.put("name", change.getFileName());
                fileInfo.put("type", change.getChangeType().name());
                fileInfo.put("language", change.getLanguage().name());
                fileInfo.put("additions", String.valueOf(change.getLinesAdded()));
                fileInfo.put("deletions", String.valueOf(change.getLinesRemoved()));
                fileList.add(fileInfo);
            }
            result.put("files", fileList);

            if (request.isRunAiReview()) {
                result.put("aiReview", "started");
                CodeReviewEngine engine = new CodeReviewEngine(config);
                ReviewResult reviewResult = engine.review(prInfo, changes);

                logger.info("ReviewResult received: totalIssues={}", reviewResult.getTotalIssues());
                logger.info("ReviewResult findings size: {}", reviewResult.getFindings().size());
                for (ReviewFinding f : reviewResult.getFindings()) {
                    logger.info("  - Finding: {} ({})", f.getTitle(), f.getSeverity());
                }

                result.put("aiReview", "complete");
                result.put("reviewSummary", reviewResult.getSummary());
                result.put("reviewDuration", reviewResult.getReviewDurationMs());
                result.put("totalIssues", reviewResult.getTotalIssues());

                Map<String, Integer> severityCount = new HashMap<>();
                for (Map.Entry<com.ai.pr.reviewer.config.Severity, Integer> entry : reviewResult.countBySeverity().entrySet()) {
                    severityCount.put(entry.getKey().name(), entry.getValue());
                }
                result.put("severityCount", severityCount);

                List<Map<String, Object>> findings = new ArrayList<>();
                for (ReviewFinding finding : reviewResult.getFindings()) {
                    Map<String, Object> f = new HashMap<>();
                    f.put("category", finding.getCategory().name());
                    f.put("severity", finding.getSeverity().name());
                    f.put("fileName", finding.getFileName());
                    f.put("lineNumber", finding.getLineNumber());
                    f.put("title", finding.getTitle());
                    f.put("description", finding.getDescription());
                    f.put("suggestion", finding.getSuggestion());
                    findings.add(f);
                }
                result.put("findings", findings);

                if (request.isPostComment()) {
                    try {
                        com.ai.pr.reviewer.output.MarkdownReporter reporter =
                            new com.ai.pr.reviewer.output.MarkdownReporter();
                        String comment = reporter.generateGithubComment(reviewResult);
                        github.postComment(repoOwner, repoName, prNumber, comment);
                        result.put("commentPosted", true);
                    } catch (Exception e) {
                        result.put("commentPosted", false);
                        result.put("commentError", e.getMessage());
                    }
                }
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(result);
        }
    }

    public static class ReviewRequest {
        private String repoOwner;
        private String repoName;
        private int prNumber;
        private boolean postComment = false;
        private boolean runAiReview = true;

        public String getRepoOwner() { return repoOwner; }
        public void setRepoOwner(String repoOwner) { this.repoOwner = repoOwner; }
        public String getRepoName() { return repoName; }
        public void setRepoName(String repoName) { this.repoName = repoName; }
        public int getPrNumber() { return prNumber; }
        public void setPrNumber(int prNumber) { this.prNumber = prNumber; }
        public boolean isPostComment() { return postComment; }
        public void setPostComment(boolean postComment) { this.postComment = postComment; }
        public boolean isRunAiReview() { return runAiReview; }
        public void setRunAiReview(boolean runAiReview) { this.runAiReview = runAiReview; }
    }
}
