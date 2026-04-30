# 🔌 API Endpoints — Karar.dev

> **Son Güncelleme**: 2026-04-26  
> **API Version**: v1  
> **Base URL**: `http://localhost:8080/api/v1`

---

## 📋 Genel Bakış

### REST Design Principles

Karar.dev API, **RESTful design principles** takip eder:

1. **Resource-based URLs** — Her resource kendi path'inde
2. **HTTP Verbs** — GET, POST, PUT, DELETE semantik kullanımı
3. **Query Parameter Filtering** — Filtreleme için query params
4. **Nested Resources** — Parent-child ilişkiler için nested paths
5. **Stateless** — Her request bağımsız, JWT authentication

### URL Structure

```
Base: /api/v1/{resource}

Examples:
  /api/v1/decisions              → Collection
  /api/v1/decisions/{id}         → Single resource
  /api/v1/decisions/{id}/comments → Nested resource
  /api/v1/decisions?userId=X     → Filtered collection
```

### Filtering Pattern

```bash
# ✅ Doğru — Query params ile filtreleme
GET /api/v1/comments?decisionId={id}
GET /api/v1/comments?userId={id}
GET /api/v1/comments?decisionId={id}&userId={id}
GET /api/v1/decisions?tagId={id}&regretLevel=HIGH

# ❌ Yanlış — Endpoint duplication
GET /api/v1/comments/decisions/{id}
GET /api/v1/comments/users/{id}
```

---

## 📑 Endpoints

### 1. Authentication (`/api/v1/auth`)

| Method | Endpoint | Açıklama | Auth |
|--------|----------|----------|------|
| POST | `/register` | Yeni kullanıcı kaydı | ❌ Public |

#### POST `/auth/register`

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "username": "johndoe",
  "role": "USER"
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "username": "johndoe",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "status": 201
}
```

---

### 2. Decisions (`/api/v1/decisions`)

| Method | Endpoint | Açıklama | Auth |
|--------|----------|----------|------|
| GET | `/decisions` | Tüm kararlar (filtrelenebilir) | ❌ Public |
| GET | `/decisions/{id}` | Karar detayı | ❌ Public |
| GET | `/decisions/{decisionId}/comments` | Kararın yorumları | ❌ Public |
| GET | `/decisions/{decisionId}/tags` | Kararın etiketleri | ❌ Public |
| POST | `/decisions` | Yeni karar oluştur | ✅ Required |
| PUT | `/decisions/{id}` | Karar güncelle | ✅ Required |
| DELETE | `/decisions/{id}` | Karar sil | ✅ Required |

#### GET `/decisions`

**Query Parameters**:
| Param | Type | Required | Açıklama |
|-------|------|----------|----------|
| `page` | Integer | No | Page number (default: 0) |
| `size` | Integer | No | Page size (default: 10) |
| `sort` | String | No | Sort field,direction (default: createdAt,desc) |
| `userId` | UUID | No | Filter by user ID |
| `regretLevel` | Enum | No | Filter by regret level (LOW, MEDIUM, HIGH) |
| `tagId` | UUID | No | Filter by tag ID |

**Examples**:
```bash
# Tüm kararlar
GET /api/v1/decisions?page=0&size=10&sort=createdAt,desc

# Kullanıcıya göre filtrele
GET /api/v1/decisions?userId=550e8400-e29b-41d4-a716-446655440000

# Pişmanlık seviyesine göre filtrele
GET /api/v1/decisions?regretLevel=HIGH

# Etikete göre filtrele
GET /api/v1/decisions?tagId=550e8400-e29b-41d4-a716-446655440001

# Kombine filtreleme
GET /api/v1/decisions?userId=X&regretLevel=MEDIUM&tagId=Y
```

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Decisions retrieved successfully",
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "title": "Should I learn Spring Boot?",
        "why": "I want to improve my backend skills",
        "alternative": "Django, Node.js",
        "regretLevel": "LOW",
        "voteCount": 42,
        "userId": "550e8400-e29b-41d4-a716-446655440001",
        "username": "johndoe",
        "commentCount": 5,
        "tags": ["java", "spring", "backend"],
        "createdAt": "2026-04-26T10:00:00Z",
        "updatedAt": "2026-04-26T10:00:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": "createdAt,desc"
    },
    "totalElements": 150,
    "totalPages": 15,
    "first": true,
    "last": false
  },
  "status": 200
}
```

---

#### GET `/decisions/{id}`

