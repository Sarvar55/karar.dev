# 🗺️ Karar.dev — Teknoloji Yol Haritası (Roadmap)

> **Son Güncelleme**: 2026-04-28  
> **Versiyon**: 1.0.0

---

## 📊 Mevcut Durum

| Alan | Durum | Açıklama |
|------|-------|----------|
| **Core CRUD** | ✅ Tamamlandı | Decision, Comment, Vote, Tag, User |
| **Security (Local)** | ✅ Tamamlandı | Spring Security + in-memory users + ownership checks |
| **Method Security** | ✅ Tamamlandı | `@PreAuthorize` ile ownership/admin kontrolleri |
| **Exception Handling** | ✅ Tamamlandı | Global exception handler + BaseResponse |
| **Swagger/OpenAPI** | ✅ Tamamlandı | SpringDoc OpenAPI 3.0 |
| **Frontend** | 🟡 Devam Ediyor | Next.js UI (`karar-ui/`) |
| **JWT Authentication** | ❌ Eksik | Şu an session-based + `{noop}` passwords |
| **Database Migrations** | ❌ Eksik | `ddl-auto: update` kullanılıyor |
| **Production DB** | ❌ Eksik | Sadece H2 in-memory |
| **Unit Tests** | ❌ Eksik | Test yok |
| **CI/CD** | ❌ Eksik | Pipeline yok |
| **Docker** | ❌ Eksik | Containerization yok |
| **CORS** | ❌ Eksik | Frontend bağlanamıyor |

---

## 🔴 Phase 1 — Temel Gereksinimler (Must-Have)

> Bu adımlar olmadan uygulama production'a çıkamaz.

---

### 1.1 CORS Yapılandırması

**Neden?** Next.js frontend (`:3000`) backend'e (`:8080`) istek yapamıyor.

**Ne Yapılacak:**
- `WebMvcConfigurer` bean ile CORS mapping
- Profile-aware: `local/dev` → permissive, `prod` → restrictive
- Allowed origins, methods, headers tanımlama

**Kullanılacak:**
- `org.springframework.web.servlet.config.annotation.WebMvcConfigurer`

**Tahmini Süre:** 30 dakika

**Örnek:**

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

---

### 1.2 JWT Authentication

**Neden?** Şu an `formLogin()` + `httpBasic()` + `{noop}` passwords kullanılıyor. REST API'ler için JWT standart.

**Ne Yapılacak:**

| Bileşen | Açıklama |
|---------|----------|
| `JwtTokenProvider` | Token üretme (access + refresh) ve doğrulama |
| `JwtAuthenticationFilter` | `Authorization: Bearer ...` header'dan token çıkarma |
| `POST /api/v1/auth/login` | Kullanıcı girişi → access + refresh token döndürme |
| `POST /api/v1/auth/refresh` | Refresh token ile yeni access token alma |
| `ProjectSecurityConfig` | JWT filter chain (non-local profil) |
| `UserDetailsService` | DB-backed user loading (`UserRepository`) |

**Dependency:**

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

**Tahmini Süre:** 1-2 gün

**Akış:**

```
┌──────────┐    POST /auth/login     ┌──────────┐
│  Client  │ ──────────────────────→ │  Server  │
│          │ ←────────────────────── │          │
│          │  { accessToken,         │          │
│          │    refreshToken }       │          │
│          │                         │          │
│          │  GET /api/v1/decisions  │          │
│          │  Authorization: Bearer  │          │
│          │  <accessToken>          │          │
│          │ ──────────────────────→ │          │
│          │ ←────────────────────── │          │
│          │  { success: true, ... } │          │
└──────────┘                         └──────────┘
```

---

### 1.3 Password Encoding

**Neden?** `{noop}` plain-text passwords güvenlik açığı.

**Ne Yapılacak:**
- `BCryptPasswordEncoder` bean tanımlama
- Registration sırasında password encoding
- Local profilde test user'lar için encoded passwords

**Tahmini Süre:** 1 saat

---

### 1.4 Flyway — Database Migrations

**Neden?** `ddl-auto: update` production'da tehlikeli. Veri kaybı riski var. Rollback imkansız.

**Ne Yapılacak:**
- Flyway dependency ekleme
- Mevcut schema'dan initial migration (`V1__init.sql`) oluşturma
- Her schema değişikliği için yeni migration dosyası
- `ddl-auto: validate` (prod) olarak değiştirme

**Dependency:**

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

**Dosya Yapısı:**

```
src/main/resources/
└── db/migration/
    ├── V1__create_users_table.sql
    ├── V2__create_decisions_table.sql
    ├── V3__create_comments_table.sql
    ├── V4__create_votes_table.sql
    ├── V5__create_tags_table.sql
    └── V6__create_decision_tags_table.sql
```

