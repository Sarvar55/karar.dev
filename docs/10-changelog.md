# Yapılan Değişiklikler

## 📝 Değişiklik Geçmişi

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

### 7. Comment Modülü Tamamlandı

**Yeni Eklenen Dosyalar**:
- `CommentController.java` - REST API endpoints
- `CommentService.java` - Business logic
- `CommentRepository.java` - Data access layer
- `dto/CommentRequest.java` - Create request DTO
- `dto/CommentResponse.java` - Response DTO
- `dto/CommentUpdateRequest.java` - Update request DTO

**Özellikler**:
- Tam CRUD operasyonları (Create, Read, Update, Delete)
- Karara göre yorum filtreleme (`/comments/decision/{decisionId}`)
- Kullanıcıya göre yorum filtreleme (`/comments/user/{userId}`)
- BaseResponse ile tutarlı response yapısı
- Swagger/OpenAPI dokümantasyonu
- Validation desteği
- Transactional işlemler

**API Endpoints**:
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/v1/comments` | Tüm yorumları listele |
| GET | `/api/v1/comments/{id}` | ID'ye göre yorum getir |
| GET | `/api/v1/comments/decision/{decisionId}` | Belirli bir karara yapılan yorumları listele |
| GET | `/api/v1/comments/user/{userId}` | Belirli bir kullanıcının yorumlarını listele |
| POST | `/api/v1/comments` | Yeni yorum oluştur |
| PUT | `/api/v1/comments/{id}` | Mevcut yorumu güncelle |
| DELETE | `/api/v1/comments/{id}` | Yorum sil |

### 8. Vote Modülü Tamamlandı

**Yeni Eklenen Dosyalar**:
- `VoteController.java` - REST API endpoints
- `VoteService.java` - Business logic
- `VoteRepository.java` - Data access layer
- `dto/VoteRequest.java` - Create request DTO
- `dto/VoteResponse.java` - Response DTO
- `dto/VoteCountResponse.java` - Vote statistics DTO

**Özellikler**:
- ✅ Tam CRUD operasyonları (Create, Read, Delete)
- ✅ Karara göre oy filtreleme (`/votes/decision/{decisionId}`)
- ✅ Kullanıcıya göre oy filtreleme (`/votes/user/{userId}`)
- ✅ Oy sayısı ve kullanıcı durumu endpoint'i (`/votes/decision/{decisionId}/count`)
- ✅ Kullanıcının oy durumu kontrolü (`/votes/check?userId=X&decisionId=Y`)
- ✅ "Unvote" fonksiyonu (oy geri çekme) - DELETE `/api/v1/votes?userId=X&decisionId=Y`
- ✅ Duplicate vote kontrolü - 409 Conflict hatası döner
- ✅ Otomatik `voteCount` güncelleme (Decision entity)
- ✅ Unique constraint: Bir kullanıcı bir karara sadece bir kez oy verebilir
- ✅ BaseResponse ile tutarlı response yapısı
- ✅ Swagger/OpenAPI dokümantasyonu
- ✅ Validation desteği
- ✅ Transactional işlemler

**API Endpoints**:
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/v1/votes` | Tüm oyları listele |
| GET | `/api/v1/votes/{id}` | ID'ye göre oy getir |
| GET | `/api/v1/votes/decision/{decisionId}` | Belirli bir kararın oylarını listele |
| GET | `/api/v1/votes/user/{userId}` | Belirli bir kullanıcının oylarını listele |
| GET | `/api/v1/votes/decision/{decisionId}/count` | Kararın oy sayısı ve kullanıcının oy durumu |
| GET | `/api/v1/votes/check?userId=X&decisionId=Y` | Kullanıcının oy verip vermediğini kontrol et |
| POST | `/api/v1/votes` | Karara oy ver |
| DELETE | `/api/v1/votes/{id}` | Oyu ID ile sil |
| DELETE | `/api/v1/votes?userId=X&decisionId=Y` | Oyu kullanıcı ve karar ID ile sil (unvote) |

### 9. Decision-Tag İlişkisi Tamamlandı

**Sorun**: `DecisionRequest.tagIds` alanı mevcuttu ama `DecisionService.createDecision()` metodunda **hiç kullanılmıyordu**. Tagler kararla birlikte kaydedilmiyordu!

**Çözüm**:

**Yeni Eklenen Dosyalar**:
- `DecisionTagRepository.java` - Junction tablo repository

**Güncellenen Dosyalar**:
- `DecisionService.java` - Tag ilişkisi eklendi
  - `createDecision()` - Karar oluştururken tagleri ilişkilendirme
  - `updateDecision()` - Karar güncellerken tagleri değiştirme
  - `getDecisionsByTagId()` - Tag'e göre karar filtreleme
  - `associateTagsWithDecision()` - Yardımcı metod: Tagleri kararla ilişkilendirme
  - `updateDecisionTags()` - Yardımcı metod: Tag ilişkilerini güncelleme
- `DecisionController.java` - Yeni endpoint eklendi
  - `GET /api/v1/decisions/tag/{tagId}` - Tag'e göre kararları filtrele