**Path Parameters**:
| Param | Type | Required | Açıklama |
|-------|------|----------|----------|
| `id` | UUID | Yes | Decision ID |

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Decision found successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Should I learn Spring Boot?",
    "why": "I want to improve my backend skills",
    "alternative": "Django, Node.js",
    "regretLevel": "LOW",
    "voteCount": 42,
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "username": "johndoe",
    "commentCount": 5,
    "tags": ["java", "spring", "backend"],
    "createdAt": "2026-04-26T10:00:00Z",
    "updatedAt": "2026-04-26T10:00:00Z"
  },
  "status": 200
}
```

---

#### GET `/decisions/{decisionId}/comments`

**Path Parameters**:
| Param | Type | Required | Açıklama |
|-------|------|----------|----------|
| `decisionId` | UUID | Yes | Decision ID |

**Query Parameters**:
| Param | Type | Required | Açıklama |
|-------|------|----------|----------|
| `page` | Integer | No | Page number |
| `size` | Integer | No | Page size |
| `sort` | String | No | Sort field |

**Service Call**: `DecisionCommentService.getCommentsByDecisionId()`

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Comments retrieved successfully",
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440002",
        "content": "Great decision! Spring Boot is amazing.",
        "userId": "550e8400-e29b-41d4-a716-446655440003",
        "username": "janedoe",
        "decisionId": "550e8400-e29b-41d4-a716-446655440000",
        "decisionTitle": "Should I learn Spring Boot?",
        "createdAt": "2026-04-26T11:00:00Z",
        "updatedAt": "2026-04-26T11:00:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 5,
    "totalPages": 1
  },
  "status": 200
}
```

---

#### GET `/decisions/{decisionId}/tags`

**Path Parameters**:
| Param | Type | Required | Açıklama |
|-------|------|----------|----------|
| `decisionId` | UUID | Yes | Decision ID |

**Service Call**: `DecisionTagService.getTagsByDecisionId()`

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Tags retrieved successfully",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "java",
      "decisionCount": 25,
      "createdAt": "2026-04-20T10:00:00Z",
      "updatedAt": "2026-04-20T10:00:00Z"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "name": "spring",
      "decisionCount": 30,
      "createdAt": "2026-04-20T10:00:00Z",
      "updatedAt": "2026-04-20T10:00:00Z"
    }
  ],
  "status": 200
}
```

---

#### POST `/decisions`

**Request Body**:
```json
{
  "title": "Should I learn Spring Boot?",
  "why": "I want to improve my backend skills",
  "alternative": "Django, Node.js",
  "regretLevel": "LOW",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "tagIds": [
    "550e8400-e29b-41d4-a716-446655440001",
    "550e8400-e29b-41d4-a716-446655440002"
  ]
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Decision created successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440010",
    "title": "Should I learn Spring Boot?",
    "why": "I want to improve my backend skills",
    "alternative": "Django, Node.js",
    "regretLevel": "LOW",
    "voteCount": 0,
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "johndoe",
    "commentCount": 0,
    "tags": ["java", "spring"],
    "createdAt": "2026-04-26T12:00:00Z",
    "updatedAt": "2026-04-26T12:00:00Z"
  },
  "status": 201
}
```

---

#### PUT `/decisions/{id}`

**Path Parameters**:
| Param | Type | Required |
|-------|------|----------|
| `id` | UUID | Yes |

**Request Body**:
```json
{
  "title": "Should I learn Spring Boot? (Updated)",
  "why": "Backend development is in high demand",
  "alternative": "Quarkus, Micronaut",
  "regretLevel": "MEDIUM",
  "tagIds": [
    "550e8400-e29b-41d4-a716-446655440001",
    "550e8400-e29b-41d4-a716-446655440003"
  ]
}
```

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Decision updated successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440010",
    "title": "Should I learn Spring Boot? (Updated)",
    "why": "Backend development is in high demand",
    "alternative": "Quarkus, Micronaut",
    "regretLevel": "MEDIUM",
    "voteCount": 0,
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "johndoe",
    "commentCount": 0,
    "tags": ["java", "quarkus"],
    "createdAt": "2026-04-26T12:00:00Z",
    "updatedAt": "2026-04-26T12:30:00Z"
  },
  "status": 200
}
```

---

#### DELETE `/decisions/{id}`

**Path Parameters**:
| Param | Type | Required |
|-------|------|----------|
| `id` | UUID | Yes |

**Response** (204 No Content):
```json
{
  "success": true,
  "message": "Decision deleted successfully",
  "data": null,
  "status": 204
}
```

---

### 3. Comments (`/api/v1/comments`)

| Method | Endpoint | Açıklama | Auth |
|--------|----------|----------|------|
| GET | `/comments` | Tüm yorumlar (filtrelenebilir) | ❌ Public |
| GET | `/comments/{id}` | Yorum detayı | ❌ Public |
| POST | `/comments` | Yeni yorum oluştur | ✅ Required |
| PUT | `/comments/{id}` | Yorum güncelle | ✅ Required |
| DELETE | `/comments/{id}` | Yorum sil | ✅ Required |