**Tahmini Süre:** 2-3 saat

---

### 1.5 PostgreSQL

**Neden?** H2 sadece development için. Production'da ACID compliant, performanslı bir DB gerekli.

**Ne Yapılacak:**
- `application-prod.yml` PostgreSQL yapılandırması
- Environment variable'lar ile secret management
- Connection pool (HikariCP) ayarları

**Yapılandırma:**

```yaml
# application-prod.yml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USER}
    password: ${DATABASE_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

**Dependency:**

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Tahmini Süre:** 1 saat

---

### 1.6 Docker + Docker Compose

**Neden?** Tek komutla tüm uygulama ayağa kalkmalı. Herkes için aynı ortam.

**Ne Yapılacak:**

| Dosya | İçerik |
|-------|--------|
| `Dockerfile` | Multi-stage build (build + runtime) |
| `docker-compose.yml` | PostgreSQL + Spring Boot app |
| `docker-compose.dev.yml` | PostgreSQL + pgAdmin (dev ortamı) |
| `.dockerignore` | Gereksiz dosyaları hariç tutma |

**Tahmini Süre:** 2-3 saat

**Örnek docker-compose:**

```yaml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: karar_dev
      POSTGRES_USER: karar
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  app:
    build: .
    depends_on:
      - db
    environment:
      DATABASE_URL: jdbc:postgresql://db:5432/karar_dev
      DATABASE_USER: karar
      DATABASE_PASSWORD: ${DB_PASSWORD}
      PROFILE: prod
    ports:
      - "8080:8080"

volumes:
  pgdata:
```

---

### 1.7 Unit & Integration Tests

**Neden?** Şu an sıfır test var. Refactoring güvenli değil. Regression tespiti imkansız.

**Ne Yapılacak:**

| Katman | Ne Test Edilir | Framework |
|--------|---------------|-----------|
| **Service** | `DecisionService`, `VoteService` iş mantığı | JUnit 5 + Mockito |
| **Controller** | HTTP status codes, validation, request/response | `@WebMvcTest` + MockMvc |
| **Repository** | Custom query'ler (`findByTagId`, `findByUserId`) | `@DataJpaTest` |
| **Security** | Public vs secured endpoints, ownership | `@WithMockUser` + SecurityMockMvc |
| **Integration** | Full akış: register → karar oluştur → oyla | `@SpringBootTest` |

**Hedef Coverage:** %70+

**Tahmini Süre:** 3-5 gün

---

## 🟡 Phase 2 — Güçlü Özellikler (Should-Have)

> Resume/portfolio'da fark yaratacak özellikler.

---

### 2.1 Redis Caching

**Neden?** Popüler kararlar, oy sayıları, tag listeleri cache'lenmeli. Gereksiz DB sorguları azalır.

**Ne Yapılacak:**
- Spring Data Redis dependency
- `@Cacheable`, `@CacheEvict` annotation'ları
- Cache strategy: decisions (TTL: 5 dk), tags (TTL: 1 saat), vote counts (TTL: 30 sn)
- Redis Docker container

**Dependency:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**Örnek Kullanım:**

```java
@Cacheable(value = "decisions", key = "#id")
public DecisionResponse getDecisionById(UUID id) { ... }

@CacheEvict(value = "decisions", key = "#id")
public void updateDecision(UUID id, DecisionUpdateRequest req) { ... }
```

**Tahmini Süre:** 1 gün

---

### 2.2 MapStruct — DTO Mapping

**Neden?** Her service'de manual `mapToResponse()` metodları var. Tekrar eden kod, hata riski.

**Ne Yapılacak:**
- MapStruct dependency ve annotation processor
- Her domain için `*Mapper` interface'leri
- `DecisionMapper`, `CommentMapper`, `VoteMapper`, `TagMapper`, `UserMapper`

**Dependency:**

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.3</version>
</dependency>
```

**Örnek:**

```java
@Mapper(componentModel = "spring")
public interface DecisionMapper {
    DecisionResponse toResponse(Decision decision);
    Decision toEntity(DecisionRequest request);
}
```

**Tahmini Süre:** 3-4 saat

---

### 2.3 Elasticsearch — Full-Text Search

**Neden?** Kararları başlık, içerik, tag'e göre arama. Sosyal platformlar için kritik.

**Ne Yapılacak:**
- Elasticsearch Docker container
- Spring Data Elasticsearch dependency
- `DecisionSearchRepository` (Elasticsearch)
- `GET /api/v1/search?q=...` endpoint'i
- Sync mekanizması (DB → Elasticsearch)