**Özellikler**:
- ✅ Karar oluştururken `tagIds` ile tagler otomatik atanır
- ✅ Karar güncellenirken eski tagler silinir, yeni tagler eklenir
- ✅ Tag ID'ye göre karar filtreleme desteği
- ✅ Composite key: `decisionId + tagId` (DecisionTagId)
- ✅ Cascade delete: Decision silindiğinde ilişkiler otomatik silinir
- ✅ Tag bulunamazsa 404 ResourceNotFoundException

**API Workflow**:
```
1. Önce Tagleri Oluştur:
   POST /api/v1/tags
   { "name": "programming" }
   → Response: { id: "tag-uuid-1", name: "programming" }

2. Karar Oluştur (Taglerle):
   POST /api/v1/decisions
   {
     "title": "Should I learn Spring Boot?",
     "why": "...",
     "regretLevel": "LOW",
     "userId": "user-uuid",
     "tagIds": ["tag-uuid-1", "tag-uuid-2"]  ← ARTIK ÇALIŞIYOR!
   }
   → Response: { ... "tags": ["programming", "career"] }

3. Tag'e Göre Kararları Getir:
   GET /api/v1/decisions/tag/{tagId}
   → Tag'e sahip tüm kararlar listelenir
```

---

## 🚀 v1.0.0 — REST API Redesign (2026-04-26)

### 📋 Genel Değişiklikler

**Amaç**: REST API design principles'a tam uyumlu, scalable ve maintainable bir yapı oluşturmak.

### ✨ Yeni Özellikler

#### 1. REST API Design Rules Uygulandı

**Filtering Rule** — SYSTEM.md Rule #2:
```bash
# ✅ Doğru — Query params ile filtreleme
GET /api/v1/comments?decisionId={id}
GET /api/v1/comments?userId={id}
GET /api/v1/decisions?tagId={id}&regretLevel=HIGH

# ❌ Kaldırıldı — Endpoint duplication
GET /api/v1/comments/decisions/{decisionId}
GET /api/v1/comments/users/{userId}
GET /api/v1/decisions/users/{userId}
GET /api/v1/decisions/regret-levels/{level}
GET /api/v1/decisions/tags/{tagId}
```

**Nested Resources Rule** — SYSTEM.md Rule #3:
```bash
# ✅ Nested paths — Parent-child ilişkiler için
GET /api/v1/decisions/{decisionId}/comments
GET /api/v1/decisions/{decisionId}/tags
GET /api/v1/users/{userId}/comments
```

#### 2. DecisionController Yenilendi

**Base Path**: `/api/v1/decisions`

**Yeni Endpoint Yapısı**:
| Endpoint | Method | Açıklama |
|----------|--------|----------|
| `/decisions` | GET | Tüm kararlar (query: `userId`, `regretLevel`, `tagId`) |
| `/decisions/{id}` | GET | Karar detayı |
| `/decisions/{decisionId}/comments` | GET | Kararın yorumları |
| `/decisions/{decisionId}/tags` | GET | Kararın etiketleri |
| `/decisions` | POST | Yeni karar oluştur |
| `/decisions/{id}` | PUT | Karar güncelle |
| `/decisions/{id}` | DELETE | Karar sil |

**Kaldırılan Endpoint'ler**:
- `GET /decisions/users/{userId}` → `GET /decisions?userId={id}`
- `GET /decisions/regret-levels/{level}` → `GET /decisions?regretLevel={level}`
- `GET /decisions/tags/{tagId}` → `GET /decisions?tagId={id}`

**Yeni Servis Çağrıları**:
- `DecisionCommentService.getCommentsByDecisionId()` — Comments için
- `DecisionTagService.getTagsByDecisionId()` — Tags için

**Dosyalar**:
- `domain/decision/DecisionController.java` — Tamamen yenilendi

---

#### 3. CommentController Yenilendi

**Base Path**: `/api/v1/comments`

**Yeni Endpoint Yapısı**:
| Endpoint | Method | Açıklama |
|----------|--------|----------|
| `/comments` | GET | Tüm yorumlar (query: `decisionId`, `userId`) |
| `/comments/{id}` | GET | Yorum detayı |
| `/comments` | POST | Yeni yorum oluştur |
| `/comments/{id}` | PUT | Yorum güncelle |
| `/comments/{id}` | DELETE | Yorum sil |

**Kaldırılan Endpoint'ler**:
- `GET /comments/decision/{decisionId}` → `GET /comments?decisionId={id}`
- `GET /comments/user/{userId}` → `GET /comments?userId={id}`

**Yeni Service Metodları**:
```java
// CommentService
getCommentsByDecisionId(UUID decisionId, Pageable pageable)
getCommentsByUserId(UUID userId, Pageable pageable)
getCommentsByDecisionIdAndUserId(UUID decisionId, UUID userId, Pageable pageable)

// CommentRepository
Page<Comment> findByDecisionIdAndUserId(UUID decisionId, UUID userId, Pageable pageable)
```

**Dosyalar**:
- `domain/comment/CommentController.java` — Tamamen yenilendi
- `domain/comment/CommentService.java` — Yeni filtreleme metodları eklendi
- `domain/comment/CommentRepository.java` — Yeni query metodu eklendi

