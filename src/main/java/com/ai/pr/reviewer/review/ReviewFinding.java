package com.ai.pr.reviewer.review;

import com.ai.pr.reviewer.config.ReviewCategory;
import com.ai.pr.reviewer.config.Severity;

/**
 * 单个审查发现
 */
public class ReviewFinding {
    private final ReviewCategory category;
    private final Severity severity;
    private final String fileName;
    private final Integer lineNumber;
    private final String title;
    private final String description;
    private final String suggestion;
    private final String codeSnippet;

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
    public Severity getSeverity() { return severity; }
    public String getFileName() { return fileName; }
    public Integer getLineNumber() { return lineNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSuggestion() { return suggestion; }
    public String getCodeSnippet() { return codeSnippet; }
}