#### GET `/comments`

**Query Parameters**:
| Param | Type | Required | Açıklama |
|-------|------|----------|----------|
| `page` | Integer | No | Page number |
| `size` | Integer | No | Page size |
| `sort` | String | No | Sort field |
| `decisionId` | UUID | No | Filter by decision ID |
| `userId` | UUID | No | Filter by user ID |

**Examples**:
```bash
# Tüm yorumlar
GET /api/v1/comments?page=0&size=10

# Karara göre filtrele
GET /api/v1/comments?decisionId=550e8400-e29b-41d4-a716-446655440000

# Kullanıcıya göre filtrele
GET /api/v1/comments?userId=550e8400-e29b-41d4-a716-446655440001

# Kombine filtreleme
GET /api/v1/comments?decisionId=X&userId=Y
```

**Service Methods**:
- `CommentService.getAllComments()`
- `CommentService.getCommentsByDecisionId()`
- `CommentService.getCommentsByUserId()`
- `CommentService.getCommentsByDecisionIdAndUserId()`

---

#### POST `/comments`

**Request Body**:
```json
{
  "content": "Great decision! Spring Boot is amazing for enterprise applications.",
  "userId": "550e8400-e29b-41d4-a716-446655440001",
  "decisionId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Comment created successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440020",
    "content": "Great decision! Spring Boot is amazing for enterprise applications.",
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "username": "janedoe",
    "decisionId": "550e8400-e29b-41d4-a716-446655440000",
    "decisionTitle": "Should I learn Spring Boot?",
    "createdAt": "2026-04-26T13:00:00Z",
    "updatedAt": "2026-04-26T13:00:00Z"
  },
  "status": 201
}
```

---

### 4. Votes (`/api/v1/votes`)

| Method | Endpoint | Açıklama | Auth |
|--------|----------|----------|------|
| GET | `/votes` | Tüm oylar (filtrelenebilir) | ❌ Public |
| GET | `/votes/{id}` | Oy detayı | ❌ Public |
| GET | `/votes/decisions/{decisionId}/count` | Oy sayısı + user status | ❌ Public |
| POST | `/votes` | Yeni oy ver | ✅ Required |
| DELETE | `/votes/{id}` | Oy sil (ID ile) | ✅ Required |
| DELETE | `/votes/users/{userId}/decisions/{decisionId}` | Oy geri al | ✅ Required |

#### GET `/votes`

**Query Parameters**:
| Param | Type | Required | Açıklama |
|-------|------|----------|----------|
| `decisionId` | UUID | No | Filter by decision ID |
| `userId` | UUID | No | Filter by user ID |

**Examples**:
```bash
# Tüm oylar
GET /api/v1/votes

# Karara göre filtrele
GET /api/v1/votes?decisionId=550e8400-e29b-41d4-a716-446655440000

# Kullanıcıya göre filtrele
GET /api/v1/votes?userId=550e8400-e29b-41d4-a716-446655440001
```

---

#### GET `/votes/decisions/{decisionId}/count`

**Path Parameters**:
| Param | Type | Required |
|-------|------|----------|
| `decisionId` | UUID | Yes |

**Query Parameters**:
| Param | Type | Required | Açıklama |
|-------|------|----------|----------|
| `currentUserId` | UUID | No | Kullanıcının oy durumu için |

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Vote count retrieved successfully",
  "data": {
    "decisionId": "550e8400-e29b-41d4-a716-446655440000",
    "count": 42,
    "hasVoted": false
  },
  "status": 200
}
```

---

#### POST `/votes`

**Request Body**:
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440001",
  "decisionId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Constraints**:
- Bir kullanıcı bir karara sadece **bir kez** oy verebilir
- Unique constraint: `(user_id, decision_id)`

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Vote created successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440030",
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "username": "janedoe",
    "decisionId": "550e8400-e29b-41d4-a716-446655440000",
    "decisionTitle": "Should I learn Spring Boot?",
    "createdAt": "2026-04-26T14:00:00Z",
    "updatedAt": "2026-04-26T14:00:00Z"
  },
  "status": 201
}
```

---

#### DELETE `/votes/users/{userId}/decisions/{decisionId}`

**Path Parameters**:
| Param | Type | Required |
|-------|------|----------|
| `userId` | UUID | Yes |
| `decisionId` | UUID | Yes |

**Response** (204 No Content):
```json
{
  "success": true,
  "message": "Vote deleted successfully",
  "data": null,
  "status": 204
}
```

