<p align="center">
  <h1 align="center">📝 DB-Document</h1>
  <p align="center">
    <strong>实时协作文档管理系统 · 后端服务</strong>
  </p>
  <p align="center">
    <img src="https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white" alt="Java 17">
    <img src="https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen?logo=springboot&logoColor=white" alt="Spring Boot 3.5.8">
    <img src="https://img.shields.io/badge/MyBatis-3.0.5-blue?logo=mybatis&logoColor=white" alt="MyBatis 3.0.5">
    <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white" alt="MySQL">
    <img src="https://img.shields.io/badge/JWT-HS512-black?logo=jsonwebtokens&logoColor=white" alt="JWT">
    <img src="https://img.shields.io/badge/WebSocket-Y.js-ff69b4" alt="WebSocket Y.js">
    <br>
    <img src="https://img.shields.io/badge/tests-33%20files-success" alt="33 test files">
    <img src="https://img.shields.io/badge/coverage-JaCoCo-red?logo=jacoco" alt="JaCoCo">
    <img src="https://img.shields.io/badge/quality-SonarQube-4E9BCD?logo=sonarqube&logoColor=white" alt="SonarQube">
    <img src="https://img.shields.io/badge/license-MIT-blue" alt="License">
  </p>
  <p align="center">
    <a href="README.md"><img src="https://img.shields.io/badge/简体中文-Readme-red?logo=googletranslate&logoColor=white" alt="中文"></a>
    <a href="README_EN.md"><img src="https://img.shields.io/badge/English-Readme-blue?logo=googletranslate&logoColor=white" alt="English"></a>
  </p>
</p>

---

## 🎯 项目简介

DB-Document 是一个**支持多人实时协作**的在线文档管理系统后端。基于 Spring Boot 构建，采用 Y.js + WebSocket 实现类似 Google Docs 的实时同步编辑体验，配合缓冲池批量落库策略在高并发协作场景下大幅降低数据库 IO 压力。

