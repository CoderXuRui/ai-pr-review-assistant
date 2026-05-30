## ADDED Requirements

### Requirement: Configuration file support
系统 SHALL 支持通过 YAML 配置文件管理审查规则。

#### Scenario: Load default config
- **WHEN** 无项目配置文件
- **THEN** 系统加载内置默认配置

#### Scenario: Load project config
- **WHEN** 项目根目录存在 .pr-reviewer.yml 配置文件
- **THEN** 系统加载并应用项目配置，覆盖默认值

### Requirement: Rule categories configuration
系统 SHALL 支持按审查类别启用/禁用规则（Bug、Security、Performance、Style）。

#### Scenario: Disable style checks
- **WHEN** 配置文件禁用 Style 类别
- **THEN** 系统跳过代码风格检查

#### Scenario: Configure severity levels
- **WHEN** 配置文件自定义类别的严重程度
- **THEN** 系统使用自定义严重程度标记问题

### Requirement: Path ignore rules
系统 SHALL 支持配置忽略路径，跳过特定文件或目录的审查。

#### Scenario: Ignore test files
- **WHEN** 配置文件设置忽略 tests/ 目录
- **THEN** 系统跳过 tests/ 下所有文件的审查

#### Scenario: Ignore by file pattern
- **WHEN** 配置文件设置忽略 *.min.js
- **THEN** 系统跳过压缩 JavaScript 文件

### Requirement: Language-specific rules
系统 SHALL 支持为不同编程语言配置不同的规则。

#### Scenario: Python-specific rules
- **WHEN** 配置文件包含 Python 特定规则
- **THEN** 审查 Python 文件时应用这些规则

### Requirement: AI model configuration
系统 SHALL 支持配置 AI 模型参数（模型名称、温度、最大 token 数等）。

#### Scenario: Configure model
- **WHEN** 配置文件指定模型为 claude-3-5-sonnet
- **THEN** 系统使用指定模型进行审查

#### Scenario: Configure temperature
- **WHEN** 配置文件设置温度参数
- **THEN** 系统使用该温度值调用 AI
