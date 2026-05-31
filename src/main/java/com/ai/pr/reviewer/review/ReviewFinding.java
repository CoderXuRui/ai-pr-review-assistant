package com.ai.pr.reviewer.review;

import com.ai.pr.reviewer.config.ReviewCategory;
import com.ai.pr.reviewer.config.Severity;

/**
 * 单个审查发现
 */
public class ReviewFinding {
    private ReviewCategory category;
    private Severity severity;
    private String fileName;
    private Integer lineNumber;
    private String title;
    private String description;
    private String suggestion;
    private String codeSnippet;

    public ReviewFinding() {}

    public ReviewFinding(ReviewCategory category, Severity severity, String fileName,
                        Integer lineNumber, String title, String description,
                        String suggestion, String codeSnippet) {
        this.category = category;
        this.severity = severity;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.title = title;
        this.description = description;
        this.suggestion = suggestion;
        this.codeSnippet = codeSnippet;
    }

    public ReviewCategory getCategory() { return category; }
    public void setCategory(ReviewCategory category) { this.category = category; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Integer getLineNumber() { return lineNumber; }
    public void setLineNumber(Integer lineNumber) { this.lineNumber = lineNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
}
