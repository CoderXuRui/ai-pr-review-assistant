## ADDED Requirements

### Requirement: Pull Request fetching
系统 SHALL 通过 GitHub API 获取指定 PR 的详细信息，包括标题、描述、作者、修改文件列表。

#### Scenario: Fetch public PR
- **WHEN** 用户提供有效的 GitHub repository 和 PR number
- **THEN** 系统成功获取 PR 元数据（标题、描述、状态、文件列表）

#### Scenario: PR not found
- **WHEN** 指定的 PR 不存在或用户无权限访问
- **THEN** 系统返回清晰的错误信息

### Requirement: Code diff parsing
系统 SHALL 解析 PR 的代码差异，识别每个文件的变更类型（新增、修改、删除）及具体变更内容。

#### Scenario: Parse added file
- **WHEN** PR 包含新增文件
- **THEN** 系统解析完整文件内容并标记为 ADDED

#### Scenario: Parse modified file
- **WHEN** PR 包含修改文件
- **THEN** 系统解析差异，包括变更行号和变更前后内容

#### Scenario: Parse deleted file
- **WHEN** PR 包含删除文件
- **THEN** 系统记录文件删除并标记为 DELETED

### Requirement: Language detection
系统 SHALL 根据文件扩展名自动检测每个变更文件的编程语言。

#### Scenario: Detect Python file
- **WHEN** 文件扩展名为 .py
- **THEN** 系统识别语言为 Python

#### Scenario: Detect JavaScript/TypeScript
- **WHEN** 文件扩展名为 .js / .jsx / .ts / .tsx
- **THEN** 系统识别语言为 JavaScript/TypeScript

### Requirement: Large file handling
系统 SHALL 支持配置文件大小限制，对超出限制的文件提供跳过或截断选项。

#### Scenario: Skip large file
- **WHEN** 文件大小超过配置阈值
- **THEN** 系统记录跳过该文件并在报告中标注
