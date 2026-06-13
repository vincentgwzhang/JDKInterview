# JDK 8 – JDK 26 面试速查

每个 JDK 版本一个目录：`jdk8` … `jdk26`；并发专题见 **`threading/`**；集合专题见 **`collections/`**。

| 文件 | 说明 |
|------|------|
| `面试知识点.md` | 该版本**常见面试题**与要点（不追求百科全书） |
| `*.java` | JUnit 5 示例，位于 **`src/test/java`**，包名与目录一致（如 `jdk8`、`jdk21`） |

## 为何 IDE 提示「安装 JDK 22」？

**不是**项目在用 JDK 22。常见原因：

1. **包名/目录名** 叫 `jdk22`（表示「JDK 22 知识点」），语言服务会误关联成「需要 JDK 22 运行时」。
2. **`_` 未命名变量** 需 **JDK 22+** 语法；若 IDE 语言级别 &lt; 22，会报：`'_' is a keyword from source level 9 onwards`。
3. 全局已配 **JDK 26**，但项目未同步 **release=26 + preview** 时，仍会按旧级别检查。

请用 **JavaSE-26** 作为默认运行时，并执行 **Java: Clean Java Language Server Workspace** → Reload。

## IDE 出现 200+ 错误 / 被当成 Java 1.8

若 `.settings/org.eclipse.jdt.core.prefs` 里出现 `compliance=1.8`、`release=disabled`，**Maven 导入会把项目降成 1.8**（旧式 `pom` 或禁用 main 编译时常见）。

**处理步骤（必做）：**

1. 确认源码在 **`src/test/java`**（标准 Maven 测试目录），`pom.xml` 中 `maven.compiler.release=26`  
2. 命令面板 → **Java: Clean Java Language Server Workspace** → **Reload and delete**  
3. 命令面板 → **Java: Force Java Compilation** → **Full**  
4. 打开 `.settings/org.eclipse.jdt.core.prefs`，确认是 **25** 而不是 1.8  

**不要**在 IDE 里把 Compliance 手动改成 1.8；文件夹名 `jdk8` 只是章节名，不是 Java 版本。

## 如何运行测试

需要本机安装 **Maven 3.6+** 与 **JDK 26**。

### 在 Cursor 里怎么跑？

| 方式 | 说明 |
|------|------|
| **@Test 方法上的 Run \| Debug** | 光标放在测试方法附近，点 CodeLens 的 **Run Test**（推荐） |
| **左侧 Testing（烧杯图标）** | 展开测试树，点运行按钮 |
| **右上角 Run Java** | **不要用**：只找 `main` 方法；JUnit 类没有 main，会显示 *No tests found* |
| **任务** | `Ctrl+Shift+P` → **Tasks: Run Test Task** → *Maven: 运行当前测试类* |

```bash
# 全部测试
mvn test

# 当前类（示例）
mvn test -Dtest="jdk8.LambdaAndFunctionalTest"

# 某一包下全部
mvn test -Dtest="jdk21.**"
mvn test -Dtest="threading.**"
mvn test -Dtest="collections.**"
```

## LTS 版本（面试重点标星）

| 版本 | 说明 |
|------|------|
| **8** | Lambda / Stream / 新日期 API |
| **11** | 字符串 API、HTTP Client、移除 Java EE |
| **17** | Records、Sealed、Switch 模式匹配预览 |
| **21** | 虚拟线程、结构化并发预览、Switch/Record 模式匹配定稿 |
| **25** | 21 以来特性的稳定与 LTS 收敛（2025） |

## 目录一览

```
jdk8/   Lambda、Stream、Optional、接口默认方法、java.time
jdk9/   模块系统概念、集合工厂、try-with-resources 增强
jdk10/  局部变量 var
jdk11/  字符串、HTTP Client、Optional.isEmpty
jdk12/  Switch 表达式（预览）、Teeing Collector
jdk13/  文本块（预览）、switch yield
jdk14/  Records（预览）、instanceof 模式匹配（预览）
jdk15/  文本块定稿、Sealed 预览
jdk16/  Records 定稿、instanceof 模式匹配定稿
jdk17/  Sealed 定稿、Switch 模式匹配（预览）
jdk18/  UTF-8 默认、简易 Web Server
jdk19/  虚拟线程（预览）
jdk20/  Scoped Values（预览）、虚拟线程改进
jdk21/  虚拟线程、Sequenced Collections、Switch/Record 模式匹配
jdk22/  未命名变量 `_`、String Templates（预览）
jdk23/  模块导入、灵活构造函数体（预览）等
jdk24/  Class-File API、Stream Gatherers 转正
jdk25/  LTS：Scoped Values 转正、结构化并发第五次预览
jdk26/  非 LTS：HTTP/3、Structured Concurrency 第六次预览等
threading/    多线程与 JUC 高频面试（synchronized、volatile、线程池、AQS 工具、CHM 等）
collections/  List、Set、Map、Queue 常见 API 与面试题（equals/hashCode、HashMap、阻塞队列等）
```

> 冷门 API、JFR 细节、仅生产运维关心的 JVM 开关等已刻意省略，聚焦**高频面试**。
