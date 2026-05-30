package com.ai.pr.reviewer.review;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class CodeChunkerTest {

    @Test
    void testChunkCode_SmallCode() {
        CodeChunker chunker = new CodeChunker(1000);
        String code = "public class Test {\n    public void test() {\n    }\n}";
        List<CodeChunker.Chunk> chunks = chunker.chunkCode(code);
        assertEquals(1, chunks.size());
    }

    @Test
    void testChunkCode_Empty() {
        CodeChunker chunker = new CodeChunker(1000);
        List<CodeChunker.Chunk> chunks = chunker.chunkCode(null);
        assertTrue(chunks.isEmpty());
        chunks = chunker.chunkCode("");
        assertTrue(chunks.isEmpty());
    }

    @Test
    void testChunkLineNumbers() {
        CodeChunker chunker = new CodeChunker(50);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 20; i++) {
            sb.append("line ").append(i).append("\n");
        }
        List<CodeChunker.Chunk> chunks = chunker.chunkCode(sb.toString());
        assertTrue(chunks.size() > 1);
        assertEquals(1, chunks.get(0).startLine());
    }
}
