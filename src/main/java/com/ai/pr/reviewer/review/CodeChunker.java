package com.ai.pr.reviewer.review;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码分块工具 - 处理大文件
 */
public class CodeChunker {

    private final int chunkSize;

    public CodeChunker(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public List<Chunk> chunkCode(String code) {
        List<Chunk> chunks = new ArrayList<>();
        if (code == null || code.isBlank()) {
            return chunks;
        }

        String[] lines = code.split("\\n");
        StringBuilder currentChunk = new StringBuilder();
        int startLine = 1;
        int currentLine = 1;

        for (String line : lines) {
            if (currentChunk.length() + line.length() > chunkSize && currentChunk.length() > 0) {
                chunks.add(new Chunk(currentChunk.toString(), startLine, currentLine - 1));
                currentChunk = new StringBuilder();
                startLine = currentLine;
            }
            currentChunk.append(line).append("\n");
            currentLine++;
        }

        if (currentChunk.length() > 0) {
            chunks.add(new Chunk(currentChunk.toString(), startLine, currentLine - 1));
        }

        return chunks;
    }

    public record Chunk(String content, int startLine, int endLine) {
        public int getLineCount() {
            return endLine - startLine + 1;
        }
    }
}
