# AI PR Review Assistant

一个使用 Anthropic Claude API 的 AI 驱动 GitHub PR 代码审查助手。

## 功能特性

- 🤖 **多维度审查**: 检查 Bug、安全问题、性能问题和代码风格
- 🌍 **多语言支持**: Java, Python, JavaScript/TypeScript, Go 等
- 📝 **灵活输出**: Markdown 报告、GitHub PR 评论、控制台输出
- ⚙️ **可配置**: 自定义审查规则、忽略路径等
- 🔧 **CLI 工具**: 易于集成到 CI/CD 流程
- 🌐 **Web UI**: 提供友好的 Web 界面

## Demo 视频

🎬 **[点击查看 Demo 视频](https://www.bilibili.com/video/your-video-id)**

（视频包含完整流程演示：拉取 PR → AI 审查 → 生成结果 → 发布评论）

## 技术栈

- **Java 17+**: 主要开发语言
- **Spring Boot**: Web 框架
- **Maven**: 项目构建工具
- **LangChain4j**: AI 模型集成
- **GitHub API (kohsuke)**: GitHub 集成
- **Picocli**: 命令行界面
- **SnakeYAML**: 配置文件解析

## 原创功能说明

本项目为独立开发的原创作品，以下为核心原创功能：

1. **智能代码分块机制**: 针对大文件的自适应 token 分块算法
2. **多维度审查引擎**: Bug、安全、性能、代码风格四维一体审查
3. **配置灵活度**: 支持 YAML 配置 + 环境变量双重覆盖
4. **双模型支持**: Claude 和 DeepSeek 模型可切换
5. **优雅的 Markdown 报告生成**: 结构化的问题展示与改进建议

## 第三方库引用声明

| 库名 | 版本 | 用途 | 许可证 |
|------|------|------|--------|
| spring-boot-starter-web | 3.2.0 | Web 框架 | Apache 2.0 |
| langchain4j | 0.29.1 | AI 模型集成 | Apache 2.0 |
| github-api | 1.318 | GitHub API 客户端 | MIT |
| picocli | 4.7.5 | 命令行框架 | Apache 2.0 |
| snakeyaml | 2.2 | YAML 解析 | Apache 2.0 |
| lombok | 1.18.30 | 简化代码 | MIT |
| slf4j-api | 2.0.9 | 日志接口 | MIT |
| logback-classic | 1.4.11 | 日志实现 | LGPL 2.1 |
| junit-jupiter | 5.10.1 | 单元测试 | EPL 2.0 |

完整依赖列表请查看 `pom.xml`。

## 加分项说明

### 1. 自定义规则引擎设计
- 支持通过配置文件动态启用/禁用审查类别
- 每个类别可独立配置严重程度
- 灵活的忽略路径和文件模式配置

### 2. 代码分块优化
- 按方法边界智能分割，避免语义截断
- 自动计算 token 使用量，防止超限
- 支持可配置的分块大小

### 3. 配置管理优雅设计
- 支持 YAML 文件、环境变量、命令行参数三级配置
- 敏感信息（API Key）通过环境变量注入，不硬编码
- 提供配置示例和初始化命令

## 快速开始

### 前置要求

- Java 17+
- Maven 3.8+
- Anthropic API Key 或 DeepSeek API Key
- GitHub Personal Access Token (可选，用于发布评论)

### 构建

```bash
mvn clean package
```

### 安装

构建后会在 `target/` 目录生成 jar 文件:

```bash
java -jar target/pr-reviewer-1.0.0-SNAPSHOT.jar --help
```

或者使用 Maven 直接运行:

```bash
mvn exec:java -Dexec.mainClass="com.ai.pr.reviewer.PrReviewerApplication" -Dexec.args="--help"
```

## 部署步骤

### 1. 环境变量配置

```bash
export ANTHROPIC_API_KEY="your-anthropic-api-key"
export DEEPSEEK_API_KEY="your-deepseek-api-key"  # 二选一
export GITHUB_TOKEN="your-github-token"  # 可选
```

### 2. 运行 CLI 工具

```bash
java -jar target/pr-reviewer-1.0.0-SNAPSHOT.jar review owner/repo 123
```

### 3. 运行 Web 服务

```bash
mvn spring-boot:run
```

访问 http://localhost:8080 使用 Web 界面。

## 使用方法

### 配置

1. 初始化配置文件:

```bash
pr-reviewer config init
```

2. 编辑 `.pr-reviewer.yml` 填入您的 API 密钥，或者使用环境变量:

```bash
export ANTHROPIC_API_KEY="your-api-key"
export GITHUB_TOKEN="your-github-token"
```

### 审查 PR

基本用法:

```bash
pr-reviewer review owner/repo 123
```

发布评论到 PR:

```bash
pr-reviewer review owner/repo 123 --post-comment
```

保存报告到文件:

```bash
pr-reviewer review owner/repo 123 --output report.md
```

完整选项:

```bash
pr-reviewer review --help
```

### 配置管理

显示当前配置:

```bash
pr-reviewer config show
```

初始化配置文件:

```bash
pr-reviewer config init
```

## 配置参考

示例 `.pr-reviewer.yml`:

```yaml
github:
  token: "github-token-here"
  apiUrl: "https://api.github.com"
  postComment: true
  updateExistingComment: true
  setStatusCheck: true

ai:
  model: "claude-3-5-sonnet-20241022"
  apiKey: "anthropic-api-key-here"
  temperature: 0.7
  maxTokens: 4096
  chunkSize: 3000

categories:
  BUG:
    enabled: true
    severity: HIGH
  SECURITY:
    enabled: true
    severity: CRITICAL
  PERFORMANCE:
    enabled: true
    severity: MEDIUM
  STYLE:
    enabled: true
    severity: LOW

ignorePaths:
  - "node_modules/"
  - "target/"
  - "build/"
  - ".git/"

ignorePatterns:
  - "*.min.js"
  - "*.min.css"
  - "*.lock"

maxFileSizeKB: 100
```

## CI/CD 集成

### GitHub Actions 示例

```yaml
name: AI PR Review
on: [pull_request]

jobs:
  review:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
      - name: Build
        run: mvn package -DskipTests
      - name: Run AI Review
        env:
          ANTHROPIC_API_KEY: ${{ secrets.ANTHROPIC_API_KEY }}
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: |
          java -jar target/pr-reviewer-*.jar review \
            ${{ github.repository }} \
            ${{ github.event.pull_request.number }} \
            --post-comment
```

## 退出码

- `0`: 审查完成，无严重问题
- `1`: 执行错误
- `2`: 审查完成，发现严重问题

## 项目结构

```
src/main/java/com/ai/pr/reviewer/
├── PrReviewerApplication.java      # CLI 主入口
├── config/                         # 配置相关
├── github/                         # GitHub 集成
├── review/                         # AI 审查核心
├── output/                         # 输出格式化
├── cli/                            # 命令行工具
└── web/                            # Web UI
```

## 许可证

MIT License
