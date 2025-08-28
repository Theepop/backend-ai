# C4 Architecture (Mermaid)

สรุปสถาปัตยกรรมของระบบ Backend-AI ในรูปแบบ C4 (Mermaid) — ประกอบด้วย System Context, Container, และ Component diagrams

## System Context

```mermaid
C4Context
title Backend-AI — System Context
Person(user, "End User", "สมาชิกที่ใช้งานผ่านเว็บหรือ API")
System(backend, "Backend-AI", "Spring Boot application providing authentication, user management and transfers API")
SystemDb(sqlite, "SQLite (data.db)", "Local database storing users and transfers")
System_Ext(swagger, "OpenAPI / Swagger UI", "API documentation")

Rel(user, backend, "Uses (HTTP/JSON)")
Rel(backend, sqlite, "Reads/Writes (JDBC)")
Rel(backend, swagger, "Exposes API docs")
```

## Container Diagram

```mermaid
C4Container
title Backend-AI — Container Diagram
Person(user, "End User", "Uses web or API")

System_Boundary(backend_sys, "Backend-AI") {
  Container(app, "Spring Boot App", "Java + Spring Boot", "Provides REST APIs: /register, /login, /me, /transfer, /transfers")
  ContainerDb(db, "SQLite", "SQLite", "Persistent storage for users & transfers (data.db)")
}

Rel(user, app, "Calls REST API (HTTP, JSON)")
Rel(app, db, "Reads/Writes via JdbcTemplate / HikariCP")
```

## Component Diagram (สำคัญ: transfer flow)

```mermaid
C4Component
title Backend-AI — Component Diagram (Transfer-related)
Container(app, "Spring Boot App", "Java + Spring Boot")

Component(authController, "AuthController", "REST Controller", "/register, /login, /me")
Component(transferController, "TransferController", "REST Controller", "POST /transfer, GET /transfers — handles validation and transaction")
Component(jwtFilter, "JwtAuthFilter", "Security Filter", "Parses Authorization: Bearer <token>, sets Authentication principal")
Component(jwtService, "JwtService", "Service", "Generate and validate JWTs; extract userId")
Component(requestLogger, "RequestLoggingFilter", "Servlet Filter", "Logs request pre/post; helpful for debugging and audit")
Component(userRepo, "JdbcUserRepository", "Repository", "CRUD and adjustPoints via JDBC")
Component(transferRepo, "JdbcTransferRepository", "Repository", "Persist transfer records")
Component(sqlite, "SQLite (data.db)", "Database", "users and transfers tables")

Rel(user, transferController, "Calls POST /transfer")
Rel(transferController, userRepo, "Loads users and adjustPoints (within @Transactional)")
Rel(transferController, transferRepo, "Saves transfer record")
Rel(jwtFilter, jwtService, "Validates token")
Rel(jwtFilter, userRepo, "Loads authenticated User by id")
Rel(authController, jwtService, "Generates JWT on successful login")
Rel(transferController, sqlite, "Indirect via repositories (JdbcTemplate)")
Rel(app, requestLogger, "Request filter runs around controllers")
```

หมายเหตุสั้นๆ:
- การโอนแต้ม (transfer) ต้องทำเป็น transaction เดียว: ลดแต้มผู้ส่ง, เพิ่มแต้มผู้รับ, บันทึกรายการในตาราง transfers
- JwtAuthFilter ทำหน้าที่แปลง token -> user principal ก่อนเข้าถึง Controller
- สามารถนำ Mermaid C4 blocks นี้ไปเปิดบน mermaid.live หรือแปลงเป็น SVG เพื่อใช้งานใน draw.io
