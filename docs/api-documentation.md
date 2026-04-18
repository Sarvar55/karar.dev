# Karar.dev API Dokümantasyonu

## 📋 İçindekiler

1. [Proje Hakkında](#proje-hakkında)
2. [Mimari Yapı](#mimari-yapı)
3. [Teknoloji Stack'i](#teknoloji-stacki)
4. [Veritabanı Şeması](#veritabanı-şeması)
5. [BaseResponse Yapısı](#baseresponse-yapısı)
6. [Exception Handling](#exception-handling)
7. [Modüller](#modüller)
   - [Auth Modülü](#auth-modülü)
   - [User Modülü](#user-modülü)
   - [Decision Modülü](#decision-modülü)
   - [Tag Modülü](#tag-modülü)
   - [Vote Modülü](#vote-modülü)
   - [Comment Modülü](#comment-modülü)
8. [API Endpoints](#api-endpoints)
9. [DTO'lar](#dtolar)
10. [Yapılan Değişiklikler](#yapılan-değişiklikler)
11. [Swagger UI](#swagger-ui)
12. [Geliştirme Ortamı](#geliştirme-ortamı)

---

## 🎯 Proje Hakkında

**Karar.dev** kullanıcıların pişmanlıklarını ve aldıkları kararları paylaşabileceği, oylayabileceği ve yorum yapabileceği bir sosyal platformdur.

### Özellikler

- **Kullanıcı Yönetimi**: Regular (bireysel) ve Company (kurumsal) kullanıcı desteği
- **Karar Paylaşımı**: Kullanıcılar aldıkları kararları, nedenlerini ve alternatiflerini paylaşabilir
- **Pişmanlık Seviyesi**: LOW, MEDIUM, HIGH seviyelerinde pişmanlık bildirimi
- **Oylama Sistemi**: Kararlar üzerinde oy kullanma
- **Etiketleme**: Kararları kategorilere ayırma
- **Yorum Sistemi**: Kararlara yorum yapma

---

## 🏗️ Mimari Yapı

```
org.karar.dev
├── common
│   ├── entity          # Temel entity sınıfları
│   ├── enums           # Enum tanımlamaları
│   └── exception       # Exception handling
│       ├── base        # Temel exception sınıfları
│       ├── conflict    # Conflict (409) exceptions
│       ├── dto         # Error response DTO
│       ├── handler     # Global exception handler
│       ├── notFound    # Not found (404) exceptions
│       └── validation  # Validation exceptions
├── config              # Konfigürasyon sınıfları
├── domain              # Domain modülleri
│   ├── auth            # Kimlik doğrulama
│   ├── base            # BaseResponse ve yardımcı sınıflar
│   ├── comment         # Yorum sistemi
│   ├── decision        # Karar yönetimi
│   ├── decisiontag     # Karar-etiket ilişkisi
│   ├── tag             # Etiket yönetimi
│   ├── user            # Kullanıcı yönetimi
│   │   ├── company     # Şirket kullanıcıları
│   │   └── regular     # Bireysel kullanıcılar
│   └── vote            # Oy sistemi
└── Application.java    # Spring Boot başlangıç sınıfı
```

---

## 🛠️ Teknoloji Stack'i

| Teknoloji | Versiyon | Amaç |
|-----------|----------|------|
| Java | 17 | Programlama dili |
| Spring Boot | 4.0.3 | Uygulama çatısı |
| Spring Data JPA | 4.0.3 | Veritabanı erişimi |
| Spring Validation | 4.0.3 | Input validasyonu |
| H2 Database | - | Geliştirme veritabanı |
| Lombok | - | Boilerplate kod azaltma |
| OpenAPI (Swagger) | 3.0.2 | API dokümantasyonu |
| Maven | - | Build aracı |

### Bağımlılıklar (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    spring-boot-starter-webmvc
    spring-boot-starter-data-jpa
    spring-boot-starter-validation
    spring-boot-devtools
    
    <!-- Database -->
    h2 (runtime)
    
    <!-- Documentation -->
    springdoc-openapi-starter-webmvc-ui (3.0.2)
    
    <!-- Utilities -->
    lombok
    
    <!-- Testing -->
    spring-boot-starter-validation-test
    spring-boot-starter-webmvc-test
</dependencies>
```

---

## 🗄️ Veritabanı Şeması

### Entity İlişki Diyagramı

```
┌─────────────────────┐     ┌─────────────────────┐
│   RegularUser       │     │   CompanyUser       │
├─────────────────────┤     ├─────────────────────┤
│ id (UUID, PK)       │     │ id (UUID, PK)       │
│ email               │     │ email               │
│ password            │     │ password            │
│ username            │     │ companyName         │
│ role                │     │ role                │
│ createdAt           │     │ createdAt           │
│ updatedAt           │     │ updatedAt           │
└──────────┬──────────┘     └─────────────────────┘
           │
           │ 1:N
           ▼
┌─────────────────────┐     ┌─────────────────────┐
│      Decision       │     │       Vote          │
├─────────────────────┤     ├─────────────────────┤
│ id (UUID, PK)       │◄────┤ id (UUID, PK)       │
│ title               │ 1:N │ decision_id (FK)    │
│ why                 │     │ user_id (FK)          │
│ alternative         │     │ voteType              │
│ regretLevel         │     └─────────────────────┘
│ voteCount           │
│ user_id (FK)        │
└──────────┬──────────┘
           │
           │ 1:N
           ▼
┌─────────────────────┐
│      Comment        │
├─────────────────────┤
│ id (UUID, PK)       │
│ content             │
│ decision_id (FK)    │
│ user_id (FK)        │
└─────────────────────┘

┌─────────────────────┐     ┌─────────────────────┐
│    DecisionTag      │     │        Tag          │
│   (Junction Table)  │     ├─────────────────────┤
├─────────────────────┤     │ id (UUID, PK)       │
│ decision_id (PK,FK) │◄────┤ name                │
│ tag_id (PK,FK)      │────►└─────────────────────┘
└─────────────────────┘
```

### Entity Açıklamaları

| Entity | Açıklama |
|--------|----------|
| **User** | Abstract temel kullanıcı sınıfı |
| **RegularUser** | Bireysel kullanıcı (username ile) |
| **CompanyUser** | Kurumsal kullanıcı (companyName ile) |
| **Decision** | Kullanıcıların paylaştığı kararlar |
| **Tag** | Karar kategorileri |
| **DecisionTag** | Decision-Tag çoka çok ilişki tablosu |
| **Vote** | Kararlara verilen oylar |
| **Comment** | Kararlara yapılan yorumlar |

---

## 📦 BaseResponse Yapısı

Tüm API response'ları tutarlı bir yapıda `BaseResponse<T>` sınıfı ile döndürülür.

### BaseResponse<T> Yapısı

```java
{
    "success": true/false,              // İşlem başarılı mı?
    "data": T,                          // Response data (null olabilir)
    "error": {                          // Hata durumunda dolu olur
        "code": "ExceptionClassName",   // Hata kodu
        "message": "Error message",     // Hata mesajı
        "details": { ... },             // Ek detaylar (opsiyonel)
        "validationErrors": {           // Validasyon hataları
            "field1": "error message",
            "field2": "error message"
        }
    },
    "timestamp": "2025-01-20T10:30:00", // ISO 8601 format
    "status": "OK/CREATED/BAD_REQUEST/..." // HTTP status
}
```

### Factory Metodları

| Metod | Açıklama | HTTP Status |
|-------|----------|-------------|
| `BaseResponse.success(T data)` | Başarılı response | 200 OK |
| `BaseResponse.success(T data, HttpStatus status)` | Özel status ile başarılı | Belirtilen status |
| `BaseResponse.error(BaseException ex, HttpStatus status)` | Exception'dan error | Belirtilen status |
| `BaseResponse.error(String code, String message, HttpStatus status)` | Manuel error | Belirtilen status |
| `BaseResponse.validationError(String message, Map<String,String> errors)` | Validasyon error | 400 BAD_REQUEST |

### Örnek Response'lar

**Başarılı (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Should I learn Spring Boot?",
    "regretLevel": "LOW",
    "voteCount": 42
  },
  "timestamp": "2025-01-20T10:30:00",
  "status": "OK"
}
```

**Validasyon Hatası (400 BAD_REQUEST):**
```json
{
  "success": false,
  "error": {
    "code": "ValidationError",
    "message": "Validation failed",
    "validationErrors": {
      "title": "Title is required",
      "email": "Email format is invalid"
    }
  },
  "timestamp": "2025-01-20T10:30:00",
  "status": "BAD_REQUEST"
}
```

**Kaynak Bulunamadı (404 NOT_FOUND):**
```json
{
  "success": false,
  "error": {
    "code": "ResourceNotFoundException",
    "message": "Decision not found with id : 'xxx'",
    "validationErrors": null
  },
  "timestamp": "2025-01-20T10:30:00",
  "status": "NOT_FOUND"
}
```

---

## ⚠️ Exception Handling

### Exception Hiyerarşisi

```
RuntimeException
    └── BaseException (abstract)
        ├── ConflictException (409)
        │   └── EmailAlreadyExistsException
        ├── NotFoundException (404)
        │   └── ResourceNotFoundException
        └── ValidationException (400)
```

### GlobalExceptionHandler

Tüm exception'lar `@RestControllerAdvice` ile merkezi olarak yönetilir:

| Exception | Handler Metodu | Response |
|-----------|----------------|----------|
| `BaseException` | `handleBaseException` | BaseResponse.error(ex, status) |
| `MethodArgumentNotValidException` | `handleValidationException` | BaseResponse.validationError(...) |
| `Exception` (genel) | `handleGenericException` | BaseResponse.error(...) 500 |

---

## 📂 Modüller

### 🔐 Auth Modülü

**Amaç**: Kullanıcı kayıt ve kimlik doğrulama işlemleri.

**Dosyalar**:
- `AuthController.java`
- `AuthService.java`
- `dto/RegisterRequest.java`
- `dto/AuthResponse.java`

**Endpoint**: `POST /api/v1/auth/register`

**Özellikler**:
- Rol bazlı kayıt (USER, COMPANY)
- Email benzersizlik kontrolü
- Dinamik kayıt stratejisi (Strategy Pattern)

### 👤 User Modülü

#### RegularUser (Bireysel Kullanıcı)

**Amaç**: Bireysel kullanıcı yönetimi.

**Dosyalar**:
- `RegularUserController.java`
- `RegularUserService.java`
- `RegularUserRepository.java`
- `dto/RegularUserResponse.java`
- `dto/RegularUserUpdateRequest.java`

**Endpoints**:
- `GET /api/v1/users` - Tüm kullanıcıları listele
- `GET /api/v1/users/{id}` - ID'ye göre getir
- `PUT /api/v1/users/{id}` - Güncelle
- `DELETE /api/v1/users/{id}` - Sil

#### CompanyUser (Kurumsal Kullanıcı)

**Amaç**: Kurumsal kullanıcı yönetimi.

**Dosyalar**:
- `CompanyUserController.java`
- `CompanyUserService.java`
- `CompanyUserRepository.java`
- `dto/CompanyUserResponse.java`
- `dto/CompanyUserUpdateRequest.java`

### 🎯 Decision Modülü

**Amaç**: Karar yönetimi - projenin temel özelliği.

**Dosyalar**:
- `DecisionController.java`
- `DecisionService.java`
- `DecisionRepository.java`
- `Decision.java`
- `dto/DecisionRequest.java`
- `dto/DecisionResponse.java`
- `dto/DecisionUpdateRequest.java`

**Endpoints**:

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/v1/decisions` | Tüm kararları listele |
| GET | `/api/v1/decisions/{id}` | ID'ye göre karar getir |
| GET | `/api/v1/decisions/user/{userId}` | Kullanıcının kararlarını getir |
| GET | `/api/v1/decisions/regret-level/{level}` | Pişmanlık seviyesine göre filtrele |
| POST | `/api/v1/decisions` | Yeni karar oluştur |
| PUT | `/api/v1/decisions/{id}` | Kararı güncelle |
| DELETE | `/api/v1/decisions/{id}` | Kararı sil |

**Decision Entity Alanları**:
- `title`: Karar başlığı
- `why`: Kararın nedeni
- `alternative`: Düşünülen alternatifler
- `regretLevel`: Pişmanlık seviyesi (LOW, MEDIUM, HIGH)
- `voteCount`: Oy sayısı
- `user`: Kararı oluşturan kullanıcı
- `comments`: Karara yapılan yorumlar
- `tags`: Karar etiketleri
- `votes`: Karara verilen oylar

### 🏷️ Tag Modülü

**Amaç**: Kararları kategorize etmek için etiket yönetimi.

**Dosyalar**:
- `Tag.java`
- `DecisionTag.java` (Junction entity)
- `DecisionTagId.java` (Composite key)

### 🗳️ Vote Modülü

**Amaç**: Kararlara oy verme sistemi.

**Dosyalar**:
- `Vote.java`

**Özellikler**:
- Bir kullanıcı bir karara sadece bir oy verebilir (unique constraint)

### 💬 Comment Modülü

**Amaç**: Kararlara yorum yapma.

**Dosyalar**:
- `Comment.java`

---

## 🔌 API Endpoints

### Tam Endpoint Listesi

| Endpoint | Method | Controller | Açıklama |
|----------|--------|------------|----------|
| `/api/v1/auth/register` | POST | AuthController | Kullanıcı kaydı |
| `/api/v1/users` | GET | RegularUserController | Tüm kullanıcıları listele |
| `/api/v1/users/{id}` | GET | RegularUserController | Kullanıcı getir |
| `/api/v1/users/{id}` | PUT | RegularUserController | Kullanıcı güncelle |
| `/api/v1/users/{id}` | DELETE | RegularUserController | Kullanıcı sil |
| `/api/v1/decisions` | GET | DecisionController | Tüm kararları listele |
| `/api/v1/decisions/{id}` | GET | DecisionController | Karar getir |
| `/api/v1/decisions/user/{userId}` | GET | DecisionController | Kullanıcı kararları |
| `/api/v1/decisions/regret-level/{level}` | GET | DecisionController | Seviyeye göre filtrele |
| `/api/v1/decisions` | POST | DecisionController | Karar oluştur |
| `/api/v1/decisions/{id}` | PUT | DecisionController | Karar güncelle |
| `/api/v1/decisions/{id}` | DELETE | DecisionController | Karar sil |

---

## 📋 DTO'lar

### Auth DTO'ları

#### RegisterRequest
```java
record RegisterRequest(
    @NotBlank String email,
    @NotBlank String password,
    @NotNull Role role,
    String username,        // USER rolü için zorunlu
    String companyName      // COMPANY rolü için zorunlu
)
```

#### AuthResponse
```java
record AuthResponse(
    UUID id,
    String email,
    Role role,
    String accessToken,
    String refreshToken
)
```

### Decision DTO'ları

#### DecisionRequest (Create)
```java
record DecisionRequest(
    @NotBlank String title,
    @NotBlank String why,
    String alternative,
    @NotNull RegretLevel regretLevel,
    @NotNull UUID userId,
    Set<UUID> tagIds
)
```

#### DecisionUpdateRequest
```java
record DecisionUpdateRequest(
    @NotBlank String title,
    @NotBlank String why,
    String alternative,
    @NotNull RegretLevel regretLevel,
    Set<UUID> tagIds
)
```

#### DecisionResponse
```java
record DecisionResponse(
    UUID id,
    String title,
    String why,
    String alternative,
    RegretLevel regretLevel,
    int voteCount,
    UUID userId,
    String username,
    int commentCount,
    Set<String> tags,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
)
```

### User DTO'ları

#### RegularUserResponse
```java
record RegularUserResponse(
    UUID id,
    String email,
    String username
)
```

#### RegularUserUpdateRequest
```java
record RegularUserUpdateRequest(
    @NotBlank @Email String email,
    @NotBlank String username
)
```

---

## 📝 Yapılan Değişiklikler

### 1. BaseResponse Yapısı Güncellendi

**Önceki**: Sadece success ve error metodları vardı, data tutmuyordu.

**Sonraki**:
- `success` flag'i eklendi
- `error` objesi detaylandırıldı: code, message, details, validationErrors
- `timestamp` eklendi
- Yeni factory metodları: `success(data, status)`, `error(code, message, status, details)`, `validationError()`

**Dosya**: `src/main/java/org/karar/dev/domain/base/BaseResponse.java`

### 2. GlobalExceptionHandler Tutarlı Hale Getirildi

**Önceki**: Farklı response tipleri (BaseResponse, ErrorResponse) karışık kullanılıyordu.

**Sonraki**: Tüm handler'lar `ResponseEntity<BaseResponse<?>>` döndürüyor.

**Dosya**: `src/main/java/org/karar/dev/common/exception/handler/GlobalExceptionHandler.java`

### 3. Auth Modülü Güncellendi

**Değişiklikler**:
- `AuthService.register()` artık `BaseResponse<AuthResponse>` döndürüyor
- `AuthController` artık `ResponseEntity<BaseResponse<AuthResponse>>` döndürüyor
- Swagger anotasyonları eklendi

**Dosyalar**:
- `src/main/java/org/karar/dev/domain/auth/AuthService.java`
- `src/main/java/org/karar/dev/domain/auth/AuthController.java`

### 4. Decision CRUD Tamamlandı

**Yeni Eklenen Dosyalar**:
- `DecisionController.java` - REST API endpoints
- `DecisionService.java` - Business logic
- `DecisionRepository.java` - Data access
- `dto/DecisionRequest.java` - Create request DTO
- `dto/DecisionResponse.java` - Response DTO
- `dto/DecisionUpdateRequest.java` - Update request DTO

**Özellikler**:
- Tüm CRUD operasyonları
- Pişmanlık seviyesine göre filtreleme
- Kullanıcıya göre filtreleme
- Title benzersizlik kontrolü (kullanıcı bazında)
- BaseResponse ile tutarlı response yapısı
- Swagger dokümantasyonu

### 5. Tag Entity Güncellendi

**Değişiklik**: `@Data` anotasyonu eklendi (getter/setter için).

**Dosya**: `src/main/java/org/karar/dev/domain/tag/Tag.java`

### 6. DecisionRepository Custom Metodları

**Yeni Metodlar**:
- `findByUserId(UUID userId)`
- `findByRegretLevel(RegretLevel regretLevel)`
- `existsByTitleAndUserId(String title, UUID userId)`

---

## 📚 Swagger UI

### Konfigürasyon

**Dosya**: `src/main/java/org/karar/dev/config/OpenApiConfig.java`

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Karar Dev API")
            .version("1.0")
            .description("API documentation for Karar Dev Application")
            .contact(new Contact()
                .name("Karar Dev Team")
                .email("contact@karar.dev"))
            .license(new License()
                .name("Apache 2.0")));
}
```

### Swagger Anotasyonları

Tüm controller'larda şu anotasyonlar kullanılıyor:

- `@Tag(name="...", description="...")` - Controller grubu
- `@Operation(summary="...", description="...")` - Endpoint açıklaması
- `@ApiResponses(...)` - Olası response'lar
- `@Parameter(description="...", required=true, example="...")` - Parametre açıklaması
- `@Schema(description="...", example="...")` - DTO alan açıklaması

### Erişim

Uygulama başlatıldığında:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 🔧 Geliştirme Ortamı

### application.yml Konfigürasyonu

```yaml
spring:
  application:
    name: karar.dev
  datasource:
    url: jdbc:h2:mem:test
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  profiles:
    active: local
  jpa:
    open-in-view: false
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  pattern:
    console: "%green(%d{HH:mm:ss.SSS}) %red([%thread]) %highlight(%-5level) %yellow(%-40.40logger{39}:%L) - %msg%n"

server:
  port: 8080
```

### Profiller

| Profil | Dosya | Amaç |
|--------|-------|------|
| local | `application-local.yml` | Yerel geliştirme |
| dev | `application-dev.yml` | Geliştirme ortamı |
| prod | `application-prod.yml` | Üretim ortamı |

### Çalıştırma

```bash
# Geliştirme (H2)
./mvnw spring-boot:run

# Veya
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./mvnw spring-boot:run

# Belirli profil ile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Log Formatı

```
10:30:15.123 [main] INFO  o.k.d.d.decision.DecisionService:42 - Decision created successfully
```

---

## 📁 Proje Yapısı (Tam)

```
karar.dev/
├── docs/
│   └── api-documentation.md     # Bu dosya
├── src/
│   └── main/
│       ├── java/
│       │   └── org/karar/dev/
│       │       ├── Application.java
│       │       ├── common/
│       │       │   ├── entity/
│       │       │   │   └── BaseEntity.java
│       │       │   ├── enums/
│       │       │   │   ├── RegretLevel.java
│       │       │   │   └── Role.java
│       │       │   └── exception/
│       │       │       ├── base/
│       │       │       │   ├── BaseException.java
│       │       │       │   └── ValidationException.java
│       │       │       ├── conflict/
│       │       │       │   ├── ConflictException.java
│       │       │       │   └── EmailAlreadyExistsException.java
│       │       │       ├── dto/
│       │       │       │   └── ErrorResponse.java
│       │       │       ├── handler/
│       │       │       │   └── GlobalExceptionHandler.java
│       │       │       └── notFound/
│       │       │           ├── NotFoundException.java
│       │       │           └── ResourceNotFoundException.java
│       │       ├── config/
│       │       │   └── OpenApiConfig.java
│       │       └── domain/
│       │           ├── auth/
│       │           │   ├── AuthController.java
│       │           │   ├── AuthService.java
│       │           │   └── dto/
│       │           │       ├── AuthResponse.java
│       │           │       └── RegisterRequest.java
│       │           ├── base/
│       │           │   ├── BaseResponse.java
│       │           │   └── DecisionTagId.java
│       │           ├── comment/
│       │           │   └── Comment.java
│       │           ├── decision/
│       │           │   ├── Decision.java
│       │           │   ├── DecisionController.java
│       │           │   ├── DecisionRepository.java
│       │           │   ├── DecisionService.java
│       │           │   └── dto/
│       │           │       ├── DecisionRequest.java
│       │           │       ├── DecisionResponse.java
│       │           │       └── DecisionUpdateRequest.java
│       │           ├── decisiontag/
│       │           │   └── DecisionTag.java
│       │           ├── tag/
│       │           │   └── Tag.java
│       │           ├── user/
│       │           │   ├── User.java
│       │           │   ├── UserRepository.java
│       │           │   ├── dto/
│       │           │   │   └── UserResponse.java
│       │           │   ├── company/
│       │           │   │   ├── CompanyUser.java
│       │           │   │   ├── CompanyUserController.java
│       │           │   │   ├── CompanyUserRepository.java
│       │           │   │   ├── CompanyUserService.java
│       │           │   │   └── dto/
│       │           │   │       ├── CompanyUserResponse.java
│       │           │   │       └── CompanyUserUpdateRequest.java
│       │           │   └── regular/
│       │           │       ├── RegularUser.java
│       │           │       ├── RegularUserController.java
│       │           │       ├── RegularUserRepository.java
│       │           │       ├── RegularUserService.java
│       │           │       └── dto/
│       │           │           ├── RegularUserResponse.java
│       │           │           └── RegularUserUpdateRequest.java
│       │           └── vote/
│       │               └── Vote.java
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           ├── application-local.yml
│           └── application-prod.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

## 🚀 Özet

Bu proje, kullanıcıların pişmanlıklarını ve aldıkları kararları paylaşabileceği modern bir REST API'dır. **Spring Boot 4.0.3**, **Java 17** ve **H2 Database** kullanılarak geliştirilmiştir.

### Temel Özellikler:
- ✅ Tutarlı response yapısı (BaseResponse)
- ✅ Merkezi exception handling
- ✅ Swagger/OpenAPI dokümantasyonu
- ✅ Validation
- ✅ CRUD operasyonları
- ✅ Filtreleme ve arama

### Gelecek Geliştirmeler İçin Öneriler:
- JWT tabanlı authentication
- Pagination ve sorting
- File upload (görseller için)
- Advanced search
- Notification sistemi
- Soft delete implementasyonu
- Audit logging
- Rate limiting

---

**Son Güncelleme**: 2025-01-20

**Versiyon**: 0.0.1-SNAPSHOT
