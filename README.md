<div align="center">

# ⚖️ karar.dev

**A social platform where people share their life decisions, explain why they made them, and let the community vote and discuss.**

Built with **Spring Boot 4** · **PostgreSQL** · **Kafka** · **Redis** · **MinIO**

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)](https://kafka.apache.org/)
[![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

</div>

---

## 📖 What is karar.dev?

**karar.dev** (Turkish: *karar* = decision) is a community-driven platform where users can:

- 📝 **Share decisions** they've made in life — career changes, tech choices, moving to a new city, etc.
- 💡 **Explain the reasoning** behind each decision and any alternatives they considered
- 😔 **Rate their regret level** (Low / Medium / High)
- 🗳️ **Vote** on other people's decisions
- 💬 **Comment** and discuss
- 🏷️ **Tag** decisions for discoverability

Think of it as a "Stack Overflow for life decisions" — a place to learn from other people's choices.

---

## 🏗️ Tech Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 17 | Language |
| **Spring Boot** | 4.0.3 | Application framework |
| **Spring Security** | 6.x | Authentication & authorization (JWT + RBAC) |
| **Spring Data JPA** | — | ORM & data access |
| **PostgreSQL** | 18 | Primary database |
| **Flyway** | — | Database migration management |
| **Redis** | 7 | Caching layer |
| **Apache Kafka** | — | Async event processing (email verification) |
| **MinIO** | 8.5 | Object storage for media uploads |
| **Apache Tika** | 2.9 | File content-type detection |
| **Lombok** | 1.18 | Boilerplate reduction |
| **SpringDoc OpenAPI** | 3.0 | Auto-generated Swagger docs |
| **Thymeleaf** | — | Email HTML templates |

### Infrastructure

| Technology | Purpose |
|-----------|---------|
| **Docker + Docker Compose** | Containerized development & deployment |
| **Multi-stage Dockerfile** | Optimized production image (JDK build → JRE runtime) |
| **Mailpit** | Local SMTP testing with web UI |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Docker & Docker Compose

### 1. Start Infrastructure Services

```bash
cd karar.dev
docker compose up -d db redis kafka minio mailpit
```

This starts PostgreSQL, Redis, Kafka, MinIO, and Mailpit.

### 2. Run the Backend

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.  
Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Useful URLs

| Service | URL |
|---------|-----|
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Mailpit (email testing) | http://localhost:8025 |
| MinIO Console | http://localhost:9001 |

---

## 📂 Project Structure

```
karardev/
├── karar.dev/                 # Backend (Spring Boot)
│   ├── src/main/java/
│   │   └── org/karar/dev/
│   │       ├── common/        # Shared: security, exceptions, configs, audit
│   │       └── domain/        # Business domains
│   │           ├── auth/      # Authentication (register, login, email verify)
│   │           ├── decision/  # Core: decisions with regret levels
│   │           ├── comment/   # Comments on decisions
│   │           ├── vote/      # Voting system
│   │           ├── tag/       # Categorization tags
│   │           ├── user/      # Users (Regular + Company)
│   │           ├── media/     # File uploads (MinIO)
│   │           └── audit/     # Audit logging
│   ├── src/main/resources/
│   │   ├── db/migration/      # Flyway SQL migrations
│   │   └── application-*.yml  # Profile-based configs (local/dev/prod)
│   ├── Dockerfile             # Multi-stage production build
│   ├── docker-compose.yml     # Full dev environment
│   └── docs-prod/             # Detailed documentation
```

---

## 🔑 Key Features

### Authentication & Security
- JWT-based auth with access + refresh token rotation
- Async email verification via Kafka → SMTP pipeline
- Role-based access control (User / Admin)
- Account locking after failed login attempts

### API Design
- Content-negotiation versioning (`Accept: application/vnd.karar.dev+json;v=1.0`)
- Unified `BaseResponse<T>` envelope for all responses
- Global exception handling with structured error codes
- Pagination & dynamic filtering via query parameters

### Data Integrity
- Flyway-managed database migrations (5 versioned migrations)
- JPA Auditing: auto-populated `createdAt`, `updatedAt`, `createdBy`, `updatedBy`
- AOP-based audit logging — every CUD operation is tracked with user & IP

### Architecture
- Domain-driven package layout with clean separation of concerns
- Single Table Inheritance for polymorphic user types
- Event-driven email flow: `AuthService → Kafka → EmailConsumer → MailService`
- Strategy pattern for token management

---

## 🗄️ Database Schema

```
users ──┬── regular_users (username, bio, profile photo)
        └── company_users (company_name)

decisions (title, why, alternative, regret_level, vote_count)
    ├── comments (content, user_id, decision_id)
    ├── decision_tags ←→ tags (many-to-many)
    └── votes (user_id + decision_id unique)

audit_logs (entity, action, performed_by, ip_address, details)
medias (filename, content_type, size, url, folder)
```

Migrations are in `src/main/resources/db/migration/` (V1 through V5).

---

## 🐳 Docker

### Multi-Stage Build

The Dockerfile uses a two-stage approach for minimal production images:

1. **Builder stage** — `eclipse-temurin:17-jdk-alpine` compiles the JAR
2. **Runtime stage** — `eclipse-temurin:17-jre-alpine` runs it as a non-root user

```bash
# Build & run everything
docker compose up --build

# Or just the infrastructure
docker compose up -d db redis kafka minio mailpit
```

---

## ⚙️ Configuration

The app uses Spring profiles for environment-specific settings:

| Profile | Database | Logging | Use Case |
|---------|----------|---------|----------|
| `local` | H2 (in-memory) | DEBUG | Quick prototyping |
| `dev` | PostgreSQL | DEBUG + file output | Development |
| `prod` | PostgreSQL | WARN + file output | Production |

All secrets are externalized via environment variables (`.env.dev`).

---

## 📚 Documentation

For deeper dives, check the `docs-prod/` folder:

| Document | Description |
|----------|-------------|
| [Architecture](./docs-prod/ARCHITECTURE.md) | System design, package structure, design patterns |
| [API Reference](./docs-prod/API.md) | All endpoints with auth levels and response formats |
| [Database Design](./docs-prod/DATABASE.md) | ER diagram, migration history, data modeling decisions |
| [Deployment Guide](./docs-prod/DEPLOYMENT.md) | Docker setup, environment variables, Spring profiles |

---

## 🧪 Testing

The project includes tests organized by domain:

```
src/test/java/org/karar/dev/domain/
├── auth/        # Authentication tests
├── comment/     # Comment service tests
├── decision/    # Decision CRUD tests
├── media/       # Media upload tests
├── tag/         # Tag tests
├── user/        # User tests
├── vote/        # Vote tests
└── extensions/  # Custom JUnit extensions (MediaParameterResolver)
```

```bash
./mvnw test
```

---

## 📄 License

This project is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0).

---

<div align="center">

**Built with ❤️ by [Sarvar](https://github.com/sarvar55)**

</div>
