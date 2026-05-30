## Context

这是一个全新的 AI PR Review 助手项目。当前 GitHub PR 审查流程主要依赖人工，存在以下痛点：
- 审查效率低，大型 PR 难以全面覆盖
- 审查标准不统一，不同审查者关注点不同
- 常见问题（如安全漏洞、性能隐患）容易漏检
- 缺乏结构化的审查意见记录

项目将采用 Java 开发（企业级生态成熟，团队更熟悉）。

## Goals / Non-Goals

**Goals:**
- 自动化分析 GitHub PR 代码变更
- 生成结构化审查意见（Bug、安全、性能、代码规范）
- 支持多语言项目（Java、Python、JavaScript/TypeScript、Go 等）
- 提供可配置的审查规则系统
- 支持 PR 评论和 Markdown 报告两种输出格式

**Non-Goals:**
- 不替代人工审查，而是作为辅助工具
- 不支持 CI/CD 流水线集成（第一阶段）
- 不实现自定义 AI 模型训练
- 不支持 GitLab、Bitbucket 等其他平台（第一阶段）

## Decisions

### 1. 技术栈选择
- **语言**: Java 17+
- **构建工具**: Maven
- **AI SDK**: LangChain4j + Anthropic Claude（推荐 Claude 3.5 Sonnet）
- **GitHub API**: Hub4j (GitHub API for Java) / Kohsuke GitHub API
- **配置管理**: Spring Boot Configuration (YAML) 
- **CLI**: picocli
- **JSON/YAML**: Jackson

**替代方案**: Python + Anthropic SDK
**决策理由**: 团队对 Java 更熟悉，企业级项目兼容性更好。

### 2. 架构模式
- **模块化架构**: 核心引擎 + 插件化语言支持
- **流程**: PR 拉取 → 差异解析 → AI 分析 → 结果聚合 → 输出
- **规则系统**: 分层配置（全局默认 + 项目级别 + PR 级别）

### 3. AI 提示策略
- **分块审查**: 大文件分块处理，保证上下文质量
- **分类提示**: 不同审查维度使用专门的提示词
- **渐进式聚合**: 先逐文件审查，再生成整体总结

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| AI 分析不准确 | 提供规则配置，允许用户调整严格程度；支持人工确认 |
| API 成本过高 | 实现token估算；支持缓存不变文件；可选低成本模型 |
| 大 PR 处理慢 | 支持增量分析；提供并发选项；允许跳过特定路径 |
| 多语言支持不完善 | 插件化设计，便于社区贡献语言支持 |
