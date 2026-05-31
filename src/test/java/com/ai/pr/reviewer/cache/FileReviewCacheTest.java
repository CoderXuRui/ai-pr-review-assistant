package com.ai.pr.reviewer.cache;

import com.ai.pr.reviewer.config.ReviewCategory;
import com.ai.pr.reviewer.config.Severity;
import com.ai.pr.reviewer.review.ReviewFinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileReviewCacheTest {

    @TempDir
    Path tempDir;

    private FileReviewCache cache;

    @BeforeEach
    void setUp() {
        cache = new FileReviewCache(tempDir);
    }

    @Test
    void testPutAndGet() {
        String repoOwner = "test-owner";
        String repoName = "test-repo";
        int prNumber = 123;
        String filePath = "src/Main.java";
        String fileHash = "abc123";

        List<ReviewFinding> findings = new ArrayList<>();
        findings.add(new ReviewFinding(
            ReviewCategory.BUG, Severity.HIGH, filePath, 10,
            "Test Bug", "Description", "Suggestion", "code"
        ));

        cache.put(repoOwner, repoName, prNumber, filePath, fileHash, findings);

        CachedFileReview cached = cache.get(repoOwner, repoName, prNumber, filePath, fileHash);

        assertNotNull(cached);
        assertEquals(repoOwner, cached.getRepoOwner());
        assertEquals(repoName, cached.getRepoName());
        assertEquals(prNumber, cached.getPrNumber());
        assertEquals(filePath, cached.getFilePath());
        assertEquals(fileHash, cached.getFileHash());
        assertEquals(1, cached.getFindings().size());
    }

    @Test
    void testHas() {
        String repoOwner = "test-owner";
        String repoName = "test-repo";
        int prNumber = 123;
        String filePath = "src/Test.java";
        String fileHash = "def456";

        assertFalse(cache.has(repoOwner, repoName, prNumber, filePath, fileHash));

        cache.put(repoOwner, repoName, prNumber, filePath, fileHash, new ArrayList<>());

        assertTrue(cache.has(repoOwner, repoName, prNumber, filePath, fileHash));
    }

    @Test
    void testClear() {
        String repoOwner = "test-owner";
        String repoName = "test-repo";
        int prNumber = 123;

        cache.put(repoOwner, repoName, prNumber, "file1.java", "hash1", new ArrayList<>());
        cache.put(repoOwner, repoName, prNumber, "file2.java", "hash2", new ArrayList<>());

        assertTrue(cache.has(repoOwner, repoName, prNumber, "file1.java", "hash1"));
        assertTrue(cache.has(repoOwner, repoName, prNumber, "file2.java", "hash2"));

        cache.clear(repoOwner, repoName, prNumber);

        assertFalse(cache.has(repoOwner, repoName, prNumber, "file1.java", "hash1"));
        assertFalse(cache.has(repoOwner, repoName, prNumber, "file2.java", "hash2"));
    }

    @Test
    void testComputeHash() {
        String content1 = "public class Test {}";
        String content2 = "public class Test {}";
        String content3 = "public class Different {}";

        String hash1 = cache.computeHash(content1);
        String hash2 = cache.computeHash(content2);
        String hash3 = cache.computeHash(content3);

        assertEquals(hash1, hash2);
        assertNotEquals(hash1, hash3);
    }
}
