# Multi-stage build for optimized Docker image size

# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

# Metadata
LABEL maintainer="film-catalog-team@example.com"
LABEL description="Film Catalog CRUD Application"

# Working directory
WORKDIR /app

# Copy Maven configuration
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests -B

# Verify JAR is created
RUN ls -la target/

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine AS runtime

# Install additional tools for monitoring and debugging
RUN apk add --no-cache \
    curl \
    wget \
    procps \
    && rm -rf /var/cache/apk/*

# Create user for security (don't run as root)
RUN addgroup -g 1001 appgroup && \
    adduser -u 1001 -G appgroup -s /bin/sh -D appuser

# Working directory
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

# Copy JAR from build stage
COPY --from=builder /app/target/film-catalog-*.jar app.jar

# Change file ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimizations for container
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -Duser.timezone=Europe/Zagreb \
               -Dfile.encoding=UTF-8"

# Startup command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Metadata labels
LABEL version="1.0.0"
LABEL git.commit="${GIT_COMMIT:-unknown}"
LABEL build.date="${BUILD_DATE:-unknown}"
LABEL application="film-catalog"
LABEL environment="production"