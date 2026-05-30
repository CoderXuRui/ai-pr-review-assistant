## ADDED Requirements

### Requirement: Markdown report generation
系统 SHALL 生成结构化的 Markdown 格式审查报告。

#### Scenario: Generate complete report
- **WHEN** 审查完成
- **THEN** 系统生成包含摘要、问题详情、改进建议的完整 Markdown 报告

#### Scenario: Report includes severity badges
- **WHEN** 报告包含不同严重程度的问题
- **THEN** 每个问题使用对应的徽章（🔴 Critical、🟠 High、🟡 Medium、🟢 Low）

### Requirement: GitHub PR comment format
系统 SHALL 生成适合直接发布为 GitHub PR 评论的格式。

#### Scenario: Format comment with code line links
- **WHEN** 生成 PR 评论
- **THEN** 问题引用包含可点击的文件行号链接

#### Scenario: Comment includes summary
- **WHEN** 生成 PR 评论
- **THEN** 评论开头包含审查摘要（问题统计、总体评价）

### Requirement: Output to file
系统 SHALL 支持将审查结果输出到文件。

#### Scenario: Save Markdown report
- **WHEN** 用户指定输出文件路径
- **THEN** 系统将 Markdown 报告写入指定文件

### Requirement: Console output
系统 SHALL 支持在控制台输出审查结果摘要。

#### Scenario: Print summary to console
- **WHEN** 审查完成
- **THEN** 系统在控制台打印审查摘要

### Requirement: Inline suggestion format
系统 SHALL 支持生成 GitHub suggested change 格式，便于直接应用修复。

#### Scenario: Generate suggestion block
- **WHEN** 问题有明确的修复方案
- **THEN** 系统生成 GitHub 建议变更格式的代码块
