package com.ai.pr.reviewer.cache;

import com.ai.pr.reviewer.review.ReviewFinding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 本地文件缓存实现
 */
public class FileReviewCache implements ReviewCache {
    private static final Logger logger = LoggerFactory.getLogger(FileReviewCache.class);

    private final Path cacheDir;
    private final ObjectMapper objectMapper;

    public FileReviewCache() {
        this(Paths.get(System.getProperty("user.home"), ".pr-reviewer", "cache"));
    }

    public FileReviewCache(Path cacheDir) {
        this.cacheDir = cacheDir;
        this.objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

        try {
            Files.createDirectories(cacheDir);
            logger.info("Cache directory initialized: {}", cacheDir);
        } catch (IOException e) {
            logger.warn("Failed to create cache directory: {}", cacheDir, e);
        }
    }

    @Override
    public CachedFileReview get(String repoOwner, String repoName, int prNumber, String filePath, String fileHash) {
        Path cacheFile = getCacheFilePath(repoOwner, repoName, prNumber, filePath, fileHash);

        if (!Files.exists(cacheFile)) {
            logger.debug("Cache miss: {}", cacheFile);
            return null;
        }

        try {
            String json = Files.readString(cacheFile, StandardCharsets.UTF_8);
            CachedFileReview cached = objectMapper.readValue(json, CachedFileReview.class);
            logger.info("Cache hit: {}", cacheFile);
            return cached;
        } catch (IOException e) {
            logger.warn("Failed to read cache: {}", cacheFile, e);
            return null;
        }
    }

    @Override
    public void put(String repoOwner, String repoName, int prNumber, String filePath, String fileHash, List<ReviewFinding> findings) {
        CachedFileReview cached = new CachedFileReview(
            repoOwner, repoName, prNumber, filePath, fileHash, findings
        );

        Path cacheFile = getCacheFilePath(repoOwner, repoName, prNumber, filePath, fileHash);

        try {
            Files.createDirectories(cacheFile.getParent());
            String json = objectMapper.writeValueAsString(cached);
            Files.writeString(cacheFile, json, StandardCharsets.UTF_8);
            logger.info("Cached: {}", cacheFile);
        } catch (IOException e) {
            logger.warn("Failed to write cache: {}", cacheFile, e);
        }
    }

    @Override
    public void clear(String repoOwner, String repoName, int prNumber) {
        Path prCacheDir = getPRCacheDir(repoOwner, repoName, prNumber);
        if (Files.exists(prCacheDir)) {
            try {
                Files.walk(prCacheDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            logger.warn("Failed to delete cache file: {}", file, e);
                        }
                    });
                logger.info("Cache cleared for PR #{} in {}/{}", prNumber, repoOwner, repoName);
            } catch (IOException e) {
                logger.warn("Failed to clear cache: {}", prCacheDir, e);
            }
        }
    }

    @Override
    public boolean has(String repoOwner, String repoName, int prNumber, String filePath, String fileHash) {
        return Files.exists(getCacheFilePath(repoOwner, repoName, prNumber, filePath, fileHash));
    }

    public String computeHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private Path getCacheFilePath(String repoOwner, String repoName, int prNumber, String filePath, String fileHash) {
        Path prDir = getPRCacheDir(repoOwner, repoName, prNumber);
        int hashLength = Math.min(16, fileHash.length());
        String safeFileName = sanitizeFileName(filePath) + "_" + fileHash.substring(0, hashLength) + ".json";
        return prDir.resolve(safeFileName);
    }

    private Path getPRCacheDir(String repoOwner, String repoName, int prNumber) {
        return cacheDir.resolve(repoOwner).resolve(repoName).resolve("pr-" + prNumber);
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
