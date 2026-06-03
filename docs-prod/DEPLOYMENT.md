# 🚀 Deployment Guide

How to build, configure, and run karar.dev in different environments.

---

## Environment Overview

| Environment | Database | Cache | Events | Files | Email |
|------------|----------|-------|--------|-------|-------|
| **local** | H2 (in-memory) | — | — | — | — |
| **dev** | PostgreSQL 18 | Redis 7 | Kafka | MinIO | Mailpit |
| **prod** | PostgreSQL 18 | Redis 7 | Kafka | MinIO | Real SMTP |

---

## Docker Compose (Development)

The `docker-compose.yml` defines the full development stack:

```yaml
services:
  app        # Spring Boot application (port 8080)
  db         # PostgreSQL 18 (port 5433 → 5432)
  redis      # Redis 7 Alpine (port 6379)
  kafka      # Apache Kafka KRaft mode (port 9092)
  minio      # MinIO object storage (API: 9000, Console: 9001)
  mailpit    # SMTP testing (SMTP: 1025, Web UI: 8025)
```

### Quick Start

```bash
# Start all infrastructure services
docker compose up -d db redis kafka minio mailpit

# Run the backend locally (connects to Docker services)
./mvnw spring-boot:run

# Or run everything in Docker
docker compose up --build
```

---

## Multi-Stage Docker Build

The `Dockerfile` uses a two-stage build for minimal, secure production images:

```
Stage 1: Builder (eclipse-temurin:17-jdk-alpine)
  ├── Copy Maven wrapper + pom.xml
  ├── Download dependencies (cached layer)
  ├── Copy source code
  └── Build JAR (skip tests)

Stage 2: Runtime (eclipse-temurin:17-jre-alpine)
  ├── Create non-root user (appuser:appgroup)
  ├── Copy JAR from builder
  ├── Set read-only permissions
  └── Run with container-aware JVM flags
```

**Security measures:**
- Non-root user (`appuser` UID 1001)
- Read-only filesystem (`chmod 555`)
- JRE-only runtime (no compiler in production)

**JVM flags:**
- `-XX:+UseContainerSupport` — respect container memory limits
- `-XX:MaxRAMPercentage=75.0` — use 75% of container memory

---

## Environment Variables

All secrets and configuration are externalized. No hardcoded values in production.

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `PROFILE` | Spring profile | `dev` / `prod` |
| `DB_URL` | JDBC connection string | `jdbc:postgresql://db:5432/karardb` |
| `DB_USER` | Database username | `user` |
| `DB_PASSWORD` | Database password | `*****` |
| `JWT_SECRET` | JWT signing key (≥32 bytes) | `your-production-secret-key` |
| `JWT_EXPIRATION` | Access token TTL (ms) | `3600000` (1 hour) |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origin | `https://karar.dev` |

### Optional Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | `8080` | Server port |
| `REDIS_HOST` | `localhost` | Redis hostname |
| `REDIS_PORT` | `6379` | Redis port |
| `KAFKA_SERVERS` | `localhost:9092` | Kafka bootstrap servers |
| `MAIL_HOST` | `localhost` | SMTP server |
| `MAIL_PORT` | `1025` | SMTP port |

---

## Spring Profiles

### `local` — Quick prototyping
- H2 in-memory database (no setup needed)
- SQL logging enabled
- No external services required

### `dev` — Full development
- PostgreSQL with Flyway migrations
- Redis caching
- Kafka event processing
- MinIO file storage
- Mailpit for email testing
- DEBUG-level logging with file output (50MB × 10 files)

### `prod` — Production
- PostgreSQL with `ddl-auto: validate` (Flyway only)
- SQL logging disabled
- WARN-level logging with file output (100MB × 50 files, 2GB cap)

---

## Logging Strategy

| Profile | Console | File | Rotation |
|---------|---------|------|----------|
| local | DEBUG, colorized | — | — |
| dev | DEBUG, colorized | `logs/karar-dev.log` | 50MB × 10 files |
| prod | WARN only | `logs/karar-prod.log` | 100MB × 50 files (2GB total) |

Log pattern includes timestamp, thread, level, logger with line number, and message.

---

## Health Checks

Docker Compose includes health checks for critical services:

- **PostgreSQL:** `pg_isready` command (10s interval, 5 retries)
- **MinIO:** HTTP health endpoint (`/minio/health/live`)
- **App:** Depends on DB health before starting

---

[← Back to README](../README.md) | [Database →](./DATABASE.md)
