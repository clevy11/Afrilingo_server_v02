# Multi-stage Dockerfile for Afrilingo Spring Boot app (Java 21)

# ===== Build stage =====
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Pre-copy pom.xml and download dependencies for better layer caching
COPY pom.xml ./
RUN mvn -q -e -DskipTests dependency:go-offline

# Copy source and build the application
COPY src ./src
RUN mvn -q -DskipTests package

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre

# Create non-root user for security
RUN useradd -ms /bin/bash appuser
USER appuser

WORKDIR /app

# Copy the Spring Boot fat jar from the build stage
# Use a generic pattern to avoid hard-coding the version
COPY --from=build /app/target/*.jar app.jar

# Default environment variables (can be overridden at runtime)
ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod

# Expose application port (matches server.port in application.properties)
EXPOSE 8081

# Healthcheck (optional, adjust path if needed)
# HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
#   CMD wget -qO- http://localhost:8081/actuator/health | grep 'UP' || exit 1

# Use exec form to pass signals properly; allow custom JVM opts
ENTRYPOINT ["/bin/sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
