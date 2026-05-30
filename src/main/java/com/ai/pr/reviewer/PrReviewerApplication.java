package com.ai.pr.reviewer;

import picocli.CommandLine;

/**
 * AI PR Review Assistant - Main Application Entry Point
 * <p>
 * AI驱动的GitHub PR代码审查助手，使用Claude API进行智能代码分析
 */
@CommandLine.Command(
    name = "pr-reviewer",
    mixinStandardHelpOptions = true,
    version = "1.0.0",
    description = "AI-powered GitHub PR code review assistant using Claude API",
    subcommands = {
        com.ai.pr.reviewer.cli.ReviewCommand.class,
        com.ai.pr.reviewer.cli.ConfigCommand.class
    }
)
public class PrReviewerApplication implements Runnable {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PrReviewerApplication()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        // Default behavior: show help
        System.out.println("Use 'pr-reviewer --help' for usage information");
        System.out.println("Available commands: review, config");
    }
}
