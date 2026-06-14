<p align="center">
  <h1 align="center">📝 DB-Document</h1>
  <p align="center">
    <strong>Real-Time Collaborative Document Management System · Backend</strong>
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

## 🎯 Overview

DB-Document is the backend for a **real-time collaborative document management system**. Built on Spring Boot, it leverages Y.js + WebSocket to deliver a Google Docs-like synchronous editing experience. An intelligent buffered batch-persistence layer (`DocUpdateBuffer`) dramatically reduces database I/O under high-concurrency collaborative workloads.

> 🔗 Frontend Repo: [Frontend](https://github.com/SaMuel-101-cky/My-Cloud-Document-Interface) · React + TipTap + Yjs

## ✨ Highlights

<table>
<tr>
<td width="50%">

### 🚀 Real-Time Collaboration Engine
- WebSocket communication based on the **Y.js binary protocol**
- Per-document room isolation + user presence broadcast (cursors/online awareness)
- Historical incremental sync — new joiners automatically replay the full edit history
- VIEWER write protection — edit messages silently discarded server-side

</td>
<td width="50%">

### ⚡ High-Performance Buffer Pool
- Per-document **in-memory buffer pool** (`DocUpdateBuffer`)
- Dual-flush strategy: **500ms timeout** OR **10-message batch threshold**
- Batch database writes dramatically reduce I/O frequency
- Automatic retry on failure + `@PreDestroy` graceful shutdown flush

</td>
</tr>
<tr>
<td width="50%">

### 🔐 Fine-Grained Permission System
- Three-tier role hierarchy: **OWNER > EDITOR > VIEWER**
- **AOP auto-interception** — just one `@RequirePermission` annotation
- Smart documentId extraction: `Long` parameter or DTO's `getDocumentId()`
- JWT stateless authentication + **single-session kickout**

</td>
<td width="50%">

### 🛡️ Security
- JWT HS512 signing · Token kickout (old sessions instantly invalidated)
- File upload: MIME-type whitelist + extension validation + path-traversal protection
- Rate limiting: max 10 uploads per user per minute
- WebSocket handshake-phase Token validation

</td>
</tr>
</table>

## 🏗️ Architecture

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
│  │  Login   │  │  WS Handshake│  │  DocumentSocket   │  │
│  │Interceptor│  │  Interceptor│  │     Handler       │  │
│  │(JWT Auth)│  │(WS Auth)    │  │(Y.js Msg Router)  │  │
│  └────┬─────┘  └──────┬───────┘  └────────┬──────────┘  │
│       │               │                   │              │
│  ┌────▼───────────────▼───────────────────▼──────────┐  │
│  │                 Controller Layer                    │  │
│  │  User · Document · Folder · Permission · DocUpdate │  │
│  │                    · File                          │  │
│  └──────────────────────┬────────────────────────────┘  │
│                         │                                │
│  ┌──────────────────────▼────────────────────────────┐  │
│  │                 AOP Layer                           │  │
│  │  PermissionAspect (AuthZ) · LogAspect (Audit Log)  │  │
│  └──────────────────────┬────────────────────────────┘  │
│                         │                                │
│  ┌──────────────────────▼────────────────────────────┐  │
│  │               Service Layer                         │  │
│  │    DocUpdateBuffer  ◄── Buffered Batch Write Engine │  │
│  │    (Scheduled + Threshold + Retry)                  │  │
│  └──────────────────────┬────────────────────────────┘  │
│                         │                                │
│  ┌──────────────────────▼────────────────────────────┐  │
│  │          MyBatis Mapper + XML Mappings             │  │
│  │   Dynamic SQL · Camel Mapping · Soft Delete · Batch│  │
│  └──────────────────────┬────────────────────────────┘  │
└─────────────────────────┼───────────────────────────────┘
                          │
              ┌───────────▼───────────┐
              │      MySQL 8.0        │
              │   Database: homework  │
              └───────────────────────┘
```

## 📦 Tech Stack

| Category | Technology | Details |
|----------|-----------|---------|
| **Framework** | Spring Boot 3.5.8 | Web + AOP + WebSocket + Validation |
| **ORM** | MyBatis 3.0.5 | XML Mappings + Dynamic SQL + Batch Ops |
| **Database** | MySQL 8.0 | Soft-delete pattern · Camel-case mapping |
| **Auth** | JWT (jjwt 0.9.1) | HS512 signing · Stateless · Token kickout |
| **Real-Time** | WebSocket | Y.js binary protocol · Room isolation · Awareness |
| **Utilities** | Lombok 1.18.30 | Eliminate boilerplate code |
| **Testing** | JUnit 5 · Mockito | 33 test files · Full module coverage |
| **Code Quality** | SonarQube · JaCoCo 0.8.13 | Static analysis + coverage reports |
| **Config** | spring-dotenv 4.0.0 | `.env` file environment variables |
| **Runtime** | Java 17 · Maven 3.11.0 | `-parameters` compiler flag |

## 🚀 Quick Start

### Prerequisites

- **JDK 17** or higher
- **Maven 3.8+**
- **MySQL 8.0** up and running

### 1. Clone

```bash
git clone https://github.com/SaMuel-101-cky/Multi-person-collaborative-document-development.git
cd Backend
```

### 2. Configuration

Create a `.env` file or set environment variables:

```bash
# Database
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT secret (use a strong random string!)
JWT_SECRET_KEY=your_jwt_secret_key_here
JWT_EXPIRATION_TIME=86400000

# File upload directory
FILE_UPLOAD_DIR=./Uploads/
```

### 3. Initialize Database

```sql
CREATE DATABASE IF NOT EXISTS homework DEFAULT CHARSET utf8mb4;
```

Tables are created automatically by MyBatis or imported manually via SQL scripts.

### 4. Start the Server

```bash
# Development mode
mvn spring-boot:run

# Or build & run
mvn clean package -DskipTests
java -jar target/DB_document-0.0.1-SNAPSHOT.jar
```

The server starts at **http://localhost:8080**.

### 5. Verify

```bash
# Register a user
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"account":"test","password":"123456","nickname":"Test User"}'