**Endpoint:**

```
GET /api/v1/search?q=career+change&tags=life,work&regretLevel=HIGH
```

**Tahmini Süre:** 2-3 gün

---

### 2.4 WebSocket (STOMP) — Real-Time Updates

**Neden?** Anlık oy sayısı güncellemesi, yeni yorum bildirimleri. Uygulamayı canlı hissettirir.

**Ne Yapılacak:**
- Spring WebSocket + STOMP dependency
- `/ws` endpoint'i
- Topic'ler: `/topic/decisions/{id}/votes`, `/topic/decisions/{id}/comments`
- Frontend entegrasyonu (SockJS + STOMP client)

**Akış:**

```
User A oy verir → Server → WebSocket broadcast
                               ↓
                    User B'nin ekranı anlık güncellenir
```

**Tahmini Süre:** 1-2 gün

---

### 2.5 Rate Limiting (Bucket4j)

**Neden?** Oy ve yorum spam'ini önleme. API kötüye kullanımını engelleme.

**Ne Yapılacak:**
- Bucket4j dependency
- Rate limit interceptor/filter
- Endpoint bazlı limitler
- `429 Too Many Requests` response

**Limitler:**

| Endpoint | Limit |
|----------|-------|
| `POST /votes` | 10 istek / dakika / kullanıcı |
| `POST /comments` | 5 istek / dakika / kullanıcı |
| `POST /decisions` | 3 istek / dakika / kullanıcı |
| Genel API | 100 istek / dakika / IP |

**Tahmini Süre:** 3-4 saat

---

## 🟢 Phase 3 — İleri Seviye Özellikler (Nice-to-Have)

> Dağıtık sistemler, monitoring, CI/CD — büyük projelerde aranan yetkinlikler.

---

### 3.1 Apache Kafka — Event-Driven Architecture

**Neden?** Asenkron işlemler: bildirim gönderme, analytics güncelleme, badge sistemi.

**Ne Yapılacak:**
- Kafka + Zookeeper Docker containers
- Spring Kafka dependency
- Event'ler: `DecisionCreatedEvent`, `VoteEvent`, `CommentEvent`
- Producer: Service katmanında event publish
- Consumer: Notification service, analytics service

**Event Akışı:**

```
Decision 100+ oy aldı
    → KafkaProducer.send("decision-events", TrendingDecisionEvent)
        → NotificationConsumer → Kullanıcıya bildirim gönder
        → AnalyticsConsumer → Dashboard'u güncelle
        → BadgeConsumer → "Trending" badge ver
```

**Dependency:**

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

**Tahmini Süre:** 2-3 gün

---

### 3.2 CI/CD — GitHub Actions

**Neden?** Her push'da otomatik build, test, deploy.

**Ne Yapılacak:**

| Pipeline Adımı | Araç |
|----------------|------|
| Build | Maven |
| Test | JUnit + Testcontainers |
| Code Quality | SonarQube / Checkstyle |
| Docker Build | Docker Buildx |
| Push Image | Docker Hub / GitHub Container Registry |
| Deploy | Railway / Render / AWS ECS |

**Dosya:** `.github/workflows/ci.yml`

**Tahmini Süre:** 1 gün

---

### 3.3 Notification System

**Neden?** Kullanıcıları aktif tutma. "Kararınıza yorum yapıldı", "Kararınız trend oldu" gibi bildirimler.

**Ne Yapılacak:**
- `Notification` entity
- In-app notification API (`GET /api/v1/notifications`)
- E-posta bildirimleri (Spring Mail + template engine)
- WebSocket ile anlık bildirim push
- Kafka consumer ile event-driven tetikleme

**Tahmini Süre:** 2-3 gün

---

### 3.4 Soft Delete

**Neden?** Hard delete geri alınamaz. Yanlışlıkla silinen veri kaybolur.

**Ne Yapılacak:**
- `BaseEntity`'ye `deletedAt` kolonu ekleme
- `@Where(clause = "deleted_at IS NULL")` ile otomatik filtreleme
- `@SQLDelete(sql = "UPDATE ... SET deleted_at = NOW()")` ile soft delete
- Admin için silinen verileri geri getirme endpoint'i

**Tahmini Süre:** 2-3 saat

---

### 3.5 Audit Logging (Spring Data Envers)

**Neden?** Kim, ne zaman, neyi değiştirdi? Enterprise uygulamalarda zorunlu.

**Ne Yapılacak:**
- Hibernate Envers dependency
- `@Audited` annotation entity'lere
- Audit history endpoint'i (admin-only)

