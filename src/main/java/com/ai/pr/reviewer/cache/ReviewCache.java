package com.ai.pr.reviewer.cache;

import com.ai.pr.reviewer.review.ReviewFinding;

import java.util.List;

/**
 * 审查结果缓存接口
 */
public interface ReviewCache {

    /**
     * 获取缓存的文件审查结果
     */
    CachedFileReview get(String repoOwner, String repoName, int prNumber, String filePath, String fileHash);

    /**
     * 缓存文件审查结果
     */
    void put(String repoOwner, String repoName, int prNumber, String filePath, String fileHash, List<ReviewFinding> findings);

    /**
     * 清除指定 PR 的缓存
     */
    void clear(String repoOwner, String repoName, int prNumber);

    /**
     * 检查缓存是否存在
     */
    boolean has(String repoOwner, String repoName, int prNumber, String filePath, String fileHash);
}
