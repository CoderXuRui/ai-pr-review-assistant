package com.ai.pr.reviewer.cli;

import com.ai.pr.reviewer.config.ConfigLoader;
import com.ai.pr.reviewer.config.ReviewConfig;
import com.ai.pr.reviewer.config.Severity;
import com.ai.pr.reviewer.github.FileChange;
import com.ai.pr.reviewer.github.GithubClient;
import com.ai.pr.reviewer.github.PullRequestInfo;
import com.ai.pr.reviewer.output.ConsoleReporter;
import com.ai.pr.reviewer.output.FileOutput;
import com.ai.pr.reviewer.output.MarkdownReporter;
import com.ai.pr.reviewer.review.CodeReviewEngine;
import com.ai.pr.reviewer.review.ReviewResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "review",
    description = "审查 GitHub Pull Request",
    mixinStandardHelpOptions = true
)
public class ReviewCommand implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(ReviewCommand.class);

    @CommandLine.Parameters(index = "0", description = "仓库: 例如 owner/repo")
    private String repository;

    @CommandLine.Parameters(index = "1", description = "PR 编号")
    private int prNumber;

    @CommandLine.Option(names = {"--post-comment"}, description = "发布评论到 PR")
    private boolean postComment = false;

    @CommandLine.Option(names = {"--set-status"}, description = "设置 PR 状态检查")
    private boolean setStatus = false;

    @CommandLine.Option(names = {"--output", "-o"}, description = "输出报告到文件")
    private String outputFile;

    @CommandLine.Option(names = {"--dry-run"}, description = "不实际发布评论或设置状态")
    private boolean dryRun = false;

    @CommandLine.Option(names = {"--config", "-c"}, description = "配置文件路径")
    private String configFile;

    @Override
    public Integer call() {
        try {
            String[] repoParts = repository.split("/", 2);
            if (repoParts.length != 2) {
                System.err.println("错误: 仓库格式应为 owner/repo");
                return 1;
            }
            String repoOwner = repoParts[0];
            String repoName = repoParts[1];

            ConfigLoader configLoader = new ConfigLoader();
            ReviewConfig config = configFile != null
                ? configLoader.loadConfigFromFile(configFile)
                : configLoader.loadConfig();

            logger.info("Fetching PR #{} from {}", prNumber, repository);

            GithubClient github = new GithubClient(config);
            PullRequestInfo prInfo = github.getPullRequestInfo(repoOwner, repoName, prNumber);
            List<FileChange> changes = github.getPullRequestChanges(repoOwner, repoName, prNumber);

            logger.info("Fetched {} changed files", changes.size());

            CodeReviewEngine engine = new CodeReviewEngine(config);
            ReviewResult result = engine.review(prInfo, changes);

            ConsoleReporter consoleReporter = new ConsoleReporter();
            consoleReporter.printSummary(result);

            if (outputFile != null) {
                FileOutput fileOutput = new FileOutput();
                fileOutput.saveMarkdownReport(result, outputFile);
                System.out.println("报告已保存到: " + outputFile);
            }

            if (!dryRun) {
                MarkdownReporter markdownReporter = new MarkdownReporter();

                if (postComment || config.getGithub().isPostComment()) {
                    logger.info("Posting comment to PR");
                    String comment = markdownReporter.generateGithubComment(result);
                    github.postComment(repoOwner, repoName, prNumber, comment);
                    System.out.println("评论已发布到 PR");
                }

                if (setStatus || config.getGithub().isSetStatusCheck()) {
                    logger.info("Setting PR status");
                    String state = result.hasCriticalIssues() ? "FAILURE" : "SUCCESS";
                    String description = String.format("AI 审查: %d 个问题", result.getTotalIssues());
                    github.setStatus(repoOwner, repoName, prInfo.getHeadSha(),
                                    state, "AI PR Review", description, null);
                    System.out.println("状态检查已设置");
                }
            }

            return result.hasCriticalIssues() ? 2 : 0;

        } catch (Exception e) {
            logger.error("Error during review", e);
            System.err.println("错误: " + e.getMessage());
            return 1;
        }
    }
}
