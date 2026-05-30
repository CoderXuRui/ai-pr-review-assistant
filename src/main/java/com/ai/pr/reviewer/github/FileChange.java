package com.ai.pr.reviewer.github;

import com.ai.pr.reviewer.config.ProgrammingLanguage;

/**
 * 文件变更记录
 */
public class FileChange {
    private final String fileName;
    private final ChangeType changeType;
    private final String content;
    private final String diff;
    private final ProgrammingLanguage language;
    private final int linesAdded;
    private final int linesRemoved;

    public enum ChangeType {
        ADDED, MODIFIED, DELETED, RENAMED
    }

    public FileChange(String fileName, ChangeType changeType, String content, String diff,
                      ProgrammingLanguage language, int linesAdded, int linesRemoved) {
        this.fileName = fileName;
        this.changeType = changeType;
        this.content = content;
        this.diff = diff;
        this.language = language;
        this.linesAdded = linesAdded;
        this.linesRemoved = linesRemoved;
    }

    public String getFileName() { return fileName; }
    public ChangeType getChangeType() { return changeType; }
    public String getContent() { return content; }
    public String getDiff() { return diff; }
    public ProgrammingLanguage getLanguage() { return language; }
    public int getLinesAdded() { return linesAdded; }
    public int getLinesRemoved() { return linesRemoved; }
}
