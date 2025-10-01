# User Service (Impulse)

This repository contains the User microservice for the Impulse project.

## Overview

A Spring Boot-based User service that provides authentication and user profile management. The service implements JWT-based authentication, event publishing for user lifecycle events, and is container-ready for local development using Docker Compose (MySQL + Redpanda).

This README documents what is implemented so far, how to run the service locally, and how to test the main endpoints.

## Features implemented

- Authentication
  - POST /api/v1/auth/register — register a new user
  - POST /api/v1/auth/login — login (returns accessToken + refreshToken)
  - POST /api/v1/auth/refresh — rotate refresh token and obtain new access token
  - POST /api/v1/auth/logout — invalidate refresh token
- JWT
  - Access token generation and validation
  - `JwtAuthenticationFilter` that validates access tokens and sets the authenticated principal
  - `SecurityConfig` with a `PasswordEncoder` and an AuthenticationEntryPoint
- User profile
  - GET /api/v1/users/me — get authenticated user's profile
  - PUT /api/v1/users/me — update profile (fullname, bio, profileImage, location, dateOfBirth)
  - DELETE /api/v1/users/me — soft-delete (sets status = DELETED)
  - GET /api/v1/users/{id} and GET /api/v1/users/search?q=<query>
- Persistence
  - JPA entities: `User`, `UserProfile` (uses LocalDate for dateOfBirth)
  - Repositories for User and UserProfile
- Events
  - User created/updated/deleted events published via Kafka (Redpanda in docker-compose)
- Containerization
  - Multi-stage `Dockerfile` for building and running the app
  - `docker-compose.yml` to run the app together with MySQL and Redpanda for local testing
- Error handling and validation
  - `GlobalExceptionHandler` (`@RestControllerAdvice`) with handlers for validation errors, access denied, data integrity, not-found and duplicate resource exceptions
  - Custom exceptions: `NotFoundException`, `DuplicateResourceException`
- Security and convenience
  - `JwtAuthenticationFilter` skips `/api/v1/auth/**` so register/login requests are not blocked by invalid tokens
  - Password hashing with BCrypt

## Architecture & tech stack

- Java 21, Spring Boot (3.x)
- Spring Web, Spring Security, Spring Data JPA
- MySQL (via Docker Compose) and HikariCP
- Spring Kafka + Redpanda (local compose) for event publishing
- jjwt (io.jsonwebtoken) for JWT creation/validation
- Maven for build

## Run locally (recommended)

The project includes a `docker-compose.yml` that brings up MySQL and Redpanda and the app (or you can run the app locally and connect to the services).

1) Build the app locally (optional)

```bash
./mvnw -DskipTests clean package
```

2) Start services via Docker Compose

```bash
docker-compose up --build
```

Notes:
- If MySQL host port 3306 is already in use on your machine, the compose file was changed to map MySQL to a different host port (check `docker-compose.yml`).
- Environment variables are used (see `src/main/resources/application.properties`) — you can override them with `docker-compose` or environment overrides.

## Important environment variables

- `SPRING_DATASOURCE_URL` — JDBC URL for MySQL (used by compose/environment)
- `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD`
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` — Kafka bootstrap servers (Redpanda)

JWT configuration
- `JWT_SECRET` or `jwt.secret` — HMAC secret used to sign and validate JWT access/refresh tokens. If not provided an ephemeral key will be used (tokens won't survive restarts). Prefer a long random string (at least 32 bytes) and provide via env var in production.

Make sure your environment or `docker-compose.yml` provides these.

## Endpoints and examples

Replace `localhost:8080` and `<ACCESS_TOKEN>` / `<REFRESH_TOKEN>` as needed.

- Register

```bash
curl -i -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","username":"alice","password":"password"}'
```

- Login

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"password"}' | jq .
```

- Get profile (authenticated)

```bash
curl -i -H "Authorization: Bearer <ACCESS_TOKEN>" http://localhost:8080/api/v1/users/me
```

- Update profile (note: `dateOfBirth` is an ISO date `yyyy-MM-dd`)

```bash
curl -i -X PUT http://localhost:8080/api/v1/users/me \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Alice Liddell","bio":"Hello","dateOfBirth":"1995-06-15"}'
```

- Refresh tokens

```bash
curl -i -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}'
```

- Logout

```bash
curl -i -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}'
```

## Testing and verification

- Database: connect to MySQL and inspect `users` and `user_profiles` tables to verify records
- Kafka: consume the topic used by the user events (e.g., `users.events`) using `kcat` or `rpk` to inspect messages