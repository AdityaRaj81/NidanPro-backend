# Multi-stage Dockerfile: build with Maven, run with slim OpenJDK 25
FROM maven:4.0.0-jdk-25 AS builder
WORKDIR /app
COPY . /app
# Use non-interactive mode and skip tests for faster builds on CI
RUN mvn -B clean package -DskipTests

FROM openjdk:25-jdk-slim
WORKDIR /app
# Copy the built jar from the builder stage (assumes single executable jar)
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
