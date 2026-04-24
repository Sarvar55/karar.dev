# Modül Dokümantasyonu

## 🔐 Auth Modülü

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

---

## 👤 User Modülü

### RegularUser (Bireysel Kullanıcı)

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

### CompanyUser (Kurumsal Kullanıcı)

**Amaç**: Kurumsal kullanıcı yönetimi.

**Dosyalar**:
- `CompanyUserController.java`
- `CompanyUserService.java`
- `CompanyUserRepository.java`
- `dto/CompanyUserResponse.java`
- `dto/CompanyUserUpdateRequest.java`

---

## 🎯 Decision Modülü

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
| GET | `/api/v1/decisions/tag/{tagId}` | Tag'e göre kararları filtrele |
| POST | `/api/v1/decisions` | Yeni karar oluştur (taglerle birlikte) |
| PUT | `/api/v1/decisions/{id}` | Kararı güncelle (tagleri değiştir) |
| DELETE | `/api/v1/decisions/{id}` | Kararı sil (tag ilişkileri cascade delete) |

**Decision Entity Alanları**:
- `title`: Karar başlığı
- `why`: Kararın nedeni
- `alternative`: Düşünülen alternatifler
- `regretLevel`: Pişmanlık seviyesi (LOW, MEDIUM, HIGH)
- `voteCount`: Oy sayısı
- `user`: Kararı oluşturan kullanıcı
- `comments`: Karara yapılan yorumlar
- `tags`: Karar etiketleri (Many-to-Many via DecisionTag)
- `votes`: Karara verilen oylar

**Tag İlişkisi Özellikleri**:
- ✅ Karar oluştururken `tagIds` array'i ile tagler atanabilir
- ✅ Karar güncellenirken tagler değiştirilebilir (eski tagler silinir, yenileri eklenir)
- ✅ Tag'a göre karar filtreleme desteği
- ✅ DecisionResponse'te tag isimleri Set<String> olarak döner

---

## 🏷️ Tag Modülü

**Amaç**: Kararları kategorize etmek için etiket yönetimi.

**Dosyalar**:
- `Tag.java` - Tag entity
- `TagController.java` - REST API endpoints
- `TagService.java` - Business logic
- `TagRepository.java` - Data access
- `dto/TagRequest.java` - Create request DTO
- `dto/TagResponse.java` - Response DTO
- `dto/TagUpdateRequest.java` - Update request DTO

---

## 🔗 DecisionTag Modülü (Junction Entity)

**Amaç**: Decision ve Tag arasındaki **Many-to-Many** ilişkiyi yönetmek.

**Dosyalar**:
- `DecisionTag.java` - Junction entity (ara tablo)
- `DecisionTagId.java` - Composite key (decisionId + tagId)
- `DecisionTagRepository.java` - Data access

**Neden Önemli?**

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│    Decision     │         │  DecisionTag    │         │      Tag        │
├─────────────────┤         ├─────────────────┤         ├─────────────────┤
│ id (PK)         │◄────────┤ decision_id(FK) │    ┌───►│ id (PK)         │
│ title           │    N:M  │ tag_id (FK)     │────┘   │ name            │
│ ...             │         │ createdAt       │        │ ...             │
└─────────────────┘         └─────────────────┘        └─────────────────┘
```

**Özellikler**:
- ✅ Composite Primary Key: `decisionId + tagId`
- ✅ Decision oluştururken tag ilişkisi otomatik kurulur
- ✅ Decision güncellenirken tagleri değiştirme desteği
- ✅ Tag ID'ye göre decision filtreleme (`/decisions/tag/{tagId}`)
- ✅ Cascade delete: Decision silindiğinde ilişkiler otomatik silinir
- ✅ Tag bulunamazsa 404 ResourceNotFoundException

---

## 🗳️ Vote Modülü

**Amaç**: Kararlara oy verme sistemi ve oy yönetimi.

**Dosyalar**:
- `Vote.java` - Entity sınıfı (unique constraint: user_id + decision_id)
- `VoteController.java` - REST API endpoints
- `VoteService.java` - Business logic
- `VoteRepository.java` - Data access
- `dto/VoteRequest.java` - Create request DTO
- `dto/VoteResponse.java` - Response DTO
- `dto/VoteCountResponse.java` - Vote statistics DTO

**Endpoints**:

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/v1/votes` | Tüm oyları listele |
| GET | `/api/v1/votes/{id}` | ID'ye göre oy getir |
| GET | `/api/v1/votes/decision/{decisionId}` | Kararın oylarını getir |
| GET | `/api/v1/votes/user/{userId}` | Kullanıcının oylarını getir |
| GET | `/api/v1/votes/decision/{decisionId}/count` | Kararın oy sayısını ve kullanıcının oy durumunu getir |
| GET | `/api/v1/votes/check` | Kullanıcının oy verip vermediğini kontrol et |
| POST | `/api/v1/votes` | Karara oy ver |
| DELETE | `/api/v1/votes/{id}` | Oyu ID ile sil |
| DELETE | `/api/v1/votes` | Oyu kullanıcı ve karar ID ile sil (unvote) |

**Vote Entity Alanları**:
- `user`: Oy veren kullanıcı
- `decision`: Oy verilen karar
- **Constraint**: Bir kullanıcı bir karara sadece bir kez oy verebilir (user_id + decision_id unique)

**Özellikler**:
- ✅ Otomatik `voteCount` güncelleme (Decision entity)
- ✅ Duplicate vote kontrolü (409 Conflict)
- ✅ Vote statistics endpoint (oy sayısı + kullanıcı oy durumu)
- ✅ "Unvote" fonksiyonu (oy geri çekme)

---

## 💬 Comment Modülü

**Amaç**: Kararlara yorum yapma ve yorumları yönetme.

**Dosyalar**:
- `Comment.java` - Entity sınıfı
- `CommentController.java` - REST API endpoints
- `CommentService.java` - Business logic
- `CommentRepository.java` - Data access
- `dto/CommentRequest.java` - Create request DTO
- `dto/CommentResponse.java` - Response DTO
- `dto/CommentUpdateRequest.java` - Update request DTO

**Endpoints**:

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/api/v1/comments` | Tüm yorumları listele |
| GET | `/api/v1/comments/{id}` | ID'ye göre yorum getir |
| GET | `/api/v1/comments/decision/{decisionId}` | Kararın yorumlarını getir |
| GET | `/api/v1/comments/user/{userId}` | Kullanıcının yorumlarını getir |
| POST | `/api/v1/comments` | Yeni yorum oluştur |
| PUT | `/api/v1/comments/{id}` | Yorum güncelle |
| DELETE | `/api/v1/comments/{id}` | Yorum sil |

**Comment Entity Alanları**:
- `content`: Yorum içeriği
- `user`: Yorumu yapan kullanıcı
- `decision`: Yorumun yapıldığı karar

---

**Son Güncelleme**: 2026-04-24
