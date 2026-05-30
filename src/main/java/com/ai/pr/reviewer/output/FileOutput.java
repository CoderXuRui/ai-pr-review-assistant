package com.ai.pr.reviewer.output;

import com.ai.pr.reviewer.review.ReviewResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件输出功能
 */
public class FileOutput {
    private static final Logger logger = LoggerFactory.getLogger(FileOutput.class);

    private final MarkdownReporter markdownReporter;

    public FileOutput() {
        this.markdownReporter = new MarkdownReporter();
    }

    public void saveMarkdownReport(ReviewResult result, String filePath) throws IOException {
        String report = markdownReporter.generateReport(result);
        writeToFile(filePath, report);
        logger.info("Markdown report saved to: {}", filePath);
    }

    public void saveGithubComment(ReviewResult result, String filePath) throws IOException {
        String comment = markdownReporter.generateGithubComment(result);
        writeToFile(filePath, comment);
        logger.info("GitHub comment saved to: {}", filePath);
    }

    private void writeToFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }
}
