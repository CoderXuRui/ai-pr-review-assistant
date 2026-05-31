package com.ai.pr.reviewer.cache;

import com.ai.pr.reviewer.config.ReviewCategory;
import com.ai.pr.reviewer.config.Severity;
import com.ai.pr.reviewer.review.ReviewFinding;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CachedFileReviewTest {

    @Test
    void testConstructor() {
        String repoOwner = "owner";
        String repoName = "repo";
        int prNumber = 123;
        String filePath = "Test.java";
        String fileHash = "hash123";
        List<ReviewFinding> findings = new ArrayList<>();

        findings.add(new ReviewFinding(
            ReviewCategory.SECURITY, Severity.CRITICAL, filePath, 5,
            "Security Issue", "Description", "Fix", null
        ));

        CachedFileReview cached = new CachedFileReview(
            repoOwner, repoName, prNumber, filePath, fileHash, findings
        );

        assertEquals(repoOwner, cached.getRepoOwner());
        assertEquals(repoName, cached.getRepoName());
        assertEquals(prNumber, cached.getPrNumber());
        assertEquals(filePath, cached.getFilePath());
        assertEquals(fileHash, cached.getFileHash());
        assertEquals(1, cached.getFindings().size());
        assertTrue(cached.getCachedAt() > 0);
    }

    @Test
    void testDefaultConstructor() {
        CachedFileReview cached = new CachedFileReview();
        assertNull(cached.getRepoOwner());
        assertNull(cached.getFindings());
    }

    @Test
    void testSetters() {
        CachedFileReview cached = new CachedFileReview();

        cached.setRepoOwner("new-owner");
        cached.setRepoName("new-repo");
        cached.setPrNumber(456);
        cached.setFilePath("New.java");
        cached.setFileHash("newhash");
        cached.setFindings(new ArrayList<>());
        cached.setCachedAt(12345L);

        assertEquals("new-owner", cached.getRepoOwner());
        assertEquals("new-repo", cached.getRepoName());
        assertEquals(456, cached.getPrNumber());
        assertEquals("New.java", cached.getFilePath());
        assertEquals("newhash", cached.getFileHash());
        assertNotNull(cached.getFindings());
        assertEquals(12345L, cached.getCachedAt());
    }
}
