package com.ai.pr.reviewer.github;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PullRequestInfoTest {

    @Test
    void testRepositoryParsing() {
        PullRequestInfo pr = new PullRequestInfo(
            "CoderXuRui/ai-pr-review-assistant",
            123,
            "Test PR",
            "Body",
            "author",
            "https://github.com/...",
            "headsha",
            "basesha",
            Collections.emptyList()
        );

        assertEquals("CoderXuRui", pr.getRepositoryOwner());
        assertEquals("ai-pr-review-assistant", pr.getRepositoryName());
    }

    @Test
    void testSimpleRepository() {
        PullRequestInfo pr = new PullRequestInfo(
            "repo-only",
            1,
            "Title",
            "",
            "",
            "",
            "",
            "",
            Collections.emptyList()
        );

        assertEquals("repo-only", pr.getRepositoryOwner());
        assertEquals("repo-only", pr.getRepositoryName());
    }

    @Test
    void testGetters() {
        List<String> labels = List.of("enhancement", "bug");
        PullRequestInfo pr = new PullRequestInfo(
            "owner/repo",
            456,
            "Feature PR",
            "PR Body",
            "test-author",
            "https://github.com/owner/repo/pull/456",
            "abc123",
            "def456",
            labels
        );

        assertEquals("owner/repo", pr.getRepository());
        assertEquals(456, pr.getNumber());
        assertEquals("Feature PR", pr.getTitle());
        assertEquals("PR Body", pr.getBody());
        assertEquals("test-author", pr.getAuthor());
        assertEquals("https://github.com/owner/repo/pull/456", pr.getUrl());
        assertEquals("abc123", pr.getHeadSha());
        assertEquals("def456", pr.getBaseSha());
        assertEquals(labels, pr.getLabels());
    }
}