> 🔗 前端仓库：[Frontend](https://github.com/SaMuel-101-cky/My-Cloud-Document-Interface) · React + TipTap + Yjs

## ✨ 核心亮点

<table>
<tr>
<td width="50%">

### 🚀 实时协作引擎
- 基于 **Y.js 二进制协议**的 WebSocket 通信
- 文档房间隔离 + 用户状态广播（光标/在线感知）
- 历史增量同步：新加入者自动回放全部编辑历史
- VIEWER 只读保护：编辑消息在服务端静默丢弃

</td>
<td width="50%">

### ⚡ 高性能缓冲池
- 每个文档独立的**内存缓冲池**（`DocUpdateBuffer`）
- 双触发策略：**500ms 超时** 或 **10 条积压**
- 批量落库，降低数据库写入频率
- 失败自动重试 + 应用关闭 `@PreDestroy` 兜底落库

</td>
</tr>
<tr>
<td width="50%">

### 🔐 精细权限体系
- 三级角色层级：**OWNER > EDITOR > VIEWER**
- **AOP 切面自动拦截**：`@RequirePermission` 一行注解搞定
- 智能 documentId 提取：Long 参数 或 DTO 的 `getDocumentId()`
- JWT 无状态认证 + **单会话踢出机制**

</td>
<td width="50%">

### 🛡️ 安全防护
- JWT HS512 签名 · Token 踢出（旧会话即时失效）
- 文件上传：MIME 白名单 + 扩展名校验 + 路径穿越防护
- 上传限速：每用户每分钟最多 10 次
- WebSocket 握手阶段 Token 校验

</td>
</tr>
</table>

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                     Client (Browser)                     │
│              React + TipTap + Yjs Provider               │
└────────────┬──────────────┬──────────────┬──────────────┘
             │ HTTP/REST    │ WebSocket    │
             │ (JWT Auth)   │ (Y.js Binary)│
             ▼              ▼              │
┌─────────────────────────────────────────────────────────┐
│                   Spring Boot 3.5.8                      │
│                                                          │
│  ┌──────────┐  ┌──────────────┐  ┌───────────────────┐  │
│  │LoginInter│  │DocumentHand- │  │ DocumentSocket-   │  │
│  │ceptor    │  │shakeIntercep.│  │ Handler           │  │
│  │(JWT校验) │  │(WS握手认证)  │  │ (Y.js消息路由)    │  │
│  └────┬─────┘  └──────┬───────┘  └────────┬──────────┘  │
│       │               │                   │              │
│  ┌────▼───────────────▼───────────────────▼──────────┐  │
│  │                  Controller 层                      │  │
│  │  User · Document · Folder · Permission · DocUpdate │  │
│  │                    · File                          │  │
│  └──────────────────────┬────────────────────────────┘  │
│                         │                                │
│  ┌──────────────────────▼────────────────────────────┐  │
│  │              AOP 切面层                             │  │
│  │  PermissionAspect (权限校验) · LogAspect (操作日志) │  │
│  └──────────────────────┬────────────────────────────┘  │
│                         │                                │
│  ┌──────────────────────▼────────────────────────────┐  │
│  │                Service 业务层                       │  │
│  │    DocUpdateBuffer  ◄── 缓冲池批量落库引擎          │  │
│  │    (定时调度 + 容量触发 + 失败重试)                 │  │
│  └──────────────────────┬────────────────────────────┘  │
│                         │                                │
│  ┌──────────────────────▼────────────────────────────┐  │
│  │           MyBatis Mapper + XML 映射                │  │
│  │     动态SQL · 驼峰映射 · 软删除 · 批量写入         │  │
│  └──────────────────────┬────────────────────────────┘  │
└─────────────────────────┼───────────────────────────────┘
                          │
              ┌───────────▼───────────┐
              │      MySQL 8.0        │
              │   Database: homework  │
              └───────────────────────┘
```

## 📦 技术栈

| 类别 | 技术 | 说明 |
|------|------|------|
| **框架** | Spring Boot 3.5.8 | Web + AOP + WebSocket + Validation |
| **ORM** | MyBatis 3.0.5 | XML 映射 + 动态 SQL + 批量操作 |
| **数据库** | MySQL 8.0 | 软删除模式 · 驼峰映射 |
| **认证** | JWT (jjwt 0.9.1) | HS512 签名 · 无状态 · Token 踢出 |
| **实时通信** | WebSocket | Y.js 二进制协议 · 房间隔离 · Awareness |
| **工具** | Lombok 1.18.30 | 消除样板代码 |
| **测试** | JUnit 5 · Mockito | 33 个测试文件 · 全模块覆盖 |
| **代码质量** | SonarQube · JaCoCo 0.8.13 | 静态分析 + 覆盖率报告 |
| **环境** | spring-dotenv 4.0.0 | .env 环境变量 |
| **运行时** | Java 17 · Maven 3.11.0 | `-parameters` 编译保留参数名 |

## 🚀 快速开始

### 环境要求

- **JDK 17** 或更高版本
- **Maven 3.8+**
- **MySQL 8.0** 运行中

### 1. 克隆项目

```bash
git clone https://github.com/SaMuel-101-cky/Multi-person-collaborative-document-development.git
```

### 2. 配置环境

创建 `.env` 文件或设置环境变量：

```bash
# 数据库配置
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT 密钥（请使用强随机字符串）
JWT_SECRET_KEY=your_jwt_secret_key_here
JWT_EXPIRATION_TIME=86400000

# 文件上传目
FILE_UPLOAD_DIR=./Uploads/
```

### 3. 初始化数据库

```sql
CREATE DATABASE IF NOT EXISTS homework DEFAULT CHARSET utf8mb4;
```

数据库表结构由 MyBatis 自动建表或手动导入 SQL 脚本。

### 4. 启动服务

```bash
# 开发环境启动
mvn spring-boot:run

# 或构建后运行
mvn clean package -DskipTests
java -jar target/DB_document-0.0.1-SNAPSHOT.jar
```

服务默认运行在 **http://localhost:8080**。

### 5. 验证

```bash
# 注册用户
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"account":"test","password":"123456","nickname":"测试用户"}'

