## ADDED Requirements

### Requirement: GitHub authentication
系统 SHALL 支持通过 GitHub token 进行身份验证。

#### Scenario: Use token from env var
- **WHEN** 环境变量 GITHUB_TOKEN 已设置
- **THEN** 系统使用该 token 进行认证

#### Scenario: Use token from config
- **WHEN** 配置文件中设置了 github.token
- **THEN** 系统使用配置中的 token

### Requirement: Post PR comment
系统 SHALL 支持将审查结果作为评论发布到 GitHub PR。

#### Scenario: Post new comment
- **WHEN** 用户选择发布评论且之前无评论
- **THEN** 系统创建新的 PR 评论

#### Scenario: Update existing comment
- **WHEN** 用户选择发布评论且之前已有评论
- **THEN** 系统更新已有评论（避免重复）

### Requirement: PR status check
系统 SHALL 支持设置 GitHub PR 状态检查（success/failure）。

#### Scenario: Set status to failure on critical issues
- **WHEN** 审查发现 Critical 级别问题
- **THEN** 系统设置 PR 状态为 failure

#### Scenario: Set status to success
- **WHEN** 审查无 Critical 问题或用户配置禁用状态检查
- **THEN** 系统设置 PR 状态为 success

### Requirement: Pull request info access
系统 SHALL 支持读取 PR 相关信息（评论、提交、状态等）。

#### Scenario: Read PR description
- **WHEN** 获取 PR 信息
- **THEN** 系统读取 PR 标题和描述作为审查上下文

#### Scenario: List existing comments
- **WHEN** 准备发布评论
- **THEN** 系统读取现有评论以判断是新建还是更新
