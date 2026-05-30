package com.ai.pr.reviewer.review;

import com.ai.pr.reviewer.config.ReviewCategory;
import com.ai.pr.reviewer.config.Severity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 完整审查结果
 */
public class ReviewResult {
    private final String repository;
    private final int prNumber;
    private final String prTitle;
    private final List<ReviewFinding> findings;
    private final String summary;
    private final long reviewDurationMs;

    public ReviewResult(String repository, int prNumber, String prTitle,
                       List<ReviewFinding> findings, String summary,
                       long reviewDurationMs) {
        this.repository = repository;
        this.prNumber = prNumber;
        this.prTitle = prTitle;
        this.findings = findings;
        this.summary = summary;
        this.reviewDurationMs = reviewDurationMs;
    }

    public String getRepository() { return repository; }
    public int getPrNumber() { return prNumber; }
    public String getPrTitle() { return prTitle; }
    public List<ReviewFinding> getFindings() { return findings; }
    public String getSummary() { return summary; }
    public long getReviewDurationMs() { return reviewDurationMs; }

    public Map<Severity, Integer> countBySeverity() {
        Map<Severity, Integer> counts = new EnumMap<>(Severity.class);
        for (Severity s : Severity.values()) {
            counts.put(s, 0);
        }
        for (ReviewFinding finding : findings) {
            counts.merge(finding.getSeverity(), 1, Integer::sum);
        }
        return counts;
    }

    public Map<ReviewCategory, Integer> countByCategory() {
        Map<ReviewCategory, Integer> counts = new EnumMap<>(ReviewCategory.class);
        for (ReviewCategory c : ReviewCategory.values()) {
            counts.put(c, 0);
        }
        for (ReviewFinding finding : findings) {
            counts.merge(finding.getCategory(), 1, Integer::sum);
        }
        return counts;
    }

    public List<ReviewFinding> getFindingsBySeverity(Severity severity) {
        return findings.stream()
            .filter(f -> f.getSeverity() == severity)
            .toList();
    }

    public boolean hasCriticalIssues() {
        return findings.stream()
            .anyMatch(f -> f.getSeverity() == Severity.CRITICAL);
    }

    public int getTotalIssues() {
        return findings.size();
    }
}
