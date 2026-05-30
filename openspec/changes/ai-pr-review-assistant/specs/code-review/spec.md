## ADDED Requirements

### Requirement: Bug detection
系统 SHALL 分析代码变更，识别潜在的 bug（如空指针引用、边界条件缺失、逻辑错误等）。

#### Scenario: Find null pointer risk
- **WHEN** 代码中存在可能导致空指针引用的变更
- **THEN** 系统标记该问题并提供修复建议

#### Scenario: Find missing error handling
- **WHEN** 代码缺少必要的异常处理
- **THEN** 系统标记该问题并建议添加错误处理

### Requirement: Security issue detection
系统 SHALL 分析代码变更，识别安全隐患（如 SQL 注入、XSS 风险、敏感信息泄露等）。

#### Scenario: Find potential injection
- **WHEN** 代码中存在未经验证的用户输入拼接
- **THEN** 系统标记为安全风险并提供安全的替代方案

#### Scenario: Find sensitive data exposure
- **WHEN** 代码可能泄露敏感信息（API keys、密码等）
- **THEN** 系统标记该问题

### Requirement: Performance issue detection
系统 SHALL 分析代码变更，识别性能隐患（如 N+1 查询、不必要的循环、低效算法等）。

#### Scenario: Find N+1 query pattern
- **WHEN** 代码中存在可能的 N+1 查询模式
- **THEN** 系统标记并建议批量查询方案

#### Scenario: Find expensive operation in loop
- **WHEN** 循环中包含昂贵的 I/O 或计算操作
- **THEN** 系统标记并建议优化

### Requirement: Code style & best practices
系统 SHALL 检查代码是否符合语言最佳实践和常见规范。

#### Scenario: Find inconsistent naming
- **WHEN** 代码命名不符合语言约定
- **THEN** 系统提供风格建议

#### Scenario: Find missing documentation
- **WHEN** 新增公共 API 缺少文档注释
- **THEN** 系统建议添加文档

### Requirement: Multi-language support
系统 SHALL 支持多种编程语言的审查，至少包括 Python、JavaScript/TypeScript、Java、Go。

#### Scenario: Review Python code
- **WHEN** 变更文件是 Python
- **THEN** 系统使用 Python 特定的审查规则

#### Scenario: Review TypeScript code
- **WHEN** 变更文件是 TypeScript
- **THEN** 系统使用 TypeScript 特定的审查规则

### Requirement: Review result aggregation
系统 SHALL 聚合所有文件的审查结果，按问题严重程度分类（Critical、High、Medium、Low）。

#### Scenario: Aggregate by severity
- **WHEN** 多个文件审查完成
- **THEN** 系统按严重程度分组展示所有问题

#### Scenario: Generate summary
- **WHEN** 所有审查完成
- **THEN** 系统生成整体总结，包括问题数量、改进建议等
