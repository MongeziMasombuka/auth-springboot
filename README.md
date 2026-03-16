# 🔐 Spring Boot JWT Authentication API

A **production-style authentication REST API** built with **Spring Boot, Spring Security, and JWT** that demonstrates secure authentication, clean architecture, and proper testing practices.

This project implements **stateless authentication** using JSON Web Tokens and follows **industry backend design patterns** used in modern backend systems.

---

# 📌 Project Overview

This application provides:

* User registration
* Secure login with JWT
* Protected API endpoints
* Password encryption
* Token validation
* Global exception handling
* Unit testing

It demonstrates **secure API development practices** commonly used in backend systems.

---

# 🚀 Key Features

* Stateless authentication using JWT
* Secure password hashing using BCrypt
* Input validation using Jakarta Validation
* Global exception handling
* Layered architecture (Controller → Service → Repository)
* Spring Security configuration with filters
* Unit tests using JUnit and Mockito
* Swagger/OpenAPI documentation

---

# 🏗️ System Architecture

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

# 📂 Project Structure

```
src/main/java/com/mo/auth

config
 └── SecurityConfig.java

controller
 ├── AuthController.java
 └── UserController.java

dto
 ├── AuthRequest.java
 ├── AuthResponse.java
 └── ErrorResponse.java

exception
 └── GlobalExceptionHandler.java

model
 └── User.java

repository
 └── UserRepository.java

security
 ├── JwtAuthenticationFilter.java
 └── JwtUtil.java

service
 ├── AuthService.java
 └── UserDetailsServiceImpl.java
```

---

# 🔐 Authentication Flow

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

Spring Security authenticates the credentials.

If valid:

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

# 📡 API Endpoints

## Register

```
POST /api/v1/auth/register
```

Request

```json
{
  "username": "john",
  "password": "password123"
}
```

Response

```
201 Created
User registered successfully
```

---

## Login

```
POST /api/v1/auth/login
```

Request

```json
{
  "username": "john",
  "password": "password123"
}
```

Response

```json
{
  "token": "jwt-token"
}
```

---

## Get Current User (Protected)

```
GET /api/v1/users/me
```

Header

```
Authorization: Bearer <jwt-token>
```

Response

```
Welcome to your profile, john!
```

---

# ⚠️ Error Handling

All errors are handled using a **Global Exception Handler**.

Example error response:

```json
{
  "status": 401,
  "message": "Invalid username or password",
  "timestamp": "2026-03-15T10:30:22"
}
```

Handled exceptions include:

* Duplicate username
* Invalid credentials
* Expired JWT
* Invalid token signature
* Unexpected server errors

---

# 🧪 Testing

The project includes **unit tests for controllers and services**.

Testing stack:

* JUnit 5
* Mockito
* Spring Boot Test
* MockMvc

Tests follow the **AAA pattern**:

```
Arrange
Act
Assert
```

Example test naming:

```
login_validCredentials_returnsToken()
register_existingUsername_throwsException()
```

---

# 🔑 Security Implementation

Security is configured in `SecurityConfig`.

Key settings:

* CSRF disabled (stateless API)
* JWT filter added to security chain
* Authentication required for all endpoints

Public endpoints:

```
/api/v1/auth/**
/swagger-ui/**
/v3/api-docs/**
```

All other endpoints require authentication.

---

# 📄 Swagger API Documentation

Swagger UI allows interactive testing of the API.

Access it at:

```
http://localhost:8080/swagger-ui.html
```

---

# ⚙️ Configuration

Example configuration in `application.properties`:

```
app.jwt.secret=your-super-secret-key
app.jwt.expiration-ms=86400000
```

JWT secret should be **at least 256 bits long** for proper security.

---

# ▶️ Running the Project

### Clone repository

```
git clone https://github.com/yourusername/springboot-jwt-auth.git
```

### Navigate to project

```
cd springboot-jwt-auth
```

### Run application

```
mvn spring-boot:run
```

Application runs on:

```
http://localhost:8080
```

---

# 🛠️ Technologies Used

* Java 17+
* Spring Boot
* Spring Security
* Spring Data JPA
* JWT (JJWT)
* Maven
* Lombok
* Swagger / OpenAPI
* JUnit 5
* Mockito

---

# 📈 Future Improvements

Potential enhancements:

* Role-based authorization (RBAC)
* Refresh tokens
* OAuth2 authentication
* Docker containerization
* Rate limiting
* Email verification
* Integration tests
* CI/CD pipeline