**Tahmini Süre:** 3-4 saat

---

### 3.6 Prometheus + Grafana — Monitoring

**Neden?** Request latency, error rate, JVM metrics. Uygulamanın sağlığını izleme.

**Ne Yapılacak:**
- Spring Boot Actuator + Micrometer
- Prometheus metric endpoint (`/actuator/prometheus`)
- Grafana dashboard (Docker)
- Custom metrics: aktif kullanıcı sayısı, günlük oy sayısı

**Docker Compose:**

```yaml
services:
  prometheus:
    image: prom/prometheus
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
  grafana:
    image: grafana/grafana
    ports:
      - "3001:3000"
```

**Tahmini Süre:** 1 gün

---

### 3.7 MinIO / S3 — File Storage

**Neden?** Kullanıcı avatarları, karar görselleri. Dosya depolama.

**Ne Yapılacak:**
- MinIO Docker container (S3-compatible)
- Spring Cloud AWS / MinIO client
- `POST /api/v1/files/upload` endpoint
- Image resize/compress (Thumbnailator)

**Tahmini Süre:** 1 gün

---

## 📋 Önerilen Uygulama Sırası

```
Phase 1 (Temel — 1-2 hafta):
  ├─ 1.1 CORS Config              (30 dk)
  ├─ 1.2 JWT Authentication       (1-2 gün)
  ├─ 1.3 Password Encoding        (1 saat)
  ├─ 1.4 Flyway Migrations        (2-3 saat)
  ├─ 1.5 PostgreSQL               (1 saat)
  ├─ 1.6 Docker + Compose         (2-3 saat)
  └─ 1.7 Unit Tests               (3-5 gün)

Phase 2 (Güçlendirme — 2-3 hafta):
  ├─ 2.1 Redis Caching            (1 gün)
  ├─ 2.2 MapStruct                (3-4 saat)
  ├─ 2.3 Elasticsearch            (2-3 gün)
  ├─ 2.4 WebSocket                (1-2 gün)
  └─ 2.5 Rate Limiting            (3-4 saat)

Phase 3 (İleri Seviye — 3-4 hafta):
  ├─ 3.1 Apache Kafka             (2-3 gün)
  ├─ 3.2 CI/CD                    (1 gün)
  ├─ 3.3 Notification System      (2-3 gün)
  ├─ 3.4 Soft Delete              (2-3 saat)
  ├─ 3.5 Audit Logging            (3-4 saat)
  ├─ 3.6 Monitoring               (1 gün)
  └─ 3.7 File Storage             (1 gün)
```

---

## 🏗️ Hedef Mimari (Tüm Phase'ler Sonrası)

```
                    ┌─────────────────┐
                    │   Next.js UI    │
                    │  (karar-ui)     │
                    └────────┬────────┘
                             │ HTTP + WebSocket
                             ▼
                    ┌─────────────────┐
                    │  Spring Boot    │
                    │  REST API       │
                    │  + JWT Auth     │
                    │  + Rate Limiter │
                    └───┬───┬───┬────┘
                        │   │   │
            ┌───────────┘   │   └───────────┐
            ▼               ▼               ▼
    ┌──────────────┐ ┌──────────┐ ┌──────────────┐
    │ PostgreSQL   │ │  Redis   │ │Elasticsearch │
    │ (Primary DB) │ │ (Cache)  │ │  (Search)    │
    └──────────────┘ └──────────┘ └──────────────┘
                        │
                        ▼
                ┌──────────────┐
                │ Apache Kafka │
                └───┬──────┬───┘
                    │      │
                    ▼      ▼
            ┌──────────┐ ┌──────────────┐
            │Notif Svc │ │Analytics Svc │
            └──────────┘ └──────────────┘
                             │
                             ▼
                    ┌──────────────┐
                    │  Prometheus  │
                    │  + Grafana   │
                    └──────────────┘
```

---

## ✅ İlerleme Takibi

```markdown
## Phase 1
- [x] CORS Config
- [ ] JWT Authentication
- [ ] Password Encoding
- [ ] Flyway Migrations
- [ ] PostgreSQL
- [ ] Docker + Compose
- [ ] Unit Tests

## Phase 2
- [ ] Redis Caching
- [ ] MapStruct
- [ ] Elasticsearch
- [ ] WebSocket
- [ ] Rate Limiting

## Phase 3
- [ ] Apache Kafka
- [ ] CI/CD
- [ ] Notification System
- [ ] Soft Delete
- [ ] Audit Logging
- [ ] Monitoring
- [ ] File Storage
```

---

**Son Güncelleme**: 2026-04-28

© 2026 Karar.dev Team
