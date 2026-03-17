# 🔐 JWT Auth System

A stateless **JWT-based authentication REST API** built with Spring Boot. Handles user registration, login, and secure access to protected resources.

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql)
![JWT](https://img.shields.io/badge/JWT-JJWT%200.13.0-purple?style=flat-square)

---

## Features

- **Register & Login** — username/password authentication with BCrypt hashing
- **Stateless JWT** — signed HS256 tokens, 1-hour expiry, no server-side sessions
- **Spring Security** — filter chain with per-request token validation
- **Global Error Handling** — consistent `{ status, message, timestamp }` error responses
- **Swagger UI** — interactive API docs at `/swagger-ui.html`
- **Docker Compose** — one-command local setup with PostgreSQL

---

## Quick Start

### 1. Clone & configure

```bash
git clone <repo-url>
cd auth
```

> ⚠️ **`JWT_SECRET` is required.** Use a random string of at least 32 characters.

**Option A — `.env` file (recommended)**

```bash
cp .env.example .env
```

Then edit `.env` and fill in your values:

```env
# JWT secret key (generate a strong secret, e.g., using openssl rand -base64 32)
JWT_SECRET=your-super-secret-jwt-key-change-this

# PostgreSQL database URL
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/your_database

# Optional: database credentials (add if needed)
# SPRING_DATASOURCE_USERNAME=your_username
# SPRING_DATASOURCE_PASSWORD=your_password
```

**Option B — export directly**

```bash
export JWT_SECRET=your-super-secret-key-minimum-32-characters
```

### 2. Run with Docker Compose

```bash
./mvnw clean package -DskipTests
docker-compose up --build
```

### 3. Run locally (Maven + external Postgres)

```bash
./mvnw spring-boot:run
```

The app starts on **http://localhost:8080**.  
Swagger UI: **http://localhost:8080/swagger-ui.html**

---

## API Reference

Base URL: `/api/v1`

### `POST /auth/register`

Create a new account.

```json
{
  "username": "alice",
  "password": "secret123"
}
```

| Response | Body |
|----------|------|
| `201 Created` | `"User registered successfully"` |
| `400 Bad Request` | `{ status, message, timestamp }` |

---

### `POST /auth/login`

Authenticate and receive a JWT.

```json
{
  "username": "alice",
  "password": "secret123"
}
```

| Response | Body |
|----------|------|
| `200 OK` | `{ "token": "eyJhbGci..." }` |
| `401 Unauthorized` | `{ status, message, timestamp }` |

---

### `GET /users/me` 🔒

Returns the authenticated user's profile. Requires a Bearer token.

```
Authorization: Bearer <token>
```

| Response | Body |
|----------|------|
| `200 OK` | `"Welcome to your profile, alice!"` |
| `401 Unauthorized` | No/expired/invalid token |

---

## 🏗️ System Architecture

The application follows a **clean layered architecture**.

```
Client
  ↓
Controller Layer
  ↓
Service Layer
  ↓
Repository Layer
  ↓
Database
```

Security processing happens through the **Spring Security filter chain**.

```
Request
  ↓
JWT Authentication Filter
  ↓
Security Context
  ↓
Controller
```

---

## 🔐 Authentication Flow

### 1️⃣ User Registers

```
POST /api/v1/auth/register
```

The password is encrypted using **BCrypt** before being stored in the database.

---

### 2️⃣ User Logs In

```
POST /api/v1/auth/login
```

Spring Security authenticates the credentials. If valid:

1. UserDetails are loaded
2. JWT token is generated
3. Token is returned to the client

Example response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 3️⃣ Client Accesses Protected Endpoint

The client sends the JWT in the header:

```
Authorization: Bearer <token>
```

The **JWT Authentication Filter**:

1. Extracts the token
2. Validates it
3. Loads the user from the database
4. Sets authentication in the Security Context

---

## Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `JWT_SECRET` | ✅ Yes | — | HMAC signing key (min 32 chars) |
| `SPRING_DATASOURCE_URL` | No | `jdbc:postgresql://localhost:5432/authdb` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | No | `postgres` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | No | `password` | DB password |

---

## Project Structure

```
src/main/java/com/mo/auth/
├── config/
│   ├── SecurityConfig.java          Filter chain, session policy
│   └── OpenApiConfig.java           Swagger UI + Bearer scheme
├── controller/
│   ├── AuthController.java          /register, /login
│   └── UserController.java          /users/me (protected)
├── dto/
│   ├── AuthRequest.java             Validated request body
│   ├── AuthResponse.java            JWT response wrapper
│   └── ErrorResponse.java           Error payload
├── exception/
│   └── GlobalExceptionHandler.java  Centralised error handling
├── model/
│   └── User.java                    JPA entity (UUID PK)
├── repository/
│   └── UserRepository.java          findByUsername()
├── security/
│   ├── JwtUtil.java                 Token generation & validation
│   └── JwtAuthenticationFilter.java Per-request Bearer interceptor
└── service/
    ├── AuthService.java             register() + login()
    └── UserDetailsServiceImpl.java  Spring Security user lookup
```

---

## Running Tests

```bash
./mvnw test
```

Tests use an H2 in-memory database — no external services required.

| Test Class | Coverage |
|------------|----------|
| `AuthControllerTest` | Register & login endpoint behaviour |
| `AuthServiceTest` | Registration and login business logic |
| `JwtUtilTest` | Token generation, validation, expiry |
| `UserDetailsServiceImplTest` | User lookup success & not-found cases |
| `GlobalExceptionHandlerTest` | HTTP status codes per exception type |

---

## Known Limitations

- No **refresh tokens** — clients must re-authenticate after expiry
- No **roles / RBAC** — all authenticated users have equal access
- No **token revocation** — tokens are valid until expiry (add a Redis blocklist to address this)
- **DDL auto-update** is enabled — use Flyway or Liquibase for production migrations
