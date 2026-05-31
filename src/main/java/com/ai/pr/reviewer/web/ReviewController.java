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

import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewConfig config;
    private final ConfigLoader configLoader;

    private final Map<String, ReviewProgress> progressMap = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

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

    @GetMapping("/progress/{taskId}")
    public ResponseEntity<Map<String, Object>> getProgress(@PathVariable("taskId") String taskId) {
        ReviewProgress progress = progressMap.get(taskId);
        if (progress == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "not_found");
            return ResponseEntity.badRequest().body(error);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("status", progress.status);
        result.put("message", progress.message);
        result.put("currentFile", progress.currentFile);
        result.put("totalFiles", progress.totalFiles);
        result.put("processedFiles", progress.processedFiles);
        result.put("percentage", progress.percentage);
        if (progress.result != null) {
            result.put("result", progress.result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/review")
    public ResponseEntity<Map<String, Object>> review(@RequestBody ReviewRequest request) {
        Map<String, Object> result = new HashMap<>();
        String taskId = UUID.randomUUID().toString();
        ReviewProgress progress = new ReviewProgress();
        progress.status = "starting";
        progress.message = "初始化审查任务...";
        progressMap.put(taskId, progress);

        executor.submit(() -> {
            try {
                String repoOwner = request.getRepoOwner();
                String repoName = request.getRepoName();
                int prNumber = request.getPrNumber();

                progress.status = "fetching";
                progress.message = "正在连接 GitHub 并获取 PR 信息...";
                progressMap.put(taskId, progress);

                GithubClient github;
                try {
                    github = new GithubClient(config);
                } catch (Exception e) {
                    throw new RuntimeException("无法初始化 GitHub 客户端，请检查配置！", e);
                }

                PullRequestInfo prInfo;
                try {
                    prInfo = github.getPullRequestInfo(repoOwner, repoName, prNumber);
                } catch (Exception e) {
                    throw new RuntimeException("无法获取 PR 信息，请检查仓库名称和 PR 编号是否正确！", e);
                }

                List<FileChange> changes;
                try {
                    changes = github.getPullRequestChanges(repoOwner, repoName, prNumber);
                } catch (Exception e) {
                    throw new RuntimeException("无法获取 PR 文件变更信息！", e);
                }

                progress.status = "fetching";
                progress.message = "成功获取 PR 信息，准备开始审查...";
                progress.totalFiles = changes.size();
                progress.processedFiles = 0;
                progressMap.put(taskId, progress);

                Map<String, Object> taskResult = new HashMap<>();
                taskResult.put("prTitle", prInfo.getTitle());
                taskResult.put("prBody", prInfo.getBody());
                taskResult.put("prAuthor", prInfo.getAuthor());
                taskResult.put("prUrl", prInfo.getUrl());
                taskResult.put("fileCount", changes.size());

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
                taskResult.put("files", fileList);

                if (request.isRunAiReview()) {
                    progress.status = "reviewing";
                    progress.message = "正在运行 AI 审查...";
                    progressMap.put(taskId, progress);

                    CodeReviewEngine engine = new CodeReviewEngine(config);

                    final int[] fileCounter = {0};
                    engine.setProgressListener((file, current, total) -> {
                        progress.currentFile = file;
                        progress.processedFiles = current;
                        progress.totalFiles = total;
                        progress.message = "正在审查文件 " + file + " (" + current + "/" + total + ")";
                        if (total > 0) {
                            progress.percentage = (int) ((current * 100) / total);
                        }
                        progressMap.put(taskId, progress);
                    });

                    ReviewResult reviewResult = engine.review(prInfo, changes);

                    logger.info("ReviewResult received: totalIssues={}", reviewResult.getTotalIssues());
                    logger.info("ReviewResult findings size: {}", reviewResult.getFindings().size());
                    for (ReviewFinding f : reviewResult.getFindings()) {
                        logger.info("  - Finding: {} ({})", f.getTitle(), f.getSeverity());
                    }

                    taskResult.put("aiReview", "complete");
                    taskResult.put("reviewSummary", reviewResult.getSummary());
                    taskResult.put("reviewDuration", reviewResult.getReviewDurationMs());
                    taskResult.put("totalIssues", reviewResult.getTotalIssues());

                    Map<String, Integer> severityCount = new HashMap<>();
                    for (Map.Entry<com.ai.pr.reviewer.config.Severity, Integer> entry : reviewResult.countBySeverity().entrySet()) {
                        severityCount.put(entry.getKey().name(), entry.getValue());
                    }
                    taskResult.put("severityCount", severityCount);

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
                    taskResult.put("findings", findings);

                    if (request.isPostComment()) {
                        try {
                            progress.status = "posting";
                            progress.message = "正在发布评论到 GitHub...";
                            progressMap.put(taskId, progress);

                            com.ai.pr.reviewer.output.MarkdownReporter reporter =
                                new com.ai.pr.reviewer.output.MarkdownReporter();
                            String comment = reporter.generateGithubComment(reviewResult);
                            github.postComment(repoOwner, repoName, prNumber, comment);
                            taskResult.put("commentPosted", true);
                        } catch (Exception e) {
                            taskResult.put("commentPosted", false);
                            taskResult.put("commentError", e.getMessage());
                        }
                    }
                }

                progress.status = "complete";
                progress.message = "审查完成！";
                progress.percentage = 100;
                progress.result = taskResult;
                progressMap.put(taskId, progress);
            } catch (Exception e) {
                logger.error("Review error", e);
                progress.status = "error";
                progress.message = "审查失败: " + e.getMessage();
                progressMap.put(taskId, progress);
            }
        });

        result.put("taskId", taskId);
        result.put("status", "started");
        return ResponseEntity.ok(result);
    }

    public static class ReviewProgress {
        public String status = "starting";
        public String message = "";
        public String currentFile = "";
        public int totalFiles = 0;
        public int processedFiles = 0;
        public int percentage = 0;
        public Map<String, Object> result = null;
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
