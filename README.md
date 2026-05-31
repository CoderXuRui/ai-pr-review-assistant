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

---

## 🚀 五分钟快速上手

### 第一步：构建项目

```bash
# 确保你安装了 Java 17+ 和 Maven 3.8+
mvn clean package -DskipTests
```

构建成功后，会在 `target/` 目录生成 `pr-reviewer-1.0.0-SNAPSHOT.jar`

### 第二步：配置 API Key

选择以下一种方式配置（推荐使用环境变量）：

**方式 A：使用环境变量（推荐）**

```bash
# 使用 Claude (推荐)
export ANTHROPIC_API_KEY="你的 Claude API Key"

# 或者使用 DeepSeek
export DEEPSEEK_API_KEY="你的 DeepSeek API Key"

# 可选：配置 GitHub Token (用于发布评论)
export GITHUB_TOKEN="你的 GitHub Personal Access Token"
```

**方式 B：使用配置文件**

```bash
# 生成配置文件
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar config init

# 编辑生成的 .pr-reviewer.yml，填入你的 API Key
```

### 第三步：开始使用！

#### 方式 1：命令行使用

```bash
# 语法：java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar review <仓库> <PR号>

# 示例：审查 facebook/react 的第 12345 号 PR
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar review facebook/react 12345

# 示例：审查并发布评论到 PR
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar review owner/repo 123 --post-comment

# 示例：保存报告到文件
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar review owner/repo 123 --output report.md

# 查看所有帮助
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar --help
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar review --help
```

#### 方式 2：Web UI 使用

```bash
# 启动 Web 服务
mvn spring-boot:run
```

然后打开浏览器访问：http://localhost:8080

在网页中输入仓库和 PR 号，点击"开始审查"即可！

---

## 📖 详细使用指南

### 1. 命令行命令说明

#### review 命令 - 审查 PR

```bash
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar review <仓库> <PR号> [选项]
```

**参数说明：**

| 参数 | 说明 |
|------|------|
| `<仓库>` | 仓库，格式为 `owner/repo`，例如 `facebook/react` |
| `<PR号>` | PR 编号，例如 `123` |

**选项说明：**

| 选项 | 说明 |
|------|------|
| `--post-comment` | 发布审查结果作为 PR 评论 |
| `--set-status` | 设置 PR 的状态检查（成功/失败） |
| `--output <文件>` | 保存 Markdown 报告到指定文件 |
| `--dry-run` | 试运行，不实际发布评论或设置状态 |
| `--config <文件>` | 使用指定的配置文件 |

**使用示例：**

```bash
# 基本审查，仅控制台输出
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar review facebook/react 123

# 审查并发布评论
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar review owner/repo 123 --post-comment

# 保存报告到文件
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar review owner/repo 123 --output report.md

# 完整功能：发布评论 + 设置状态 + 保存报告
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar review owner/repo 123 --post-comment --set-status --output report.md
```

#### config 命令 - 配置管理

```bash
# 初始化配置文件
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar config init

# 显示当前配置
java -jar target/ai-pr-review-assistant-1.0.0-SNAPSHOT.jar config show
```

### 2. 配置文件详解

生成的 `.pr-reviewer.yml` 配置文件说明：

```yaml
github:
  token: "github-token-here"           # GitHub Token (可选，也可用环境变量)
  apiUrl: "https://api.github.com"     # GitHub API 地址
  postComment: true                    # 是否自动发布评论
  updateExistingComment: true          # 是否更新已有评论
  setStatusCheck: true                 # 是否设置状态检查

ai:
  provider: "anthropic"                # AI 提供商: anthropic (Claude) 或 deepseek
  model: "claude-3-5-sonnet-20241022"  # 模型名称 (DeepSeek 用 "deepseek-chat")
  baseUrl: null                        # 自定义 API 地址 (可选)
  apiKey: "anthropic-api-key-here"     # API Key (可选，也可用环境变量)
  temperature: 0.7                     # 温度参数 (0.0-2.0)
  maxTokens: 4096                      # 最大 Token 数
  chunkSize: 3000                      # 代码分块大小（字符数）

categories:
  BUG: { enabled: true, severity: HIGH }          # Bug 检查
  SECURITY: { enabled: true, severity: CRITICAL } # 安全检查
  PERFORMANCE: { enabled: true, severity: MEDIUM }# 性能检查
  STYLE: { enabled: true, severity: LOW }         # 风格检查

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

### 3. 获取 GitHub Token

如果需要发布评论到 PR，需要获取 GitHub Personal Access Token：

1. 访问 https://github.com/settings/tokens
2. 点击 "Generate new token (classic)"
3. 选择 scopes：至少需要 `repo` 权限
4. 生成并保存 Token（只显示一次！）

---

## 💡 使用技巧

### 技巧 1：创建 alias 简化命令

```bash
# 添加到 ~/.bashrc 或 ~/.zshrc
alias pr-reviewer='java -jar /path/to/target/pr-reviewer-1.0.0-SNAPSHOT.jar'

