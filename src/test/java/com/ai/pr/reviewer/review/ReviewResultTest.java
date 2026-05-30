package com.ai.pr.reviewer.review;

import com.ai.pr.reviewer.config.ReviewCategory;
import com.ai.pr.reviewer.config.Severity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReviewResultTest {

    @Test
    void testCountBySeverity() {
        List<ReviewFinding> findings = new ArrayList<>();
        findings.add(createFinding(Severity.CRITICAL));
        findings.add(createFinding(Severity.CRITICAL));
        findings.add(createFinding(Severity.HIGH));

        ReviewResult result = new ReviewResult(
            "test/repo", 123, "Test PR",
            findings, "Summary", 1000);

        Map<Severity, Integer> counts = result.countBySeverity();
        assertEquals(2, counts.get(Severity.CRITICAL));
        assertEquals(1, counts.get(Severity.HIGH));
    }

    @Test
    void testCountByCategory() {
        List<ReviewFinding> findings = new ArrayList<>();
        findings.add(createFinding(ReviewCategory.BUG, Severity.HIGH));
        findings.add(createFinding(ReviewCategory.SECURITY, Severity.CRITICAL));
        findings.add(createFinding(ReviewCategory.SECURITY, Severity.CRITICAL));

        ReviewResult result = new ReviewResult(
            "test/repo", 123, "Test PR",
            findings, "Summary", 1000);

        Map<ReviewCategory, Integer> counts = result.countByCategory();
        assertEquals(1, counts.get(ReviewCategory.BUG));
        assertEquals(2, counts.get(ReviewCategory.SECURITY));
    }

    @Test
    void testHasCriticalIssues() {
        List<ReviewFinding> findings = new ArrayList<>();
        findings.add(createFinding(Severity.CRITICAL));

        ReviewResult result = new ReviewResult(
            "test/repo", 123, "Test PR",
            findings, "Summary", 1000);

        assertTrue(result.hasCriticalIssues());
    }

    @Test
    void testHasNoCriticalIssues() {
        List<ReviewFinding> findings = new ArrayList<>();
        findings.add(createFinding(Severity.HIGH));
        findings.add(createFinding(Severity.MEDIUM));

        ReviewResult result = new ReviewResult(
            "test/repo", 123, "Test PR",
            findings, "Summary", 1000);

        assertFalse(result.hasCriticalIssues());
    }

    @Test
    void testGetTotalIssues() {
        List<ReviewFinding> findings = new ArrayList<>();
        findings.add(createFinding(Severity.HIGH));
        findings.add(createFinding(Severity.MEDIUM));
        findings.add(createFinding(Severity.LOW));

        ReviewResult result = new ReviewResult(
            "test/repo", 123, "Test PR",
            findings, "Summary", 1000);

        assertEquals(3, result.getTotalIssues());
    }

    private ReviewFinding createFinding(Severity severity) {
        return createFinding(ReviewCategory.BUG, severity);
    }

    private ReviewFinding createFinding(ReviewCategory category, Severity severity) {
        return new ReviewFinding(category, severity, "test.java", 42,
                               "Title", "Description", "Suggestion", null);
    }
}
