# 🗄️ Database Design

Overview of the data model, migration strategy, and key modeling decisions.

---

## ER Diagram

```mermaid
erDiagram
    users ||--o| regular_users : "inherits (STI)"
    users ||--o| company_users : "inherits (STI)"
    regular_users ||--o{ decisions : "creates"
    regular_users ||--o{ comments : "writes"
    regular_users ||--o{ votes : "casts"
    regular_users ||--o{ regular_user_skills : "has"
    decisions ||--o{ comments : "has"
    decisions ||--o{ votes : "receives"
    decisions ||--o{ decision_tags : "tagged with"
    tags ||--o{ decision_tags : "applied to"
    users ||--o{ medias : "uploads"

    users {
        UUID id PK
        VARCHAR email UK
        VARCHAR password
        VARCHAR role
        BOOLEAN email_verified
        BOOLEAN account_locked
        INT failed_login_attempts
        TIMESTAMP locked_until
        TIMESTAMP created_at
        TIMESTAMP updated_at
        VARCHAR created_by
        VARCHAR updated_by
    }

    regular_users {
        UUID id PK_FK
        VARCHAR username
        TEXT bio
        VARCHAR photo_url
        VARCHAR location
        VARCHAR job_title
        TEXT experience
        TEXT open_to
        VARCHAR website
        VARCHAR github_url
        VARCHAR twitter_url
    }

    company_users {
        UUID id PK_FK
        VARCHAR company_name
    }

    decisions {
        UUID id PK
        VARCHAR title
        TEXT why
        TEXT alternative
        VARCHAR regret_level
        INT vote_count
        UUID user_id FK
    }

    comments {
        UUID id PK
        TEXT content
        UUID user_id FK
        UUID decision_id FK
    }

    votes {
        UUID id PK
        UUID user_id FK
        UUID decision_id FK
    }

    tags {
        UUID id PK
        VARCHAR name UK
    }

    decision_tags {
        UUID decision_id PK_FK
        UUID tag_id PK_FK
    }

    audit_logs {
        UUID id PK
        VARCHAR entity_name
        VARCHAR entity_id
        VARCHAR action
        VARCHAR performed_by
        TEXT details
        VARCHAR ip_address
        TIMESTAMP created_at
    }

    medias {
        UUID id PK
        VARCHAR object_name
        VARCHAR bucket_name
        VARCHAR original_filename
        VARCHAR content_type
        BIGINT size
        VARCHAR status
        UUID uploaded_by_user_id FK
    }
```

---

## Migration History

All schema changes are managed by **Flyway** — no manual DDL, fully version-controlled:

| Version | Description | What it does |
|---------|-------------|--------------|
| `V1` | Initial schema | Creates `users`, `regular_users`, `company_users`, `decisions`, `comments`, `tags`, `decision_tags`, `votes` |
| `V2` | Audit logs | Creates `audit_logs` table with performance indexes |
| `V3` | JPA Auditing | Adds `created_by` / `updated_by` columns to all tables |
| `V4` | User profiles | Adds profile fields (bio, photo, location, skills, social links) to `regular_users` |
| `V5` | Media storage | Creates `medias` table for MinIO file tracking |

---

## Key Design Decisions

### Single Table Inheritance (STI) for Users

Instead of a single `users` table with nullable columns, the schema uses **joined inheritance**:

```
users (base)  →  regular_users (extends)
              →  company_users (extends)
```

**Why?** Regular users and company users have fundamentally different profiles. STI keeps the base `users` table clean while allowing each subtype to have its own columns. JPA's `@Inheritance(strategy = JOINED)` maps this automatically.

### Composite Primary Key for Many-to-Many

`decision_tags` uses a **composite primary key** `(decision_id, tag_id)` instead of a surrogate UUID. This enforces uniqueness at the database level and eliminates duplicate tag assignments.

### Vote Uniqueness Constraint

`votes` has a `UNIQUE(user_id, decision_id)` constraint — one vote per user per decision, enforced at the DB level. No application-level race conditions.

### UUID Primary Keys

All tables use `UUID` instead of auto-increment integers:
- No sequential ID enumeration (security)
- Safe for distributed systems
- No conflicts when merging data

### Audit Trail

Every table has `created_at`, `updated_at`, `created_by`, `updated_by` columns, auto-populated by JPA Auditing (`@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`).

The `audit_logs` table provides a **separate, immutable** record of all CUD operations with indexed columns for fast querying.

---

[← Back to README](../README.md) | [Architecture →](./ARCHITECTURE.md)
