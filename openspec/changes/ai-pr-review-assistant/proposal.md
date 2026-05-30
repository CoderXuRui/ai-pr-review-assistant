## Why

现代软件开发流程中，PR 代码审查是保证代码质量的关键环节。但人工审查存在效率低、覆盖不全面、标准不统一等问题。通过 AI 辅助 PR 审查，可以自动化发现常见问题（bug、安全隐患、性能问题），提高审查效率和一致性，让开发者专注于更有价值的设计和架构讨论。

## What Changes

- 新增 GitHub PR 代码变更自动分析功能
- 新增多维度审查能力（Bug 检测、安全问题、性能隐患、代码规范）
- 新增可配置审查规则系统
- 新增多语言项目支持
- 新增 PR 评论和 Markdown 报告两种输出格式

## Capabilities

### New Capabilities
- `pr-analysis`: PR 变更拉取与代码差异解析
- `code-review`: 多维度 AI 代码审查引擎
- `rules-config`: 审查规则配置与管理
- `output-formatter`: PR 评论与 Markdown 报告生成
- `github-integration`: GitHub API 集成与交互

### Modified Capabilities

## Impact

- 需要新增 AI 模型依赖（如 Claude API）
- 需要 GitHub API 权限用于 PR 交互
- 新增配置文件用于管理审查规则
