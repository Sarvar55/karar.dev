# 📖 Karar.dev — Proje Dokümantasyonu

> **Son Güncelleme**: 2026-04-26  
> **Versiyon**: 1.0.0

---

## 🎯 Proje Nedir?

**Karar.dev**, kullanıcıların hayatlarında aldıkları kararları, bu kararların nedenlerini ve alternatiflerini paylaşabilecekleri; diğer kullanıcıların bu kararları oylayabileceği ve yorum yapabileceği bir **sosyal karar paylaşım platformudur**.

### 💡 İlham Kaynağı

> "En büyük pişmanlık, karar vermemektir." 

Karar.dev, insanların karar alma süreçlerini şeffaf hale getirerek:
- Başkalarının deneyimlerinden öğrenmesini
- Karar verme süreçlerini反思 etmesini
- Topluluk desteğiyle daha iyi kararlar almasını sağlar

---

## 👨‍💻 Kim Tarafından Yazıldı?

| Rol | İsim | Katkı |
|-----|------|-------|
| **Lead Developer** | Sarvar | Backend Architecture, API Design, Database Schema |
| **Contributors** | Karar.dev Team | Code Review, Testing |

**Geliştirme Tarihi**: Mart 2024 - Devam Ediyor

---

## 🛠️ Tech Stack

### Core Technologies

```
┌─────────────────────────────────────────────────────────┐
│                    Java 17 LTS                          │
│         (Modern, performant, long-term support)         │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                  Spring Boot 4.0.3                      │
│        (Rapid development, convention over config)      │
└─────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│  Spring MVC   │   │  Spring JPA   │   │  Spring Sec   │
│   (REST API)  │   │  (Data Layer) │   │  (Security)   │
└───────────────┘   └───────────────┘   └───────────────┘
```

### Full Technology List

| Kategori | Teknoloji | Versiyon | Amaç |
|----------|-----------|----------|------|
| **Dil** | Java | 17 | Backend programlama |
| **Framework** | Spring Boot | 4.0.3 | Uygulama çatısı |
| **Web** | Spring MVC | 4.0.3 | RESTful API |
| **Data** | Spring Data JPA | 4.0.3 | ORM ve data access |
| **Validation** | Hibernate Validator | 8.x | Input validation |
| **Security** | Spring Security | 6.x | Authentication & Authorization |
| **Database (Dev)** | H2 | 2.x | Geliştirme veritabanı |
| **Database (Prod)** | PostgreSQL | 15.x | Production veritabanı |
| **Documentation** | SpringDoc OpenAPI | 3.0.2 | Swagger UI |
| **Build Tool** | Maven | 3.9.x | Dependency & build management |
| **Utilities** | Lombok | 1.18.x | Boilerplate reduction |
| **Testing** | JUnit 5 + Spring Test | 5.x | Unit & integration tests |

### Neden Bu Teknolojiler?

| Teknoloji | Seçilme Nedeni |
|-----------|----------------|
| **Java 17** | LTS sürüm, modern özellikler (records, sealed classes), güçlü tip güvenliği |
| **Spring Boot** | Hızlı geliştirme, geniş ekosistem, production-ready |
| **JPA/Hibernate** | Object-relational mapping, lazy loading, cache desteği |
| **PostgreSQL** | ACID uyumlu, JSONB desteği, yüksek performans |
| **Maven** | Dependency management, multi-module support,成熟生态系统 |

---

## 🏗️ Scalable Yapı

