package com.ai.pr.reviewer.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReviewConfigTest {

    private ReviewConfig config;

    @BeforeEach
    void setUp() {
        config = new ReviewConfig();
    }

    @Test
    void testDefaultCategoriesEnabled() {
        assertTrue(config.isCategoryEnabled(ReviewCategory.BUG));
        assertTrue(config.isCategoryEnabled(ReviewCategory.SECURITY));
        assertTrue(config.isCategoryEnabled(ReviewCategory.PERFORMANCE));
        assertTrue(config.isCategoryEnabled(ReviewCategory.STYLE));
    }

    @Test
    void testDefaultIgnorePaths() {
        assertTrue(config.shouldIgnoreFile("node_modules/package.json"));
        assertTrue(config.shouldIgnoreFile("target/classes/test.class"));
        assertTrue(config.shouldIgnoreFile("build/test.txt"));
    }

    @Test
    void testDefaultIgnorePatterns() {
        assertTrue(config.shouldIgnoreFile("app.min.js"));
        assertTrue(config.shouldIgnoreFile("style.min.css"));
        assertTrue(config.shouldIgnoreFile("package-lock.json"));
    }

    @Test
    void testNotIgnored() {
        assertFalse(config.shouldIgnoreFile("src/main/Test.java"));
        assertFalse(config.shouldIgnoreFile("test.py"));
    }

    @Test
    void testDefaultMaxFileSize() {
        assertEquals(Integer.valueOf(100), config.getMaxFileSizeKB());
    }
}
