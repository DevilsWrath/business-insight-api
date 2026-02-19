# Business Insight API

Enterprise-style Spring Boot backend designed to demonstrate
real-world backend architecture, security, and infrastructure practices.

## 🚀 Tech Stack

- Java 17
- Spring Boot 3
- Spring Security
- PostgreSQL
- Flyway
- Docker
- Maven

---

## 🧱 Architecture

This project follows a layered structure:

- `api` → Controllers & DTOs
- `application` → Business logic / Use cases
- `domain` → Core entities & repository interfaces
- `infrastructure` → Security & configuration
- `common` → Shared components

Designed with maintainability and scalability in mind.

---

## 🔐 Authentication (Work in Progress)

- User registration with BCrypt password hashing
- JWT authentication (planned)
- Role-based access control (planned)

---

## 🐳 Run Locally

### 1️⃣ Start PostgreSQL

```bash
docker compose up -d
```
### 2️⃣ Run Spring Boot application
```bash
./mvnw spring-boot:run
```
### Application will be available at:
```bash
http://localhost:8080
```