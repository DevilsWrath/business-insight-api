# Business Insight API

A production-oriented backend application designed to support
ERP-based reporting, data extraction, and business intelligence workflows.

The system allows data engineers to define external data sources and parameterized SQL query templates,
while end users can later build visualizations on top of those datasets.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot 3
- Spring Security (JWT, stateless)
- PostgreSQL
- Flyway (database migrations)
- Maven
- Docker (local database)

---

## 🔐 Authentication & Security

- JWT-based stateless authentication
- BCrypt password hashing
- Custom authentication filter (Bearer token)
- Global exception handling with standardized JSON error responses
- SELECT-only enforcement for query templates (MVP guardrail)

All endpoints except `/api/auth/**` require authentication.

---

## 🧱 Architecture Overview

The project follows a layered architecture:

- `api` → REST controllers & DTOs
- `application` → Business services
- `domain` → Core entities & repositories
- `infrastructure` → Security & configuration
- `common` → Shared components & error handling

Insight module structure:

- DataSource → Represents an external ERP connection definition
- QueryTemplate → Parameterized, SELECT-only SQL query definitions
- (Upcoming) QueryRun & DatasetSnapshot → Execution and persistence layer

---

## 🏢 Insight Module (MVP Scope)

### Data Sources

Represents an external ERP connection configuration.

Example connection JSON:

```json
{
  "host": "10.0.0.10",
  "port": 1433,
  "database": "ERP",
  "username": "readonly_user",
  "secretRef": "env:ERP_PASSWORD"
}
```
### Query Templates

Parameterized SQL definitions restricted to SELECT queries only.

Example parameters:

```
[
  { "name": "dateFrom", "type": "date", "required": true },
  { "name": "dateTo", "type": "date", "required": true }
]
```
Example output schema:

```
[
  { "name": "date", "type": "date" },
  { "name": "amount", "type": "decimal" },
  { "name": "currency", "type": "string" }
]
```
---
## 🐳 Running Locally

### 1. Start PostgreSQL

```
docker compose up -d
```
## 2. Set JWT Secret

Linux/macOS:
```
export JWT_SECRET=your_long_random_secret_here
```
Windows:
```
$env:JWT_SECRET="your_long_random_secret_here"
```
### 3. Run the application
```
./mvnw spring-boot:run
```
Application runs at:
```
http://localhost:8080
```
---

## RBAC Method Security (Roadmap Step)

- `@EnableMethodSecurity` is enabled.
- JWT access token includes `perms` claim (permission codes).
- `JwtAuthenticationFilter` maps `perms` claim to Spring authorities.
- Insight endpoints are guarded with `@PreAuthorize`.

## Admin RBAC API (MVP)

Base path: `/api/admin/rbac`

- `GET /permissions` requires `RBAC_PERMISSION_MANAGE`
- `GET /roles` requires `RBAC_ROLE_MANAGE`
- `POST /roles` requires `RBAC_ROLE_MANAGE`
- `PUT /roles/{id}` requires `RBAC_ROLE_MANAGE`
- `PUT /roles/{id}/permissions` requires `RBAC_ROLE_MANAGE`
- `GET /users?query=...` requires `RBAC_USER_ROLE_ASSIGN`
- `PUT /users/{id}/roles` requires `RBAC_USER_ROLE_ASSIGN`
