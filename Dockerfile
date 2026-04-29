# Multi-stage Dockerfile: build with Maven, run with slim OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . /app
# Use non-interactive mode and skip tests for faster builds on CI
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copy the built jar from the builder stage (assumes single executable jar)
COPY --from=builder /app/target/*.jar /app/app.jar
# Render injects PORT env var; Spring Boot reads SERVER_PORT automatically
EXPOSE 10000
ENTRYPOINT ["java","-jar","/app/app.jar","--server.port=${PORT:-10000}"]