### 📐 Mimari Prensipler

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                      │
│                    (REST Controllers)                       │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP Request/Response
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                      Business Layer                         │
│                  (Services + Transactions)                  │
└─────────────────────┬───────────────────────────────────────┘
                      │ Business Logic
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                     Persistence Layer                       │
│                 (Repositories + JPA/Hibernate)              │
└─────────────────────┬───────────────────────────────────────┘
                      │ SQL Queries
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                       Database Layer                        │
│                    (PostgreSQL / H2)                        │
└─────────────────────────────────────────────────────────────┘
```

### 🔧 Scalability Stratejileri

#### 1. **Katmanlı Mimari (Layered Architecture)**
- Her katman tek bir sorumluluğa sahip
- Kolay test edilebilir
- Değişiklikler izole edilir

#### 2. **Domain-Driven Design (DDD) Lite**
- Her domain bağımsız modül olarak organize
- Cross-domain dependency minimum
- Ortak kod `common/` altında

#### 3. **Stateless API**
- RESTful, stateless endpoints
- JWT-based authentication
- Horizontal scaling-friendly

#### 4. **Lazy Loading & Pagination**
- Büyük veri setleri için pagination
- Lazy loading ile performans optimizasyonu
- N+1 query problemi önleme

#### 5. **Connection Pooling**
- HikariCP ile database connection pooling
- Efficient resource management

### 📈 Gelecek Genişletme Noktaları

```yaml
Phase 1 (MVP):
  ✅ REST API
  ✅ JWT Authentication
  ✅ Basic CRUD operations
  ✅ H2/PostgreSQL support

Phase 2 (Scalability):
  ⏳ Redis caching
  ⏳ Elasticsearch (search functionality)
  ⏳ RabbitMQ/Kafka (async processing)
  ⏳ Docker + Kubernetes

Phase 3 (Advanced):
  ⏳ Microservices architecture
  ⏳ Event sourcing
  ⏳ CQRS pattern
  ⏳ GraphQL API
```

---

## ⚙️ Functional Requirements (Fonksiyonel Gereksinimler)

### FR-1: Kullanıcı Yönetimi

| ID | Özellik | Açıklama |
|----|---------|----------|
| FR-1.1 | Kayıt Ol | Kullanıcı email, şifre ile kayıt olabilir |
| FR-1.2 | Giriş Yap | Kullanıcı JWT token ile giriş yapar |
| FR-1.3 | Profil Görüntüleme | Kullanıcı kendi profilini görebilir |
| FR-1.4 | Profil Güncelleme | Kullanıcı bilgilerini güncelleyebilir |
| FR-1.5 | Hesap Silme | Kullanıcı hesabını silebilir |

### FR-2: Karar Yönetimi

| ID | Özellik | Açıklama |
|----|---------|----------|
| FR-2.1 | Karar Oluştur | Başlık, neden, alternatif, pişmanlık seviyesi ile karar oluştur |
| FR-2.2 | Karar Listeleme | Tüm kararları pagination ile listele |
| FR-2.3 | Karar Filtreleme | User, tag, regret level ile filtrele |
| FR-2.4 | Karar Güncelleme | Kendi kararını güncelle |
| FR-2.5 | Karar Silme | Kendi kararını sil |

### FR-3: Oylama Sistemi

| ID | Özellik | Açıklama |
|----|---------|----------|
| FR-3.1 | Oy Ver | Karara oy ver (bir kullanıcı bir karara sadece bir kez) |
| FR-3.2 | Oy Sayısı | Kararın toplam oy sayısını görüntüle |
| FR-3.3 | Oy Durumu | Kullanıcının oy verip vermediğini kontrol et |
| FR-3.4 | Oy Geri Alma | Verilen oyu geri al |

### FR-4: Yorum Sistemi

| ID | Özellik | Açıklama |
|----|---------|----------|
| FR-4.1 | Yorum Yap | Karara yorum ekle |
| FR-4.2 | Yorum Listeleme | Kararın yorumlarını listele |
| FR-4.3 | Yorum Güncelleme | Kendi yorumunu güncelle |
| FR-4.4 | Yorum Silme | Kendi yorumunu sil |

### FR-5: Etiket Sistemi

| ID | Özellik | Açıklama |
|----|---------|----------|
| FR-5.1 | Etiket Oluştur | Yeni etiket oluştur |
| FR-5.2 | Etiket Ata | Karara etiket ata |
| FR-5.3 | Etiket ile Filtrele | Etikete göre kararları filtrele |
| FR-5.4 | Etiket Yönetimi | Etiket güncelleme ve silme |

---

## 🎯 Non-Functional Requirements (Fonksiyonel Olmayan Gereksinimler)

### NFR-1: Performans

| Metrik | Hedef |
|--------|-------|
| API Response Time | < 200ms (p95) |
| Database Query Time | < 50ms (ortalama) |
| Concurrent Users | 1000+ eşzamanlı kullanıcı |
| Throughput | 100+ request/second |

### NFR-2: Güvenlik

| Gereksinim | Açıklama |
|------------|----------|
| Authentication | JWT-based authentication |
| Authorization | Role-based access control (RBAC) |
| Password Security | BCrypt hashing |
| Input Validation | Tüm input'lar validate edilmeli |
| SQL Injection | JPA ile önlendi |
| XSS Protection | Spring Security ile koruma |

### NFR-3: Güvenilirlik

| Gereksinim | Hedef |
|------------|-------|
| Uptime | %99.9 availability |
| Error Rate | < 0.1% error rate |
| Data Integrity | ACID compliance |
| Backup | Günlük yedekleme |

### NFR-4: Ölçeklenebilirlik

| Gereksinim | Açıklama |
|------------|----------|
| Horizontal Scaling | Load balancer ile çoklu instance |
| Database Scaling | Read replicas support |
| Caching | Redis integration ready |
| Async Processing | Message queue ready |

### NFR-5: Maintainability

| Gereksinim | Hedef |
|------------|-------|
| Code Coverage | > 80% test coverage |
| Documentation | 100% API documentation |
| Code Quality | SonarQube A rating |
| CI/CD | Automated pipeline |

### NFR-6: Usability

| Gereksinim | Açıklama |
|------------|----------|
| API Documentation | Interactive Swagger UI |
| Error Messages | Clear, actionable error messages |
| Consistency | RESTful design patterns |
| Versioning | API versioning support (v1, v2, ...) |

---

## 📜 Prensiplerimiz & Kurallarımız

### Core Principles

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│   KISS  —  Keep It Simple, Stupid                          │
│   YAGNI —  You Ain't Gonna Need It                         │
│   DRY   —  Don't Repeat Yourself                           │
│   SOLID —  Single Responsibility, Open/Closed, Liskov,     │
│            Interface Segregation, Dependency Inversion     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### REST API Design Rules

#### ✅ Doğru

```bash
# Filtering → Query params
GET /api/v1/comments?decisionId={id}
GET /api/v1/comments?userId={id}
GET /api/v1/decisions?tagId={id}&regretLevel=HIGH

