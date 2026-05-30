## 1. 项目初始化

- [x] 1.1 初始化 Java 项目结构（Maven/Gradle, README, LICENSE）
- [x] 1.2 配置依赖（langchain4j, hub4j/github-api, jackson, picocli, spring-boot-configuration）
- [x] 1.3 设置开发工具（SpotBugs, Checkstyle, JUnit 5, AssertJ）

## 2. 配置系统

- [x] 2.1 实现配置模型（Java Records / POJO with Jackson）
- [x] 2.2 实现配置加载器（默认值 + 项目配置文件合并）
- [x] 2.3 添加配置文件示例 .pr-reviewer.example.yml

## 3. GitHub 集成

- [x] 3.1 实现 GitHub 认证模块
- [x] 3.2 实现 PR 信息拉取功能
- [x] 3.3 实现代码差异解析功能（JGit / GitHub API diff）
- [x] 3.4 实现语言检测功能
- [x] 3.5 实现发布 PR 评论功能
- [x] 3.6 实现设置 PR 状态功能

## 4. AI 代码审查引擎

- [x] 4.1 实现 AI 客户端封装（LangChain4j + Claude API）
- [x] 4.2 设计并实现审查提示词模板
- [x] 4.3 实现分块处理逻辑（大文件支持）
- [x] 4.4 实现多维度审查（Bug、Security、Performance、Style）
- [x] 4.5 实现审查结果聚合与分类
- [x] 4.6 实现多语言支持（Java、Python、JS/TS、Go）

## 5. 输出格式化

- [x] 5.1 实现 Markdown 报告生成器
- [x] 5.2 实现 GitHub PR 评论格式生成器
- [x] 5.3 实现控制台输出
- [x] 5.4 实现文件输出功能

## 6. CLI 接口

- [x] 6.1 实现 picocli 命令行入口
- [x] 6.2 实现 review 子命令（分析 PR）
- [x] 6.3 实现 config 子命令（管理配置）
- [x] 6.4 添加命令行参数与帮助文档

## 7. 测试与文档

- [x] 7.1 编写单元测试（核心模块）
- [ ] 7.2 编写集成测试（端到端流程）
- [x] 7.3 完善 README 文档（使用说明、配置参考）
- [x] 7.4 添加示例配置与使用示例
