# user_service

This is the User Service for the Impulse project. It provides user registration, authentication, profile management, and user search APIs using Spring Boot, JWT, and MySQL.

## Features
- User registration and login
- JWT-based authentication (access & refresh tokens)
- Profile management (view, update, soft-delete)
- User search (authenticated)
- Refresh token rotation and invalidation
- Kafka event publishing for user events

## API Endpoints

### Auth
- `POST /api/v1/auth/register` — Register a new user
- `POST /api/v1/auth/login` — Login and receive tokens
- `POST /api/v1/auth/refresh` — Get new access token using refresh token

### Profile
- `GET /api/v1/users/me` — Get your profile (requires Authorization header)
- `PUT /api/v1/users/me` — Update your profile
- `DELETE /api/v1/users/me` — Soft-delete your account

### User
- `GET /api/v1/users/{id}` — Get user by ID (requires authentication)
- `GET /api/v1/users/search?q=...` — Search users (requires authentication)

## Authentication
- All endpoints except `/api/v1/auth/**` require a valid JWT access token in the `Authorization: Bearer <token>` header.
- Use the refresh token with `/api/v1/auth/refresh` to obtain a new access token when the old one expires.

## Logout
- By default, logout is handled client-side: simply delete tokens from storage.
- If you want server-side refresh token invalidation, use `POST /api/v1/auth/logout` with `{ "refreshToken": "..." }` in the body.

## Running the Service

1. **Configure MySQL and Kafka (Redpanda):**
   - Use the provided `docker-compose.yml` to start dependencies.

2. **Build and run:**
   ```sh
   ./mvnw clean package
   java -jar target/user_service-0.0.1-SNAPSHOT.jar
   ```

3. **Environment variables:**
   - Set `JWT_SECRET` for token signing (or use the default in `application.properties`).
   - Set `KAFKA_BOOTSTRAP_SERVERS` if not using the default.

## Example: Refresh Token Flow
1. Login to get `accessToken` and `refreshToken`.
2. When `accessToken` expires, call:
   ```sh
   curl -X POST http://localhost:8080/api/v1/auth/refresh \
     -H "Content-Type: application/json" \
     -d '{"refreshToken":"<your-refresh-token>"}'
   ```
3. Use the new `accessToken` for authenticated requests.

## Notes
- All user data is soft-deleted (status set to DELETED) for account deletion.
- User search is only available to authenticated users.
- Kafka events are published for user create, update, and delete actions.