# Nested resources → Parent-child relationship
GET /api/v1/decisions/{decisionId}/comments
GET /api/v1/users/{userId}/decisions
```

#### ❌ Yanlış

```bash
# Endpoint duplication
GET /api/v1/comments/decisions/{decisionId}
GET /api/v1/comments/users/{userId}
```

### Katman Kuralları

```
Controller → Service → Repository
    ↓          ↓          ↓
  HTTP    Business    Data Access
  Logic   Logic       Only
```

| Kural | Açıklama |
|-------|----------|
| **Controller** | Sadece HTTP request/response, Service çağırır |
| **Service** | Business logic, transaction boundary, diğer Service'leri çağırabilir |
| **Repository** | Sadece kendi entity'si için data access, başka Repository çağırılmaz |

### Code Quality Rules

| Kural | Açıklama |
|-------|----------|
| Method Boyutu | Max 20 satır |
| Class Boyutu | Max 300 satır |
| Cyclomatic Complexity | Max 10 |
| Null Return | Yasak → Optional veya Exception |
| Magic Values | Yasak → Constant kullan |
| DTO Reuse | Yasak → Her endpoint için ayrı DTO |

---

## 🔌 Services & Endpoints

### 1. Auth Service

**Base Path**: `/api/v1/auth`

| Endpoint | Method | Açıklama |
|----------|--------|----------|
| `/register` | POST | Yeni kullanıcı kaydı |

**Service**: `AuthService`

---

### 2. Decision Service

**Base Path**: `/api/v1/decisions`

| Endpoint | Method | Açıklama | Service Method |
|----------|--------|----------|----------------|
| `/` | GET | Tüm kararlar (query: `userId`, `regretLevel`, `tagId`) | `getAllDecisions()` |
| `/{id}` | GET | Karar detayı | `getDecisionById(id)` |
| `/{decisionId}/comments` | GET | Kararın yorumları | `decisionCommentService.getCommentsByDecisionId()` |
| `/{decisionId}/tags` | GET | Kararın etiketleri | `decisionTagService.getTagsByDecisionId()` |
| `/` | POST | Yeni karar oluştur | `createDecision(request)` |
| `/{id}` | PUT | Karar güncelle | `updateDecision(id, request)` |
| `/{id}` | DELETE | Karar sil | `deleteDecision(id)` |

**Services**: `DecisionService`, `DecisionCommentService`, `DecisionTagService`

---

### 3. Comment Service

**Base Path**: `/api/v1/comments`

| Endpoint | Method | Açıklama | Service Method |
|----------|--------|----------|----------------|
| `/` | GET | Tüm yorumlar (query: `decisionId`, `userId`) | `getAllComments()` |
| `/{id}` | GET | Yorum detayı | `getCommentById(id)` |
| `/` | POST | Yeni yorum oluştur | `createComment(request)` |
| `/{id}` | PUT | Yorum güncelle | `updateComment(id, request)` |
| `/{id}` | DELETE | Yorum sil | `deleteComment(id)` |

**Service**: `CommentService`

---

### 4. Vote Service

**Base Path**: `/api/v1/votes`

| Endpoint | Method | Açıklama | Service Method |
|----------|--------|----------|----------------|
| `/` | GET | Tüm oylar (query: `decisionId`, `userId`) | `getAllVotes()` |
| `/{id}` | GET | Oy detayı | `getVoteById(id)` |
| `/decisions/{decisionId}/count` | GET | Oy sayısı + user status | `getVoteCountByDecisionId()` |
| `/` | POST | Yeni oy ver | `createVote(request)` |
| `/{id}` | DELETE | Oy sil (ID ile) | `deleteVote(id)` |
| `/users/{userId}/decisions/{decisionId}` | DELETE | Oy geri al | `deleteVoteByUserAndDecision()` |

**Service**: `VoteService`

---

### 5. Tag Service

**Base Path**: `/api/v1/tags`

| Endpoint | Method | Açıklama | Service Method |
|----------|--------|----------|----------------|
| `/` | GET | Tüm etiketler | `getAllTags()` |
| `/{id}` | GET | Etiket detayı | `getTagById(id)` |
| `/name/{name}` | GET | Etiket ile ara | `getTagByName(name)` |
| `/` | POST | Yeni etiket oluştur | `createTag(request)` |
| `/{id}` | PUT | Etiket güncelle | `updateTag(id, request)` |
| `/{id}` | DELETE | Etiket sil | `deleteTag(id)` |

**Service**: `TagService`

---

### 6. Regular User Service

**Base Path**: `/api/v1/users`

| Endpoint | Method | Açıklama | Service Method |
|----------|--------|----------|----------------|
| `/` | GET | Tüm kullanıcılar | `getAll()` |
| `/{id}` | GET | Kullanıcı detayı | `getUserById(id)` |
| `/{userId}/comments` | GET | Kullanıcının yorumları | `userCommentService.getCommentsByUserId()` |
| `/{id}` | PUT | Kullanıcı güncelle | `update(id, request)` |
| `/{id}` | DELETE | Kullanıcı sil | `delete(id)` |

**Service**: `RegularUserService`, `UserCommentService`

---

### 7. Company User Service

**Base Path**: `/api/v1/companies`

| Endpoint | Method | Açıklama | Service Method |
|----------|--------|----------|----------------|
| `/` | GET | Tüm şirketler | `getAll()` |
| `/{id}` | GET | Şirket detayı | `getCompanyById(id)` |
| `/{id}` | PUT | Şirket güncelle | `update(id, request)` |
| `/{id}` | DELETE | Şirket sil | `delete(id)` |

**Service**: `CompanyUserService`

---

## 🗄️ Entities & İlişkiler

### Entity Relationship Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│  ┌─────────────┐         ┌─────────────┐                        │
│  │RegularUser  │         │CompanyUser  │                        │
│  │(extends     │         │(extends     │                        │
│  │ User)       │         │ User)       │                        │
│  ├─────────────┤         ├─────────────┤                        │
│  │ id: UUID    │         │ id: UUID    │                        │
│  │ email       │         │ email       │                        │
│  │ password    │         │ password    │                        │
│  │ username    │         │ companyName │                        │
│  │ role: USER  │         │ role:COMPANY│                        │
│  └──────┬──────┘         └─────────────┘                        │
│         │                                                        │
│         │ 1:N                                                    │
│         │                                                        │
│         ▼                                                        │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                      Decision                            │    │
│  ├─────────────────────────────────────────────────────────┤    │
│  │ id: UUID                                                 │    │
│  │ title: String                                            │    │
│  │ why: String                                              │    │
│  │ alternative: String                                      │    │
│  │ regretLevel: Enum (LOW, MEDIUM, HIGH)                   │    │
│  │ voteCount: int                                           │    │
│  │ user_id: UUID (FK → RegularUser)                        │    │
│  └──────────────┬──────────────────────────────────────────┘    │
│                 │                                                 │
│         ┌───────┼───────┬────────────┐                           │
│         │       │       │            │                           │
│         │ 1:N   │ 1:N   │ 1:N        │                           │
│         ▼       │       ▼            ▼                           │
│  ┌─────────┐  │  ┌──────────┐  ┌──────────┐                     │
│  │ Comment │  │  │   Vote   │  │DecisionTag│                    │
│  ├─────────┤  │  ├──────────┤  ├──────────┤                     │
│  │ id      │  │  │ id       │  │decision_ │                     │
│  │ content │  │  │user_id   │  │tag_id    │                     │
│  │user_id  │  │  │decision_ │  │(PK, FK)  │                     │
│  │decision_│  │  │id        │  │tag_id    │                     │
│  │id       │  │  │          │  │(PK, FK)  │                     │
│  └─────────┘  │  └──────────┘  └────┬─────┘                     │
│               │                     │ 1:N                        │
│               │                     │                            │
│               │                     ▼                            │
│               │              ┌──────────┐                        │
│               │              │   Tag    │                        │
│               │              ├──────────┤                        │
│               │              │ id: UUID │                        │
│               │              │ name     │                        │
│               │              └──────────┘                        │
│               │                                                  │
└───────────────┴──────────────────────────────────────────────────┘
```

