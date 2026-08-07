# syntax=docker/dockerfile:1

# --- Build stage -------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy the descriptor first so dependency resolution is cached across code changes.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Runtime stage -----------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
COPY --from=build /build/target/*.jar app.jar
USER app

EXPOSE 8080

# MaxRAMPercentage keeps the heap inside small container limits (Render free = 512MB).
ENTRYPOINT ["sh", "-c", "java -XX:MaxRAMPercentage=75 -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