---

#### 4. VoteController Yenilendi

**Base Path**: `/api/v1/votes`

**Yeni Endpoint Yapısı**:
| Endpoint | Method | Açıklama |
|----------|--------|----------|
| `/votes` | GET | Tüm oylar (query: `decisionId`, `userId`) |
| `/votes/{id}` | GET | Oy detayı |
| `/votes/decisions/{decisionId}/count` | GET | Oy sayısı + user status |
| `/votes` | POST | Yeni oy ver |
| `/votes/{id}` | DELETE | Oy sil (ID ile) |
| `/votes/users/{userId}/decisions/{decisionId}` | DELETE | Oy geri al |

**Kaldırılan Endpoint'ler**:
- `GET /votes/decision/{decisionId}` → `GET /votes?decisionId={id}`
- `GET /votes/user/{userId}` → `GET /votes?userId={id}`
- `GET /votes/check` → Query params ile kontrol

**Dosyalar**:
- `domain/vote/VoteController.java` — Tamamen yenilendi

---

#### 5. TagController Yenilendi

**Base Path**: `/api/v1/tags`

**Yeni Endpoint Yapısı**:
| Endpoint | Method | Açıklama |
|----------|--------|----------|
| `/tags` | GET | Tüm etiketler |
| `/tags/{id}` | GET | Etiket detayı |
| `/tags/name/{name}` | GET | Etiket ile ara |
| `/tags` | POST | Yeni etiket oluştur |
| `/tags/{id}` | PUT | Etiket güncelle |
| `/tags/{id}` | DELETE | Etiket sil |

**Kaldırılan Endpoint'ler**:
- `GET /tags/{id}/decisions` → `GET /decisions?tagId={id}` (DecisionController'da)

**Değişiklikler**:
- `DecisionService` dependency kaldırıldı
- Sadece kendi resource'una odaklanıyor

**Dosyalar**:
- `domain/tag/TagController.java` — Tamamen yenilendi

---

#### 6. DecisionTagService Genişletildi

**Yeni Metod**:
```java
public BaseResponse<List<TagResponse>> getTagsByDecisionId(UUID decisionId)
```

**Özellikler**:
- Decision'a ait tüm tagleri getirir
- Her tag için `TagService.getTagById()` çağırır
- Join yerine service composition kullanır

**Dosyalar**:
- `domain/decisiontag/DecisionTagService.java` — Yeni metod eklendi

---

### 🏗️ Mimari Değişiklikler

#### Service Composition Pattern

**Önceki Yaklaşım**:
```java
// ❌ Repository'ler arası join
@Query("SELECT d FROM Decision d JOIN d.tags t WHERE t.id = :tagId")
Page<Decision> findByTagId(UUID tagId);
```

**Yeni Yaklaşım**:
```java
// ✅ Service composition
@GetMapping("/{decisionId}/tags")
public ResponseEntity<BaseResponse<List<TagResponse>>> getDecisionTags(
    @PathVariable UUID decisionId) {
    return ResponseEntity.ok(
        decisionTagService.getTagsByDecisionId(decisionId)
    );
}
```

**Avantajlar**:
- Loose coupling
- Her service kendi responsibility'sine odaklanır
- Kolay test edilebilir
- Performance optimization flexible

---

### 📊 Endpoint Summary

| Controller | Base Path | Endpoints | Değişiklik |
|------------|-----------|-----------|------------|
| Auth | `/api/v1/auth` | 1 | Değişiklik yok |
| Decision | `/api/v1/decisions` | 7 | Filtering query params, nested resources |
| Comment | `/api/v1/comments` | 5 | Filtering query params |
| Vote | `/api/v1/votes` | 6 | Filtering query params |
| Tag | `/api/v1/tags` | 6 | Decision endpoint kaldırıldı |
| RegularUser | `/api/v1/users` | 5 | Değişiklik yok |
| CompanyUser | `/api/v1/companies` | 4 | Değişiklik yok |

**Toplam**: 34 endpoint (7 azaltıldı, 2 eklendi)

---

### ✅ Build Status

```bash
[INFO] BUILD SUCCESS
[INFO] Compiling 74 source files
[INFO] Total time: 3.459 s
```

---

### 📚 Yeni Dokümantasyon

- `docs/project-idea.md` — Kapsamlı proje dokümantasyonu
  - Proje nedir, neden var
  - Tech stack detayları
  - Functional & Non-functional requirements
  - Prensipler ve kurallar
  - Services & endpoints
  - Entities & ilişkiler
  
- `docs/07-api-endpoints.md` — Detaylı API dokümantasyonu
  - Her endpoint için örnek request/response
  - Query parameters
  - Authentication examples
  - Pagination usage
  - Error codes

---

### 🎯 Sonuç

**Ölçeklenebilirlik**: ⭐⭐⭐⭐⭐  
**Okunabilirlik**: ⭐⭐⭐⭐⭐  
**Maintainability**: ⭐⭐⭐⭐⭐  
**REST Compliance**: ⭐⭐⭐⭐⭐

---

**Son Güncelleme**: 2026-04-26