### Entity Detayları

#### 1. User (Abstract Base Entity)

| Field | Type | Açıklama |
|-------|------|----------|
| `id` | UUID | Primary key |
| `email` | String | Unique, nullable=false |
| `password` | String | BCrypt hashed |
| `role` | Enum | USER, COMPANY, ADMIN |
| `createdAt` | LocalDateTime | Oluşturulma zamanı |
| `updatedAt` | LocalDateTime | Güncellenme zamanı |

**Inheritance**: `RegularUser`, `CompanyUser`

---

#### 2. RegularUser

| Field | Type | Açıklama |
|-------|------|----------|
| `id` | UUID | Inherited from User |
| `email` | String | Inherited from User |
| `password` | String | Inherited from User |
| `username` | String | Unique username |
| `role` | Enum | ROLE_USER |

**Relationships**:
- 1:N → Decision
- 1:N → Comment
- 1:N → Vote

---

#### 3. CompanyUser

| Field | Type | Açıklama |
|-------|------|----------|
| `id` | UUID | Inherited from User |
| `email` | String | Inherited from User |
| `password` | String | Inherited from User |
| `companyName` | String | Company name |
| `role` | Enum | ROLE_COMPANY |

---

#### 4. Decision

| Field | Type | Açıklama |
|-------|------|----------|
| `id` | UUID | Primary key |
| `title` | String | Decision title |
| `why` | String | Reasoning |
| `alternative` | String | Alternative options |
| `regretLevel` | Enum | LOW, MEDIUM, HIGH |
| `voteCount` | int | Total vote count |
| `user_id` | UUID | Foreign key → RegularUser |

