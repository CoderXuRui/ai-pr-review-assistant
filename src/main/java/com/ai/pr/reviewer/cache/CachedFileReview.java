package com.ai.pr.reviewer.cache;

import com.ai.pr.reviewer.review.ReviewFinding;

import java.util.List;

/**
 * 缓存的文件审查结果
 */
public class CachedFileReview {
    private String repoOwner;
    private String repoName;
    private int prNumber;
    private String filePath;
    private String fileHash;
    private List<ReviewFinding> findings;
    private long cachedAt;

    public CachedFileReview() {}

    public CachedFileReview(String repoOwner, String repoName, int prNumber,
                            String filePath, String fileHash, List<ReviewFinding> findings) {
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.prNumber = prNumber;
        this.filePath = filePath;
        this.fileHash = fileHash;
        this.findings = findings;
        this.cachedAt = System.currentTimeMillis();
    }

    public String getRepoOwner() { return repoOwner; }
    public void setRepoOwner(String repoOwner) { this.repoOwner = repoOwner; }

    public String getRepoName() { return repoName; }
    public void setRepoName(String repoName) { this.repoName = repoName; }

    public int getPrNumber() { return prNumber; }
    public void setPrNumber(int prNumber) { this.prNumber = prNumber; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public List<ReviewFinding> getFindings() { return findings; }
    public void setFindings(List<ReviewFinding> findings) { this.findings = findings; }

    public long getCachedAt() { return cachedAt; }
    public void setCachedAt(long cachedAt) { this.cachedAt = cachedAt; }
}
