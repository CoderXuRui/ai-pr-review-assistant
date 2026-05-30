package com.ai.pr.reviewer.review;

import com.ai.pr.reviewer.config.ProgrammingLanguage;
import com.ai.pr.reviewer.config.ReviewCategory;

/**
 * 审查提示词模板
 */
public class PromptTemplates {

    private PromptTemplates() {}

    public static String buildFileReviewPrompt(String fileName, ProgrammingLanguage language,
                                               String content, String diff) {
        String languageSpecific = getLanguageInstructions(language);

        return """
            # AI代码评审规则

            ## 评审角色定位

            作为资深架构师/技术专家进行代码评审，分析代码变更并提供专业的评审意见。

            ## 评审原则

            - **客观性**: 基于技术标准，避免主观偏见
            - **建设性**: 提供具体可行的改进建议
            - **教育性**: 解释问题原因和最佳实践
            - **完整性**: 覆盖功能、性能、安全、可维护性等多个维度

            ## 评审标准

            ### 问题分级

            - 🔴 **CRITICAL**: 安全漏洞、严重性能问题、数据一致性问题、线程安全问题、空指针
            - 🟡 **HIGH**: 代码质量问题、潜在性能隐患、可维护性问题
            - 🟠 **MEDIUM**: 一般代码质量问题、最佳实践建议
            - 🟢 **LOW**: 代码风格改进、小的优化建议

            ### 评审维度检查清单

            #### 1. 代码质量
            - [ ] 代码逻辑清晰，易于理解
            - [ ] 遵循项目的编码规范和命名约定
            - [ ] 方法长度适中，单一职责原则
            - [ ] 没有明显的性能问题或算法缺陷
            - [ ] 错误处理完整且得当
            - [ ] 注释清晰且必要，避免过度注释

            #### 2. 安全性
            - [ ] 输入验证完整，包括参数校验和边界检查
            - [ ] 权限控制正确，遵循最小权限原则
            - [ ] 敏感数据正确加密和脱敏
            - [ ] SQL注入防护措施到位
            - [ ] XSS和CSRF防护
            - [ ] 日志记录不包含敏感信息
            - [ ] 依赖库安全性检查
            - [ ] 硬编码的密钥或密码

            #### 3. 性能
            - [ ] N+1查询模式
            - [ ] 循环中的昂贵操作
            - [ ] 不必要的计算
            - [ ] 内存使用效率问题
            - [ ] 未使用的代码
            - [ ] 低效的算法或数据结构

            #### 4. 可维护性
            - [ ] 类和方法职责单一，高内聚低耦合
            - [ ] 适当的设计模式使用
            - [ ] 合理的变量和方法命名，见名知意
            - [ ] 代码复用性好，避免重复代码
            - [ ] 单元测试覆盖率充足
            - [ ] 易于扩展和修改

            ---

            ## 待评审代码

            文件: ${fileName}
            语言: ${language}

            完整代码:
            ```${languageExtension}
            ${content}
            ```

            变更内容 (diff):
            ```diff
            ${diff}
            ```

            ${languageSpecific}

            ---

            ## 评审输出格式

            请以 JSON 格式返回结果，格式如下：

            {
                "findings": [
                    {
                        "severity": "CRITICAL|HIGH|MEDIUM|LOW",
                        "category": "BUG|SECURITY|PERFORMANCE|STYLE",
                        "lineNumber": 行号或 null,
                        "title": "简短标题",
                        "description": "详细描述",
                        "suggestion": "修复建议",
                        "codeSnippet": "相关代码片段（可选）"
                    }
                ],
                "summary": "文件审查摘要"
            }

            只返回 JSON，不要包含其他说明文字。
            """
            .replace("${fileName}", fileName)
            .replace("${language}", language.getDisplayName())
            .replace("${languageExtension}", getLanguageExtension(language))
            .replace("${content}", content != null ? content : "")
            .replace("${diff}", diff != null ? diff : "")
            .replace("${languageSpecific}", languageSpecific);
    }

    public static String buildSummaryPrompt(String prTitle, String prBody, String findingsSummary) {
        return """
            # PR 审查总结

            请为以下 PR 审查结果生成一个总体摘要：

            PR 标题: ${prTitle}
            PR 描述: ${prBody}

            审查发现摘要:
            ${findingsSummary}

            请提供：
            1. 整体评价
            2. 最需要关注的问题
            3. 改进建议
            4. 可以合并的条件（如果适用）

            以 Markdown 格式返回，不超过 1000 字。
            """
            .replace("${prTitle}", prTitle)
            .replace("${prBody}", prBody != null ? prBody : "(无描述)")
            .replace("${findingsSummary}", findingsSummary);
    }

    private static String getLanguageInstructions(ProgrammingLanguage language) {
        return switch (language) {
            case JAVA -> """
                ## Java 特定检查
                - 正确使用 equals/hashCode
                - Stream API 使用是否恰当
                - 异常处理是否符合最佳实践
                - 资源管理（try-with-resources）
                - 注解使用是否正确
                - 线程安全考虑充分
                - 空指针问题处理妥当，避免NPE
                - 合理使用Java 8+新特性
                - 遵循Spring框架最佳实践
                """;
            case PYTHON -> """
                ## Python 特定检查
                - 符合 PEP 8 风格
                - 类型提示的使用（如果适用）
                - 正确处理可变默认参数
                - 异常处理
                - Pythonic 的写法
                """;
            case JAVASCRIPT, TYPESCRIPT -> """
                ## JavaScript/TypeScript 特定检查
                - 异步代码处理是否正确
                - 类型安全（TypeScript）
                - React/Vue 等框架最佳实践
                - 错误处理
                - 避免常见陷阱
                - XSS防护
                """;
            case GO -> """
                ## Go 特定检查
                - 错误处理模式
                - goroutine 安全
                - 接口使用是否恰当
                - 符合 Go 惯用写法
                """;
            default -> "";
        };
    }

    private static String getLanguageExtension(ProgrammingLanguage language) {
        return switch (language) {
            case JAVA -> "java";
            case PYTHON -> "python";
            case JAVASCRIPT -> "javascript";
            case TYPESCRIPT -> "typescript";
            case GO -> "go";
            case RUST -> "rust";
            case KOTLIN -> "kotlin";
            case UNKNOWN -> "text";
        };
    }
}
