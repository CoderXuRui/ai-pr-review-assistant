package com.ai.pr.reviewer.output;

import com.ai.pr.reviewer.config.ReviewCategory;
import com.ai.pr.reviewer.config.Severity;
import com.ai.pr.reviewer.review.ReviewFinding;
import com.ai.pr.reviewer.review.ReviewResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * Markdown 报告生成器
 */
public class MarkdownReporter {

    public String generateReport(ReviewResult result) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("<!-- AI PR Review Assistant -->");
        pw.println();
        pw.println("# AI PR 审查报告");
        pw.println();
        pw.printf("**仓库**: %s  \n", result.getRepository());
        pw.printf("**PR #%d**: %s  \n", result.getPrNumber(), result.getPrTitle());
        pw.printf("**审查耗时**: %.1f 秒  \n", result.getReviewDurationMs() / 1000.0);
        pw.println();

        printSummary(pw, result);
        pw.println();
        printFindingsBySeverity(pw, result);
        pw.println();
        printFindingsByCategory(pw, result);
        pw.println();
        pw.println("---");
        pw.println("## 总体摘要");
        pw.println();
        pw.println(result.getSummary());

        return sw.toString();
    }

    private void printSummary(PrintWriter pw, ReviewResult result) {
        pw.println("## 审查摘要");
        pw.println();
        pw.println("| 严重程度 | 数量 |");
        pw.println("|----------|------|");

        Map<Severity, Integer> severityCounts = result.countBySeverity();
        for (Severity s : Severity.values()) {
            int count = severityCounts.getOrDefault(s, 0);
            pw.printf("| %s %s | %d |\n", s.getEmoji(), s.getDisplayName(), count);
        }
        pw.println();
        pw.printf("**总计**: %d 个问题  \n", result.getTotalIssues());

        if (result.hasCriticalIssues()) {
            pw.println();
            pw.println("⚠️ **注意**: 发现严重级别问题！");
        }
    }

    private void printFindingsBySeverity(PrintWriter pw, ReviewResult result) {
        pw.println("## 按严重程度分类");
        pw.println();

        for (Severity s : Severity.values()) {
            List<ReviewFinding> findings = result.getFindingsBySeverity(s);
            if (findings.isEmpty()) {
                continue;
            }

            pw.printf("### %s %s (%d)\n", s.getEmoji(), s.getDisplayName(), findings.size());
            pw.println();

            for (ReviewFinding finding : findings) {
                printFinding(pw, finding);
            }
        }
    }

    private void printFindingsByCategory(PrintWriter pw, ReviewResult result) {
        pw.println("## 按类别分类");
        pw.println();

        Map<ReviewCategory, Integer> categoryCounts = result.countByCategory();
        for (ReviewCategory category : ReviewCategory.values()) {
            int count = categoryCounts.getOrDefault(category, 0);
            if (count == 0) {
                continue;
            }

            List<ReviewFinding> findings = result.getFindings().stream()
                .filter(f -> f.getCategory() == category)
                .toList();

            pw.printf("### %s (%d)\n", category.getDisplayName(), count);
            pw.println();

            for (ReviewFinding finding : findings) {
                printFinding(pw, finding);
            }
        }
    }

    private void printFinding(PrintWriter pw, ReviewFinding finding) {
        pw.printf("#### %s %s\n", finding.getSeverity().getEmoji(), finding.getTitle());
        pw.println();

        pw.printf("**文件**: `%s`", finding.getFileName());
        if (finding.getLineNumber() != null) {
            pw.printf(" L%d", finding.getLineNumber());
        }
        pw.println();

        pw.printf("**类别**: %s\n", finding.getCategory().getDisplayName());
        pw.println();

        pw.println("**描述**:");
        pw.println();
        pw.println(finding.getDescription());
        pw.println();

        if (finding.getSuggestion() != null && !finding.getSuggestion().isBlank()) {
            pw.println("**建议**:");
            pw.println();
            pw.println(finding.getSuggestion());
            pw.println();
        }

        if (finding.getCodeSnippet() != null && !finding.getCodeSnippet().isBlank()) {
            pw.println("**代码片段**:");
            pw.println();
            pw.println("```");
            pw.println(finding.getCodeSnippet());
            pw.println("```");
            pw.println();
        }

        pw.println("---");
        pw.println();
    }

    public String generateGithubComment(ReviewResult result) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("<!-- AI PR Review Assistant -->");
        pw.println();
        pw.println("# 🤖 AI PR 审查");
        pw.println();

        Map<Severity, Integer> severityCounts = result.countBySeverity();
        pw.println("| 🔴 严重 | 🟠 高 | 🟡 中 | 🟢 低 | 总计 |");
        pw.println("|---------|-------|-------|-------|------|");
        pw.printf("| %d | %d | %d | %d | %d |\n",
            severityCounts.getOrDefault(Severity.CRITICAL, 0),
            severityCounts.getOrDefault(Severity.HIGH, 0),
            severityCounts.getOrDefault(Severity.MEDIUM, 0),
            severityCounts.getOrDefault(Severity.LOW, 0),
            result.getTotalIssues());
        pw.println();

        if (result.hasCriticalIssues()) {
            pw.println("⚠️ **发现严重问题，建议先修复再合并**");
            pw.println();
        }

        pw.println("<details>");
        pw.println("<summary>查看详细报告</summary>");
        pw.println();

        for (Severity s : Severity.values()) {
            List<ReviewFinding> findings = result.getFindingsBySeverity(s);
            if (findings.isEmpty()) continue;

            pw.printf("### %s %s\n", s.getEmoji(), s.getDisplayName());
            pw.println();

            for (ReviewFinding finding : findings) {
                String fileLink = getFileLink(finding.getFileName(), finding.getLineNumber(), result);
                pw.printf("- **%s** [%s](%s)\n", finding.getTitle(), finding.getFileName(), fileLink);
            }
            pw.println();
        }

        pw.println("---");
        pw.println();
        pw.println("## 摘要");
        pw.println();
        pw.println(result.getSummary());
        pw.println();
        pw.println("</details>");

        return sw.toString();
    }

    private String getFileLink(String fileName, Integer lineNumber, ReviewResult result) {
        String repo = result.getRepository();
        return String.format("https://github.com/%s/blob/HEAD/%s%s",
            repo, fileName, lineNumber != null ? "#L" + lineNumber : "");
    }
}
