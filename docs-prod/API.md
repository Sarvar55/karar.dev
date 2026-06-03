# 📡 API Reference

All endpoints use the `/api` prefix and return a unified `BaseResponse<T>` envelope.

> **Interactive docs:** [Swagger UI](http://localhost:8080/swagger-ui.html)

---

## Response Format

```json
// Success
{ "success": true, "data": { ... }, "status": "OK", "timestamp": "..." }

// Error
{ "success": false, "error": { "code": "...", "message": "..." }, "status": "NOT_FOUND" }
```

---

## Endpoints Overview

### 🔑 Auth (`/auth`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/auth/register` | Public | Register a new user (sends verification email via Kafka) |
| POST | `/auth/login` | Public | Login → returns JWT access + refresh tokens |
| POST | `/auth/refresh` | Public | Refresh an expired access token |
| GET | `/auth/verify?token=` | Public | Verify email address |
| POST | `/auth/resend-verification` | Public | Resend verification email |

### 📝 Decisions (`/decisions`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/decisions` | Public | List all (paginated, filterable by userId/regretLevel/tagId) |
| GET | `/decisions/{id}` | Public | Get single decision |
| POST | `/decisions` | User | Create a decision |
| PUT | `/decisions/{id}` | User | Update a decision |
| DELETE | `/decisions/{id}` | User | Delete a decision (cascades comments & votes) |
| GET | `/decisions/{id}/comments` | Public | List comments for a decision |
| GET | `/decisions/{id}/tags` | Public | List tags for a decision |

### 💬 Comments (`/comments`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/comments` | Public | List all comments (paginated) |
| POST | `/comments` | User | Create a comment |
| PUT | `/comments/{id}` | User | Update a comment |
| DELETE | `/comments/{id}` | User | Delete a comment |

### 🗳️ Votes (`/votes`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/votes/decisions/{id}/count` | Public | Get vote count (+ check if current user voted) |
| POST | `/votes` | User | Cast a vote (one per user per decision) |
| DELETE | `/votes/users/{uid}/decisions/{did}` | User | Remove a vote |

### 🏷️ Tags (`/tags`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/tags` | Public | List all tags |
| GET | `/tags/{id}` | Public | Get a tag |
| POST | `/tags` | Admin | Create a tag |
| DELETE | `/tags/{id}` | Admin | Delete a tag |

### 👤 Users (`/users`) & 🏢 Companies (`/companies`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/users` | Public | List users (paginated) |
| GET | `/users/{id}` | Public | Get user profile |
| PUT | `/users/{id}` | User | Update own profile |
| GET | `/users/{id}/comments` | Public | List user's comments |
| GET | `/companies` | Public | List company accounts |

### 📸 Media (`/media`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/media/upload` | User | Upload a file to MinIO (multipart/form-data) |

### 📋 Audit Logs (`/audit-logs`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/api/audit-logs` | Admin | List all audit logs (paginated) |

---

[← Back to README](../README.md) | [Architecture →](./ARCHITECTURE.md)
