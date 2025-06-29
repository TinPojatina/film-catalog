# Multi-stage build za optimizaciju veličine Docker image-a

# Stage 1: Build stage
FROM maven:3.9-openjdk-17-slim AS builder

# Metadata
LABEL maintainer="film-catalog-team@example.com"
LABEL description="Film Catalog CRUD Application"

# Radni direktorij
WORKDIR /app

# Kopiraj Maven konfiguraciju
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Download dependencies (cache layer)
RUN mvn dependency:go-offline -B

# Kopiraj izvorni kod
COPY src ./src

# Build aplikacije
RUN mvn clean package -DskipTests -B

# Provjeri da li je JAR kreiran
RUN ls -la target/

# Stage 2: Runtime stage
FROM openjdk:17-jdk-slim AS runtime

# Instalacija dodatnih alata za monitoring i debugging
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    procps \
    && rm -rf /var/lib/apt/lists/*

# Kreiranje korisnika za sigurnost (ne pokretati kao root)
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Radni direktorij
WORKDIR /app

# Kreiranje direktorija za logove
RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

# Kopiraj JAR iz build stage-a
COPY --from=builder /app/target/film-catalog-*.jar app.jar

# Promijeni vlasništvo fajlova
RUN chown -R appuser:appgroup /app

# Prebaci na non-root korisnika
USER appuser

# Ekspoziraj port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimizacije za container
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+PrintGCDetails \
               -XX:+PrintGCTimeStamps \
               -Xloggc:/app/logs/gc.log \
               -Duser.timezone=Europe/Zagreb \
               -Dfile.encoding=UTF-8"

# Startup command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Alternativno pokretanje s exec format (preporučeno)
# ENTRYPOINT ["java", "-jar", "app.jar"]

# Metadata labels
LABEL version="1.0.0"
LABEL git.commit="${GIT_COMMIT:-unknown}"
LABEL build.date="${BUILD_DATE:-unknown}"
LABEL application="film-catalog"
LABEL environment="production"