User Service - Containerization

This module packages a Spring Boot service with MySQL and Kafka (Redpanda) for local dev.

Prereqs
- Docker Desktop
- Java 21 & Maven (optional if you use docker compose only)

Configure
Configuration is environment-driven. Defaults are sensible for local dev, but can be overridden.

Key env vars (with defaults):
- DB_HOST=localhost, DB_PORT=3306, DB_NAME=userdb, DB_USERNAME=root, DB_PASSWORD=root
- KAFKA_BOOTSTRAP_SERVERS=localhost:9092
- SPRING_JPA_HIBERNATE_DDL_AUTO=update

Build a local jar (optional)
From the User folder, run:
  ./mvnw.cmd -q -DskipTests package

Build and run with Docker Compose
From the User folder, run:
  docker compose up -d --build
  docker compose logs -f user-service
  docker compose down -v

The app will be available at http://localhost:8080

Notes
- Kafka uses Redpanda single-broker for simplicity. External listener is at localhost:9092; internal at redpanda:29092.
- MySQL data is persisted via a named volume mysql_data.
- Tokens and secrets should be externalized for production (use a secret store).
