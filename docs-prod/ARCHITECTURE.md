# 🏛️ Architecture Overview

High-level overview of the karar.dev system architecture and key design decisions.

---

## System Diagram

```
┌─────────────────────────────────────────────┐
│           karar-ui (Next.js 16)             │
│         React 19 · TypeScript · TailwindCSS │
└──────────────────┬──────────────────────────┘
                   │  REST / JSON
                   ▼
┌─────────────────────────────────────────────┐
│          karar.dev (Spring Boot 4)          │
│  ┌───────────────────────────────────────┐  │
│  │  Security: JWT + RBAC Filter Chain    │  │
│  ├───────────────────────────────────────┤  │
│  │  Controllers → Services → Repos      │  │
│  ├───────────────────────────────────────┤  │
│  │  Cross-cutting: AOP Audit, Exceptions│  │
│  └───────────────────────────────────────┘  │
├─────────────┬──────┬──────┬────────┬────────┤
│ PostgreSQL  │Redis │Kafka │ MinIO  │  SMTP  │
│  (data)     │(cache)│(events)│(files)│(email) │
└─────────────┴──────┴──────┴────────┴────────┘
```

---

## Package Structure

The backend follows a **domain-based package layout**. Each business area lives in its own isolated module:

```
org.karar.dev
├── common/              # Shared infrastructure
│   ├── audit/           # @Auditable AOP aspect
│   ├── config/          # App configs (Kafka, Redis, OpenAPI, Web, etc.)
│   ├── dto/             # BaseResponse<T>, PageResponse
│   ├── entity/          # BaseEntity (UUID + audit timestamps)
│   ├── exception/       # GlobalExceptionHandler + custom exceptions
│   ├── notification/    # Email service (Thymeleaf templates)
│   └── security/        # JWT filter, RBAC, token management
│
├── domain/
│   ├── auth/            # Register, Login, Email verification, Token refresh
│   ├── decision/        # Core domain — CRUD + filtering + tags
│   ├── comment/         # User comments on decisions
│   ├── vote/            # One-vote-per-user system
│   ├── tag/             # Categorization tags
│   ├── user/            # User management (Regular + Company via STI)
│   ├── media/           # File uploads via MinIO
│   └── audit/           # Audit log storage & querying
```

---

## Key Design Patterns

| Pattern | Where | Why |
|---------|-------|-----|
| **Domain-Driven Design** | Package layout | Each bounded context is self-contained |
| **Single Table Inheritance** | User entity | `RegularUser` and `CompanyUser` share one table |
| **AOP (Aspect-Oriented)** | `@Auditable` annotation | Audit logging without touching business logic |
| **Event-Driven** | Auth → Kafka → Email | Async email verification after registration |
| **Strategy Pattern** | Token management | Separate access/refresh token strategies |
| **Builder Pattern** | `BaseResponse<T>` | Consistent API response envelope |

---

## API Versioning

Uses **content-negotiation** via the `Accept` header instead of URL-based versioning:

```
Accept: application/vnd.karar.dev+json;v=1.0
```

This leverages Spring Boot 4's native `ApiVersionConfigurer` and keeps URLs clean.

---

## Security Flow

1. Client sends `POST /api/auth/login` → receives JWT pair (access + refresh)
2. Every request includes `Authorization: Bearer <token>`
3. `AuthenticationTokenFilter` validates the token and sets `SecurityContext`
4. `SecurityPathConfig` enforces RBAC rules per endpoint
5. On 401, client auto-refreshes via `/api/auth/refresh`

---

[← Back to README](../README.md)