---

### 5. Tags (`/api/v1/tags`)

| Method | Endpoint | Açıklama | Auth |
|--------|----------|----------|------|
| GET | `/tags` | Tüm etiketler | ❌ Public |
| GET | `/tags/{id}` | Etiket detayı | ❌ Public |
| GET | `/tags/name/{name}` | Etiket ile ara | ❌ Public |
| POST | `/tags` | Yeni etiket oluştur | ✅ Required |
| PUT | `/tags/{id}` | Etiket güncelle | ✅ Required |
| DELETE | `/tags/{id}` | Etiket sil | ✅ Required |

#### GET `/tags`

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Tags retrieved successfully",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "java",
      "decisionCount": 25,
      "createdAt": "2026-04-20T10:00:00Z",
      "updatedAt": "2026-04-20T10:00:00Z"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "name": "spring",
      "decisionCount": 30,
      "createdAt": "2026-04-20T10:00:00Z",
      "updatedAt": "2026-04-20T10:00:00Z"
    }
  ],
  "status": 200
}
```

---

#### POST `/tags`

**Request Body**:
```json
{
  "name": "microservices"
}
```

**Constraints**:
- Tag name **unique** olmalı
- Name lowercase olarak kaydedilir

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Tag created successfully",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "name": "microservices",
    "decisionCount": 0,
    "createdAt": "2026-04-26T15:00:00Z",
    "updatedAt": "2026-04-26T15:00:00Z"
  },
  "status": 201
}
```

---

### 6. Regular Users (`/api/v1/users`)

| Method | Endpoint | Açıklama | Auth |
|--------|----------|----------|------|
| GET | `/users` | Tüm kullanıcılar | ❌ Public |
| GET | `/users/{id}` | Kullanıcı detayı | ❌ Public |
| GET | `/users/{userId}/comments` | Kullanıcının yorumları | ❌ Public |
| PUT | `/users/{id}` | Kullanıcı güncelle | ✅ Required |
| DELETE | `/users/{id}` | Kullanıcı sil | ✅ Required |

#### GET `/users/{userId}/comments`

**Path Parameters**:
| Param | Type | Required |
|-------|------|----------|
| `userId` | UUID | Yes |

**Query Parameters**:
| Param | Type | Required |
|-------|------|----------|
| `page` | Integer | No |
| `size` | Integer | No |
| `sort` | String | No |

**Service Call**: `UserCommentService.getCommentsByUserId()`

---

### 7. Company Users (`/api/v1/companies`)

| Method | Endpoint | Açıklama | Auth |
|--------|----------|----------|------|
| GET | `/companies` | Tüm şirketler | ❌ Public |
| GET | `/companies/{id}` | Şirket detayı | ❌ Public |
| PUT | `/companies/{id}` | Şirket güncelle | ✅ Required |
| DELETE | `/companies/{id}` | Şirket sil | ✅ Required |

---

## 📊 HTTP Status Codes

| Code | Description | When |
|------|-------------|------|
| 200 | OK | Successful GET, PUT |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation error, invalid input |
| 401 | Unauthorized | Missing or invalid JWT |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate resource (email, title, tag name) |
| 500 | Internal Server Error | Server error |

---

## 🔐 Authentication

### JWT Token Usage

Tüm authenticated endpoint'ler için:

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Example with curl

```bash
curl -X POST http://localhost:8080/api/v1/decisions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My Decision",
    "why": "Because...",
    "regretLevel": "LOW",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "tagIds": ["550e8400-e29b-41d4-a716-446655440001"]
  }'
```

---

## 📝 Pagination

Tüm collection endpoint'leri pagination destekler:

```bash
# Default pagination
GET /api/v1/decisions

# Custom page and size
GET /api/v1/decisions?page=1&size=20

# With sorting
GET /api/v1/decisions?page=0&size=10&sort=voteCount,desc
GET /api/v1/decisions?page=0&size=10&sort=createdAt,asc
```

**Pagination Response**:
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": "createdAt,desc"
  },
  "totalElements": 150,
  "totalPages": 15,
  "first": true,
  "last": false,
  "numberOfElements": 10,
  "empty": false
}
```

---

## 🌐 Swagger UI

Interactive API documentation:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:
```
http://localhost:8080/v3/api-docs
```

---

## 📚 Related Documents

- [project-idea.md](project-idea.md) — Proje genel bakış
- [08-dtos.md](08-dtos.md) — DTO yapıları
- [06-exception-handling.md](06-exception-handling.md) — Error responses
- [11-swagger.md](11-swagger.md) — Swagger documentation

---

**© 2026 Karar.dev Team**