**Relationships**:
- N:1 → RegularUser
- 1:N → Comment (cascade: REMOVE, PERSIST)
- 1:N → Vote (cascade: REMOVE, PERSIST)
- 1:N → DecisionTag (cascade: ALL)

---

#### 5. Comment

| Field | Type | Açıklama |
|-------|------|----------|
| `id` | UUID | Primary key |
| `content` | String | Comment content |
| `user_id` | UUID | Foreign key → RegularUser |
| `decision_id` | UUID | Foreign key → Decision |

**Relationships**:
- N:1 → RegularUser
- N:1 → Decision

---

#### 6. Vote

| Field | Type | Açıklama |
|-------|------|----------|
| `id` | UUID | Primary key |
| `user_id` | UUID | Foreign key → RegularUser |
| `decision_id` | UUID | Foreign key → Decision |

**Constraints**:
- Unique: `(user_id, decision_id)` — Bir kullanıcı bir karara sadece bir kez oy verebilir

**Relationships**:
- N:1 → RegularUser
- N:1 → Decision

---

#### 7. Tag

| Field | Type | Açıklama |
|-------|------|----------|
| `id` | UUID | Primary key |
| `name` | String | Unique tag name |
| `createdAt` | LocalDateTime | Oluşturulma zamanı |
| `updatedAt` | LocalDateTime | Güncellenme zamanı |