# 登录获取 Token
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"account":"test","password":"123456"}'
```

## 📡 API 概览

<details open>
<summary><b>👤 用户模块</b> — <code>/api/user</code></summary>

| 方法 | 路径 | 认证 | 说明 |
|------|------|:---:|------|
| POST | `/register` | ❌ | 用户注册 |
| POST | `/login` | ❌ | 登录，返回 JWT（同时踢出旧会话） |
| GET | `/ping` | ✅ | Token 心跳检测 |
| GET | `/me` | ✅ | 获取当前用户信息 |
| GET | `/detail/{userId}` | ❌ | 查看用户公开名片 |
| GET | `/search?nickname=` | ✅ | 按昵称搜索用户 |
| POST | `/update/avatar` | ✅ | 更新头像 |
| POST | `/update/info` | ✅ | 更新昵称/简介 |
| POST | `/change-password` | ✅ | 修改密码 |

</details>

<details open>
<summary><b>📄 文档模块</b> — <code>/api/document</code></summary>

| 方法 | 路径 | 权限 | 说明 |
|------|------|:---:|------|
| POST | `/create` | — | 创建文档 |
| DELETE | `/delete/{id}` | OWNER | 删除文档 |
| POST | `/move` | EDITOR | 移动文档到文件夹 |
| POST | `/update/info` | EDITOR | 更新文档标题等信息 |
| GET | `/detail/{id}` | VIEWER | 获取文档详情 |
| GET | `/shared` | — | 获取共享文档列表 |

</details>

<details>
<summary><b>🔄 增量更新模块</b> — <code>/api/docUpdate</code></summary>

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/create` | 创建增量更新 |
| GET | `/detail?documentId=&vectorClock=` | 按向量时钟查询 |
| GET | `/list/{documentId}` | 文档全部更新记录 |
| GET | `/children?documentId=&parentUpdateId=` | 子更新列表 |
| POST | `/update` | 更新记录 |
| DELETE | `/delete/{id}` | 删除记录 |

</details>

<details>
<summary><b>📁 文件夹模块</b> — <code>/api/folder</code></summary>

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/create` | 创建文件夹 |
| DELETE | `/delete/{id}` | 删除文件夹 |
| POST | `/move` | 移动文件夹 |
| GET | `/content` | 个人文件夹内容 |
| GET | `/shared-content` | 共享文件夹内容 |

</details>

<details>
<summary><b>🔑 权限模块</b> — <code>/api/permission</code></summary>

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/create` | 向用户共享文档 |
| POST | `/delete` | 撤销用户权限 |
| GET | `/document/{id}` | 文档权限列表 |

</details>

<details>
<summary><b>📤 文件上传</b> — <code>/api/file</code></summary>

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/upload/image` | 上传图片（限速 10次/分钟） |

</details>

## 🔍 深入设计

### WebSocket 协作流程

```
  Client A              Server               Client B
     │                     │                     │
     │── WS Connect ──────►│◄──── WS Connect ────│
     │   (docId, token)    │    (docId, token)    │
     │                     │                     │
     │── SyncStep1 [0,0]──►│                     │
     │                     │── 历史更新回放 ──────►│
     │                     │── Awareness 广播 ───►│
     │                     │                     │
     │── SyncStep2 [0,2]──►│                     │
     │   (编辑内容)         │── DocUpdateBuffer   │
     │                     │   .enqueueUpdate()  │
     │                     │── 广播给 Client B ──►│
     │                     │                     │
     │── Awareness [1] ───►│                     │
     │   (光标位置)         │── 广播给 Client B ──►│
     │                     │                     │
     │                     │── 定时/批量落库 ────► MySQL
```

### 缓冲池工作原理

```
  WebSocket 消息
       │
       ▼
  DocUpdateBuffer.enqueueUpdate(docId, payload)
       │
       ├── pending.size < 10 && 距上次 < 500ms
       │      └── 加入 pending 队列，等待调度
       │
       ├── pending.size ≥ 10
       │      └── 立即 drain → 批量写入 DB
       │
       └── 距上次 ≥ 500ms
              └── 定时任务触发 → 批量写入 DB

  写入失败 → 消息与后续 pending 合并 → 重新调度
  应用关闭 → @PreDestroy → 遍历所有文档 → 强制落库
