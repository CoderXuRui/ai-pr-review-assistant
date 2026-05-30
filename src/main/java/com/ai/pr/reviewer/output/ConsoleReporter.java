package com.ai.pr.reviewer.output;

import com.ai.pr.reviewer.config.Severity;
import com.ai.pr.reviewer.review.ReviewFinding;
import com.ai.pr.reviewer.review.ReviewResult;

import java.util.Map;

/**
 * 控制台输出
 */
public class ConsoleReporter {

    public void printSummary(ReviewResult result) {
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("           AI PR 审查报告");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println();
        System.out.printf("仓库: %s%n", result.getRepository());
        System.out.printf("PR #%d: %s%n", result.getPrNumber(), result.getPrTitle());
        System.out.printf("审查耗时: %.1f 秒%n", result.getReviewDurationMs() / 1000.0);
        System.out.println();

        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("问题统计:");
        System.out.println();

        Map<Severity, Integer> counts = result.countBySeverity();
        for (Severity s : Severity.values()) {
            int count = counts.getOrDefault(s, 0);
            System.out.printf("  %s %-8s: %d%n", s.getEmoji(), s.getDisplayName(), count);
        }
        System.out.printf("  %-10s: %d%n", "总计", result.getTotalIssues());
        System.out.println();

        if (result.hasCriticalIssues()) {
            System.out.println("⚠️  警告: 发现严重级别问题！");
            System.out.println();
        }

        if (!result.getFindings().isEmpty()) {
            System.out.println("─────────────────────────────────────────────────────");
            System.out.println("主要发现:");
            System.out.println();

            for (ReviewFinding finding : result.getFindings()) {
                if (finding.getSeverity() == Severity.CRITICAL ||
                    finding.getSeverity() == Severity.HIGH) {
                    System.out.printf("%s %s%n", finding.getSeverity().getEmoji(), finding.getTitle());
                    System.out.printf("   文件: %s%s%n",
                        finding.getFileName(),
                        finding.getLineNumber() != null ? ":" + finding.getLineNumber() : "");
                    System.out.printf("   类别: %s%n", finding.getCategory().getDisplayName());
                    System.out.println();
                }
            }
        }

        System.out.println("─────────────────────────────────────────────────────");
        System.out.println();
        System.out.println("💡 使用 --output 参数保存完整报告");
        System.out.println();
    }
}