# 然后就可以这样用了！
pr-reviewer review owner/repo 123
```

### 技巧 2：使用 DeepSeek 替代 Claude

编辑配置文件：

```yaml
ai:
  provider: "deepseek"
  model: "deepseek-chat"
```

或者设置环境变量：

```bash
export DEEPSEEK_API_KEY="你的 DeepSeek API Key"
```

### 技巧 3：在 CI/CD 中使用

参考下方的 GitHub Actions 示例。

---

## 🔧 CI/CD 集成

### GitHub Actions 示例

在仓库中创建 `.github/workflows/ai-pr-review.yml`：

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

---

## 📊 退出码说明

| 退出码 | 说明 |
|--------|------|
| `0` | 审查完成，无严重问题 |
| `1` | 执行错误 |
| `2` | 审查完成，发现严重问题 |

---

## 🛠️ 技术栈

- **Java 17+**: 主要开发语言
- **Spring Boot**: Web 框架
- **Maven**: 项目构建工具
- **LangChain4j**: AI 模型集成
- **GitHub API (kohsuke)**: GitHub 集成
- **Picocli**: 命令行界面
- **Jackson**: JSON/YAML 配置解析

## ✨ 原创功能说明

本项目为独立开发的原创作品，以下为核心原创功能：

1. **智能代码分块机制**: 针对大文件的按行分割算法
2. **多维度审查引擎**: Bug、安全、性能、代码风格四维一体审查
3. **配置灵活度**: 支持 YAML 配置 + 环境变量双重覆盖
4. **双模型支持**: Claude 和 DeepSeek 模型可切换
5. **优雅的 Markdown 报告生成**: 结构化的问题展示与改进建议

## 📚 第三方库引用声明

| 库名 | 版本 | 用途 | 许可证 |
|------|------|------|--------|
| spring-boot-starter-web | 3.2.5 | Web 框架 | Apache 2.0 |
| langchain4j | 0.34.0 | AI 模型集成 | Apache 2.0 |
| langchain4j-anthropic | 0.34.0 | Claude 集成 | Apache 2.0 |
| langchain4j-open-ai | 0.34.0 | DeepSeek 集成 | Apache 2.0 |
| github-api | 1.319 | GitHub API 客户端 | MIT |
| jackson-databind | (managed) | JSON 处理 | Apache 2.0 |
| jackson-dataformat-yaml | (managed) | YAML 解析 | Apache 2.0 |
| picocli | 4.7.5 | 命令行框架 | Apache 2.0 |
| slf4j-api | (managed) | 日志接口 | MIT |
| logback-classic | (managed) | 日志实现 | LGPL 2.1 |
| junit-jupiter | 5.10.2 | 单元测试 | EPL 2.0 |
| assertj-core | 3.25.3 | 测试断言 | Apache 2.0 |
| mockito-core | 5.11.0 | Mock 测试 | MIT |

完整依赖列表请查看 `pom.xml`。

## 🏆 加分项说明

### 1. 自定义规则引擎设计
- 支持通过配置文件动态启用/禁用审查类别
- 每个类别可独立配置严重程度
- 灵活的忽略路径和文件模式配置

### 2. 代码分块优化
- 按行分割，避免在单行中间截断
- 可配置的分块大小（字符数）
- 保留行号信息，便于定位问题

### 3. 配置管理优雅设计
- 支持 YAML 文件、环境变量、命令行参数三级配置
- 敏感信息（API Key）通过环境变量注入，不硬编码
- 提供配置示例和初始化命令

## 📂 项目结构

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

## 📄 许可证

MIT License