# Login to get a token
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"account":"test","password":"123456"}'
```

## 📡 API Reference

<details open>
<summary><b>👤 User</b> — <code>/api/user</code></summary>

| Method | Path | Auth | Description |
|--------|------|:---:|-------------|
| POST | `/register` | ❌ | Register a new user |
| POST | `/login` | ❌ | Login → JWT (kicks out old session) |
| GET | `/ping` | ✅ | Token heartbeat check |
| GET | `/me` | ✅ | Get current user info |
| GET | `/detail/{userId}` | ❌ | View public user profile |
| GET | `/search?nickname=` | ✅ | Search users by nickname |
| POST | `/update/avatar` | ✅ | Update avatar |
| POST | `/update/info` | ✅ | Update nickname/bio |
| POST | `/change-password` | ✅ | Change password |

</details>

<details open>
<summary><b>📄 Document</b> — <code>/api/document</code></summary>

| Method | Path | Permission | Description |
|--------|------|:----------:|-------------|
| POST | `/create` | — | Create a document |
| DELETE | `/delete/{id}` | OWNER | Delete a document |
| POST | `/move` | EDITOR | Move document to a folder |
| POST | `/update/info` | EDITOR | Update document metadata |
| GET | `/detail/{id}` | VIEWER | Get document details |
| GET | `/shared` | — | List shared documents |

</details>

<details>
<summary><b>🔄 Incremental Updates</b> — <code>/api/docUpdate</code></summary>

| Method | Path | Description |
|--------|------|-------------|
| POST | `/create` | Create an incremental update |
| GET | `/detail?documentId=&vectorClock=` | Query by documentId + vector clock |
| GET | `/list/{documentId}` | All updates for a document |
| GET | `/children?documentId=&parentUpdateId=` | Child update list |
| POST | `/update` | Update a record |
| DELETE | `/delete/{id}` | Delete a record |

</details>

<details>
<summary><b>📁 Folder</b> — <code>/api/folder</code></summary>

| Method | Path | Description |
|--------|------|-------------|
| POST | `/create` | Create a folder |
| DELETE | `/delete/{id}` | Delete a folder |
| POST | `/move` | Move a folder |
| GET | `/content` | Personal folder contents |
| GET | `/shared-content` | Shared folder contents |

</details>

<details>
<summary><b>🔑 Permission</b> — <code>/api/permission</code></summary>

| Method | Path | Description |
|--------|------|-------------|
| POST | `/create` | Share document with a user |
| POST | `/delete` | Revoke a user's permission |
| GET | `/document/{id}` | List document permissions |

</details>

<details>
<summary><b>📤 File Upload</b> — <code>/api/file</code></summary>

| Method | Path | Description |
|--------|------|-------------|
| POST | `/upload/image` | Upload an image (rate limit: 10/min) |

</details>

## 🔍 Deep Dive

### WebSocket Collaboration Flow

```
  Client A              Server               Client B
     │                     │                     │
     │── WS Connect ──────►│◄──── WS Connect ────│
     │   (docId, token)    │    (docId, token)    │
     │                     │                     │
     │── SyncStep1 [0,0]──►│                     │
     │                     │── Replay history ───►│
     │                     │── Broadcast aware.──►│
     │                     │                     │
     │── SyncStep2 [0,2]──►│                     │
     │   (edit content)    │── DocUpdateBuffer   │
     │                     │   .enqueueUpdate()  │
     │                     │── Broadcast to B ───►│
     │                     │                     │
     │── Awareness [1] ───►│                     │
     │   (cursor position) │── Broadcast to B ───►│
     │                     │                     │
     │                     │── Scheduled/batch ──► MySQL