**Relationships**:
- 1:N → DecisionTag

---

#### 8. DecisionTag (Junction Table)

| Field | Type | Açıklama |
|-------|------|----------|
| `decision_id` | UUID | Primary key, FK → Decision |
| `tag_id` | UUID | Primary key, FK → Tag |

**Relationships**:
- N:1 → Decision
- N:1 → Tag

**Purpose**: Decision ve Tag arasında çok-çok ilişki kurar

---

## 📊 Database Schema (SQL)

```sql
-- Users table (inheritance via JOINED strategy)
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE regular_users (
    id UUID PRIMARY KEY REFERENCES users(id),
    username VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE company_users (
    id UUID PRIMARY KEY REFERENCES users(id),
    company_name VARCHAR(255) NOT NULL
);

-- Decisions
CREATE TABLE decisions (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    why TEXT NOT NULL,
    alternative TEXT,
    regret_level VARCHAR(50) NOT NULL,
    vote_count INTEGER DEFAULT 0,
    user_id UUID REFERENCES regular_users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Comments
CREATE TABLE comments (
    id UUID PRIMARY KEY,
    content TEXT NOT NULL,
    user_id UUID REFERENCES regular_users(id),
    decision_id UUID REFERENCES decisions(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Votes
CREATE TABLE votes (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES regular_users(id),
    decision_id UUID REFERENCES decisions(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, decision_id)
);

-- Tags
CREATE TABLE tags (
    id UUID PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Decision-Tag Junction
CREATE TABLE decision_tag (
    decision_id UUID REFERENCES decisions(id) ON DELETE CASCADE,
    tag_id UUID REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (decision_id, tag_id)
);
```

---

## 🚀 Quick Start

### Development Setup

```bash
# 1. Clone repository
git clone https://github.com/karar-dev/karar.dev.git
cd karar.dev

# 2. Set environment variables
export JWT_SECRET=your-secret-key

# 3. Run with local profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 4. Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

### Docker Setup

```bash
# Build and run
docker-compose up -d

# Access application
curl http://localhost:8080/api/v1/decisions
```

---

## 📚 Ek Dokümantasyon

| Doküman | Açıklama |
|---------|----------|
| [01-overview.md](01-overview.md) | Proje genel bakış |
| [02-architecture.md](02-architecture.md) | Mimari detaylar |
| [03-technology-stack.md](03-technology-stack.md) | Teknoloji stack |
| [04-database-schema.md](04-database-schema.md) | Veritabanı şeması |
| [05-baseresponse.md](05-baseresponse.md) | BaseResponse format |
| [06-exception-handling.md](06-exception-handling.md) | Exception handling |
| [07-api-endpoints.md](07-api-endpoints.md) | API endpoints |
| [08-dtos.md](08-dtos.md) | DTO yapıları |
| [09-development.md](09-development.md) | Geliştirme rehberi |
| [10-changelog.md](10-changelog.md) | Değişiklik logu |

---

## 📞 İletişim

- **Website**: https://karar.dev
- **Email**: info@karar.dev
- **GitHub**: https://github.com/karar-dev/karar.dev

---

**© 2026 Karar.dev Team. Tüm hakları saklıdır.**
