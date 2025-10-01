# syntax=docker/dockerfile:1.7

# ============ Build stage ============
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -q --no-transfer-progress -DskipTests package && \
    mkdir -p /workspace/target && \
    ls -la target

# ============ Runtime stage ============
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd -r -u 1001 spring && chown -R spring /app
USER spring

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
