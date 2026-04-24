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

**Son Güncelleme**: 2026-04-24