```

### 权限校验链路

```
  HTTP Request
       │
       ▼
  LoginInterceptor.preHandle()
       ├── 解析 Authorization Header
       ├── 验证 JWT Token 有效性
       ├── 检测 Token 是否被踢出
       └── UserContext.setUserId(userId)
       │
       ▼
  PermissionAspect.checkPermission()   ← @RequirePermission 触发
       ├── 从方法参数提取 documentId
       ├── 查库获取用户在该文档的真实角色
       ├── actualRole.hasPermission(requiredRole)
       └── 权限不足 → 抛出 RuntimeException
       │
       ▼
  Controller 方法执行
       │
       ▼
  LoginInterceptor.afterCompletion()
       └── UserContext.remove()  ← 防止内存泄漏
```

## 📂 项目结构

```
Backend/
├── src/main/java/com/example/db_document/
│   ├── annotation/       # @RequirePermission · @Log
│   ├── aspect/           # PermissionAspect · LogAspect
│   ├── config/           # WebConfig · WebSocketConfig · JwtConfig
│   ├── controller/       # 6 个 Controller，30+ 接口
│   ├── exception/        # BusinessException
│   ├── handler/          # GlobalExceptionHandler · DocumentSocketHandler
│   ├── interceptor/      # LoginInterceptor · DocumentHandshakeInterceptor
│   ├── mapper/           # 6 个 MyBatis Mapper 接口
│   ├── model/
│   │   ├── dto/          # 12 个请求 DTO
│   │   └── vo/           # 6 个响应 VO
│   ├── pojo/             # 8 个实体类（含 PermissionType 枚举）
│   ├── service/          # 9 个 Service（含 DocUpdateBuffer）
│   └── utils/            # JwtUtil · UserContext
├── src/main/resources/
│   ├── mapper/           # MyBatis XML 映射文件
│   └── application.yml   # 主配置文件
├── src/test/java/        # 33 个测试文件
├── pom.xml
├── CLAUDE.md             # AI 编程助手指南
└── README.md
```

## 🧪 测试

```bash
# 运行全部测试
mvn test

# 运行单个测试类
mvn test -Dtest=DocumentServiceTest

# 生成覆盖率报告
mvn test
# 报告位置: target/site/jacoco/index.html
```

**测试覆盖分布：**

```
Controller (5)  ████████░░  测试 API 端点与参数校验
Service   (9)  ████████████ 测试业务逻辑与事务
Mapper    (6)  ████████░░  测试 SQL 映射与查询
Aspect    (2)  ████░░░░░░  测试权限切面与日志切面
Handler   (2)  ████░░░░░░  测试异常处理与 WebSocket
Interceptor(2) ████░░░░░░  测试 JWT 校验与 WS 握手
Config    (1)  ██░░░░░░░░  测试 CORS 与资源配置
Util/POJO (6)  ████████░░  测试工具方法与枚举逻辑
```

## 🔧 配置参考

```yaml
# application.yml 关键配置项
jwt:
  secret-key: ${JWT_SECRET_KEY}          # JWT 签名密钥（环境变量）
  expiration-time: 86400000              # Token 过期时间 24h

doc-update:
  flush-interval-ms: 500                 # 缓冲池落库间隔
  max-batch-size: 10                     # 最大批次数量

file:
  upload-dir: ./Uploads/                 # 上传目录
  allowed-types: image/jpeg,image/png,image/gif,image/webp
  max-size: 5242880                      # 5MB

spring:
  servlet.multipart.max-file-size: 5MB
  servlet.multipart.max-request-size: 5MB

mybatis:
  type-aliases-package: com.example.db_document.pojo
  mapper-locations: classpath:mapper/*.xml
  configuration.map-underscore-to-camel-case: true
```

---

<p align="center">
  <sub>Built with ❤️ using Spring Boot · MyBatis · WebSocket</sub>
  <br>
  <sub>📝 DB-Document © 2025</sub>
</p>
