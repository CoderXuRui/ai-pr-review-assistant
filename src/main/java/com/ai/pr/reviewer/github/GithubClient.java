package com.ai.pr.reviewer.github;

import com.ai.pr.reviewer.config.ProgrammingLanguage;
import com.ai.pr.reviewer.config.ReviewConfig;
import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * GitHub API 客户端封装
 */
public class GithubClient {
    private static final Logger logger = LoggerFactory.getLogger(GithubClient.class);

    private final ReviewConfig config;
    private final GitHub github;

    public GithubClient(ReviewConfig config) throws IOException {
        this.config = config;
        this.github = authenticate();
    }

    private GitHub authenticate() throws IOException {
        String token = config.getGithub().getToken();
        if (token != null && !token.isBlank()) {
            logger.info("Authenticating with GitHub token");
            return new GitHubBuilder().withOAuthToken(token).build();
        }
        logger.warn("No GitHub token provided, using anonymous access (rate-limited)");
        return GitHub.connectAnonymously();
    }

    /**
     * 获取 PR 信息
     */
    public PullRequestInfo getPullRequestInfo(String repoOwner, String repoName, int prNumber) throws IOException {
        GHRepository repo = github.getRepository(repoOwner + "/" + repoName);
        GHPullRequest pr = repo.getPullRequest(prNumber);

        List<String> labels = pr.getLabels().stream()
            .map(GHLabel::getName)
            .toList();

        return new PullRequestInfo(
            repoOwner + "/" + repoName,
            prNumber,
            pr.getTitle(),
            pr.getBody(),
            pr.getUser().getLogin(),
            pr.getHtmlUrl().toString(),
            pr.getHead().getSha(),
            pr.getBase().getSha(),
            labels
        );
    }

    /**
     * 获取 PR 中的所有文件变更
     */
    public List<FileChange> getPullRequestChanges(String repoOwner, String repoName, int prNumber) throws IOException {
        GHRepository repo = github.getRepository(repoOwner + "/" + repoName);
        GHPullRequest pr = repo.getPullRequest(prNumber);

        List<FileChange> changes = new ArrayList<>();
        int maxFileSizeBytes = config.getMaxFileSizeKB() * 1024;

        for (GHPullRequestFileDetail file : pr.listFiles()) {
            String fileName = file.getFilename();

            if (config.shouldIgnoreFile(fileName)) {
                logger.info("Ignoring file: {}", fileName);
                continue;
            }

            FileChange.ChangeType changeType = parseChangeType(file.getStatus());
            ProgrammingLanguage language = ProgrammingLanguage.fromFileName(fileName);
            String content = null;
            String diff = file.getPatch();

            if (changeType != FileChange.ChangeType.DELETED) {
                try {
                    GHContent ghContent = repo.getFileContent(fileName, pr.getHead().getSha());
                    if (ghContent.getSize() <= maxFileSizeBytes) {
                        content = ghContent.getContent();
                    } else {
                        logger.warn("File too large, skipping content: {} ({} bytes)", fileName, ghContent.getSize());
                    }
                } catch (Exception e) {
                    logger.warn("Could not fetch content for: {}", fileName, e);
                }
            }

            changes.add(new FileChange(
                fileName,
                changeType,
                content,
                diff,
                language,
                file.getAdditions(),
                file.getDeletions()
            ));
        }

        return changes;
    }

    /**
     * 发布 PR 评论
     */
    public void postComment(String repoOwner, String repoName, int prNumber, String comment) throws IOException {
        if (!config.getGithub().isPostComment()) {
            logger.info("Posting comments disabled by config");
            return;
        }

        GHRepository repo = github.getRepository(repoOwner + "/" + repoName);
        GHPullRequest pr = repo.getPullRequest(prNumber);

        if (config.getGithub().isUpdateExistingComment()) {
            Optional<GHIssueComment> existingComment = findExistingComment(pr);
            if (existingComment.isPresent()) {
                logger.info("Updating existing comment");
                existingComment.get().update(comment);
                return;
            }
        }

        logger.info("Creating new comment");
        pr.comment(comment);
    }

    /**
     * 设置 PR 状态检查
     */
    public void setStatus(String repoOwner, String repoName, String commitSha,
                         String state, String context, String description,
                         String targetUrl) throws IOException {
        if (!config.getGithub().isSetStatusCheck()) {
            logger.info("Setting status checks disabled by config");
            return;
        }

        GHRepository repo = github.getRepository(repoOwner + "/" + repoName);
        GHCommitState commitState = parseCommitState(state);
        repo.createCommitStatus(commitSha, commitState, targetUrl, description, context);
    }

    private Optional<GHIssueComment> findExistingComment(GHPullRequest pr) throws IOException {
        String botCommentMarker = "<!-- AI PR Review Assistant -->";
        for (GHIssueComment comment : pr.getComments()) {
            if (comment.getBody().contains(botCommentMarker)) {
                return Optional.of(comment);
            }
        }
        return Optional.empty();
    }

    private FileChange.ChangeType parseChangeType(String status) {
        return switch (status) {
            case "added" -> FileChange.ChangeType.ADDED;
            case "modified" -> FileChange.ChangeType.MODIFIED;
            case "removed" -> FileChange.ChangeType.DELETED;
            case "renamed" -> FileChange.ChangeType.RENAMED;
            default -> FileChange.ChangeType.MODIFIED;
        };
    }

    private GHCommitState parseCommitState(String state) {
        return switch (state.toUpperCase()) {
            case "SUCCESS" -> GHCommitState.SUCCESS;
            case "FAILURE" -> GHCommitState.FAILURE;
            case "ERROR" -> GHCommitState.ERROR;
            case "PENDING" -> GHCommitState.PENDING;
            default -> GHCommitState.SUCCESS;
        };
    }
}
