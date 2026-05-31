package com.ai.pr.reviewer.review;

import com.ai.pr.reviewer.cache.CachedFileReview;
import com.ai.pr.reviewer.cache.FileReviewCache;
import com.ai.pr.reviewer.cache.ReviewCache;
import com.ai.pr.reviewer.config.*;
import com.ai.pr.reviewer.github.FileChange;
import com.ai.pr.reviewer.github.PullRequestInfo;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 多维度 AI 代码审查引擎
 */
public class CodeReviewEngine {
    private static final Logger logger = LoggerFactory.getLogger(CodeReviewEngine.class);

    private final ReviewConfig config;
    private final AiClient aiClient;
    private final CodeChunker chunker;
    private final ReviewCache cache;

    public CodeReviewEngine(ReviewConfig config) {
        this(config, new FileReviewCache());
    }

    public CodeReviewEngine(ReviewConfig config, ReviewCache cache) {
        this.config = config;
        this.aiClient = new AiClient(config);
        this.chunker = new CodeChunker(config.getAi().getChunkSize());
        this.cache = cache;
    }

    public ReviewResult review(PullRequestInfo prInfo, List<FileChange> fileChanges) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting review for PR #{}: {}", prInfo.getNumber(), prInfo.getTitle());

        List<ReviewFinding> allFindings = new ArrayList<>();
        String repoOwner = prInfo.getRepositoryOwner();
        String repoName = prInfo.getRepositoryName();
        int prNumber = prInfo.getNumber();

        for (FileChange file : fileChanges) {
            if (file.getChangeType() == FileChange.ChangeType.DELETED) {
                logger.info("Skipping deleted file: {}", file.getFileName());
                continue;
            }
            if (file.getLanguage() == ProgrammingLanguage.UNKNOWN) {
                logger.info("Skipping file with unknown language: {}", file.getFileName());
                continue;
            }
            allFindings.addAll(reviewFileWithCache(repoOwner, repoName, prNumber, file));
        }

        String summary = generateSummary(prInfo, allFindings);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Review completed in {} ms, found {} issues", duration, allFindings.size());

        return new ReviewResult(
            prInfo.getRepository(),
            prInfo.getNumber(),
            prInfo.getTitle(),
            allFindings,
            summary,
            duration
        );
    }

    private List<ReviewFinding> reviewFileWithCache(String repoOwner, String repoName, int prNumber, FileChange file) {
        String fileHash = ((FileReviewCache) cache).computeHash(file.getContent());

        CachedFileReview cached = cache.get(repoOwner, repoName, prNumber, file.getFileName(), fileHash);
        if (cached != null) {
            logger.info("Using cached review for file: {}", file.getFileName());
            return cached.getFindings();
        }

        List<ReviewFinding> findings = reviewFile(file);

        cache.put(repoOwner, repoName, prNumber, file.getFileName(), fileHash, findings);

        return findings;
    }

    private List<ReviewFinding> reviewFile(FileChange file) {
        List<ReviewFinding> findings = new ArrayList<>();
        logger.info("Reviewing file: {}", file.getFileName());

        String prompt = PromptTemplates.buildFileReviewPrompt(
            file.getFileName(),
            file.getLanguage(),
            file.getContent(),
            file.getDiff()
        );

        JsonNode response = aiClient.generateJson(prompt);
        logger.info("AI response: {}", response);

        if (response != null && response.has("findings")) {
            JsonNode findingsNode = response.get("findings");
            logger.info("Findings node type: {}, size: {}",
                findingsNode.getNodeType(),
                findingsNode.isArray() ? findingsNode.size() : "not array");

            if (findingsNode.isArray()) {
                for (JsonNode findingNode : findingsNode) {
                    try {
                        ReviewFinding finding = parseFinding(findingNode, file.getFileName());
                        findings.add(finding);
                        logger.info("Parsed finding: {}", finding.getTitle());
                    } catch (Exception e) {
                        logger.error("Failed to parse finding: {}", findingNode, e);
                    }
                }
            }
        }

        logger.info("Found {} findings for file {}", findings.size(), file.getFileName());
        return findings;
    }

    private ReviewFinding parseFinding(JsonNode node, String fileName) {
        ReviewCategory category = parseCategory(node.path("category").asText("BUG"));
        Severity severity = parseSeverity(node.path("severity").asText("MEDIUM"));
        Integer lineNumber = node.has("lineNumber") && !node.path("lineNumber").isNull()
            ? node.path("lineNumber").asInt() : null;
        String title = node.path("title").asText("");
        String description = node.path("description").asText("");
        String suggestion = node.path("suggestion").asText("");
        String codeSnippet = node.has("codeSnippet") ? node.path("codeSnippet").asText("") : null;

        return new ReviewFinding(category, severity, fileName, lineNumber, title,
                                 description, suggestion, codeSnippet);
    }

    private ReviewCategory parseCategory(String text) {
        try {
            return ReviewCategory.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ReviewCategory.BUG;
        }
    }

    private Severity parseSeverity(String text) {
        try {
            return Severity.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Severity.MEDIUM;
        }
    }

    private String generateSummary(PullRequestInfo prInfo, List<ReviewFinding> findings) {
        StringBuilder findingsSummary = new StringBuilder();
        for (Severity s : Severity.values()) {
            long count = findings.stream().filter(f -> f.getSeverity() == s).count();
            if (count > 0) {
                findingsSummary.append(String.format("- %s: %d issues\n", s.getDisplayName(), count));
            }
        }

        String prompt = PromptTemplates.buildSummaryPrompt(
            prInfo.getTitle(),
            prInfo.getBody(),
            findingsSummary.toString()
        );

        return aiClient.generate(prompt);
    }
}