```

### Buffer Pool Mechanics

```
  WebSocket Message
       │
       ▼
  DocUpdateBuffer.enqueueUpdate(docId, payload)
       │
       ├── pending.size < 10 AND elapsed < 500ms
       │      └── Append to pending queue, await schedule
       │
       ├── pending.size ≥ 10
       │      └── Drain immediately → batch write to DB
       │
       └── elapsed ≥ 500ms
              └── Scheduled task fires → batch write to DB

  Write failure → merge with subsequent pending → reschedule
  App shutdown → @PreDestroy → iterate all docs → force flush
```

### Permission Check Chain

```
  HTTP Request
       │
       ▼
  LoginInterceptor.preHandle()
       ├── Parse Authorization header
       ├── Validate JWT token
       ├── Check if token was kicked out
       └── UserContext.setUserId(userId)
       │
       ▼
  PermissionAspect.checkPermission()   ← triggered by @RequirePermission
       ├── Extract documentId from method args
       ├── Query DB for user's actual role on the document
       ├── actualRole.hasPermission(requiredRole)
       └── Insufficient → throw RuntimeException
       │
       ▼
  Controller method executes
       │
       ▼
  LoginInterceptor.afterCompletion()
       └── UserContext.remove()  ← prevent memory leaks
```

## 📂 Project Structure

```
Backend/
├── src/main/java/com/example/db_document/
│   ├── annotation/       # @RequirePermission · @Log
│   ├── aspect/           # PermissionAspect · LogAspect
│   ├── config/           # WebConfig · WebSocketConfig · JwtConfig
│   ├── controller/       # 6 Controllers, 30+ endpoints
│   ├── exception/        # BusinessException
│   ├── handler/          # GlobalExceptionHandler · DocumentSocketHandler
│   ├── interceptor/      # LoginInterceptor · DocumentHandshakeInterceptor
│   ├── mapper/           # 6 MyBatis Mapper interfaces
│   ├── model/
│   │   ├── dto/          # 12 request DTOs
│   │   └── vo/           # 6 response VOs
│   ├── pojo/             # 8 entity classes (incl. PermissionType enum)
│   ├── service/          # 9 Services (incl. DocUpdateBuffer)
│   └── utils/            # JwtUtil · UserContext
├── src/main/resources/
│   ├── mapper/           # MyBatis XML mapping files
│   └── application.yml   # Main configuration
├── src/test/java/        # 33 test files
├── pom.xml
├── CLAUDE.md             # AI coding assistant guide
├── README.md             # 中文文档
└── README_EN.md          # English docs (this file)
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=DocumentServiceTest

# Generate coverage report (auto-generated after `mvn test`)
# Report location: target/site/jacoco/index.html
```

**Coverage by Layer:**

```
Controller (5)  ████████░░  API endpoints & parameter validation
Service   (9)  ████████████ Business logic & transactions
Mapper    (6)  ████████░░  SQL mappings & queries
Aspect    (2)  ████░░░░░░  Permission & logging aspects
Handler   (2)  ████░░░░░░  Exception handling & WebSocket
Interceptor(2) ████░░░░░░  JWT validation & WS handshake
Config    (1)  ██░░░░░░░░  CORS & resource config
Util/POJO (6)  ████████░░  Utility methods & enum logic
```

## 🔧 Configuration Reference

```yaml
# Key application.yml settings
jwt:
  secret-key: ${JWT_SECRET_KEY}          # JWT signing key (env variable)
  expiration-time: 86400000              # Token TTL: 24h

doc-update:
  flush-interval-ms: 500                 # Buffer flush interval
  max-batch-size: 10                     # Max batch size

file:
  upload-dir: ./Uploads/                 # Upload directory
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
