# 🔒 Güvenlik Katmanı Sağlamlaştırma (Security Hardening)

**Tarih**: 2026-05-14  
**Branch**: `fix/security-auth-hardening`  
**Etkilenen Modüller**: Security, Decision, Comment, Vote

---

## 📋 Genel Bakış

Bu dokümanda, Karar.dev platformunun güvenlik modülünde tespit edilen **6 güvenlik açığı** ve bunların nasıl giderildiği açıklanmaktadır. En kritik düzeltme, `userId`'nin istemci (client) tarafından gönderilebilmesinin engellenmesidir.

---

## 🔴 Sorun 1 — Create İşlemlerinde `userId` İstemciden Geliyordu

### Neydi?

`DecisionRequest`, `CommentRequest` ve `VoteRequest` DTO'larında `userId` alanı mevcuttu. İstemci, request body'de istediği herhangi bir `userId`'yi gönderebiliyordu.

### Neden Tehlikeli?

Authenticated bir kullanıcı (`user-A`) request body'e `"userId": "user-B"` yazarak başka bir kullanıcının adına karar, yorum veya oy oluşturabiliyordu. Sistem bu değeri doğrulamadan kabul ediyordu.

**Saldırı Senaryosu:**

```bash
# user-A olarak giriş yap, ama user-B adına karar oluştur
curl -X POST /api/v1/decisions \
  -H "Authorization: Bearer <user-A-token>" \
  -d '{
    "title": "Sahte Karar",
    "why": "...",
    "regretLevel": "LOW",
    "userId": "user-B-uuid"   ← 🔴 Başkası adına işlem!
  }'
```

### Nasıl Düzeltildi?

**DTO'lardan `userId` alanı tamamen kaldırıldı.** Kullanıcı kimliği artık JWT token'dan `SecurityContextHolder` üzerinden alınıyor.

**Önceki Durum (Tehlikeli):**

```java
// DecisionRequest.java
public record DecisionRequest(
    String title,
    String why,
    UUID userId,   // ❌ İstemciden geliyor — sahtecilik riski!
    Set<UUID> tagIds
) {}

// DecisionService.java
public BaseResponse<DecisionResponse> createDecision(DecisionRequest request) {
    RegularUser user = regularUserService.getById(request.userId());  // ❌
    // ...
}
```

**Sonraki Durum (Güvenli):**

```java
// DecisionRequest.java
public record DecisionRequest(
    String title,
    String why,
    RegretLevel regretLevel,
    Set<UUID> tagIds
    // userId YOK — JWT'den alınacak
) {}

// DecisionService.java
public BaseResponse<DecisionResponse> createDecision(DecisionRequest request) {
    UUID currentUserId = securityService.getCurrentUserId();  // ✅ JWT'den
    RegularUser user = regularUserService.getById(currentUserId);
    // ...
}
```

**Etkilenen Dosyalar:**

| Dosya | Değişiklik |
|-------|-----------|
| `domain/decision/dto/DecisionRequest.java` | `userId` alanı kaldırıldı |
| `domain/comment/dto/CommentRequest.java` | `userId` alanı kaldırıldı |
| `domain/vote/dto/VoteRequest.java` | `userId` alanı kaldırıldı |
| `domain/decision/DecisionService.java` | `request.userId()` → `securityService.getCurrentUserId()` |
| `domain/comment/CommentService.java` | `request.userId()` → `securityService.getCurrentUserId()` |
| `domain/vote/VoteService.java` | `request.userId()` → `securityService.getCurrentUserId()` |

---

## 🔴 Sorun 2 — JWT Token'dan `userId` Çıkarılmıyordu

### Neydi?

JWT token oluşturulurken `userId` ve `role` claim olarak ekleniyordu, ancak token parse edilirken sadece `subject` (email) alınıyordu. Token'daki `userId` hiç kullanılmıyordu.

### Neden Sorun?

Her sahiplik kontrolünde (`SecurityService.isOwnerOfDecision()`, vb.) veritabanından kullanıcı çekmek gerekiyordu. Oysa bu bilgi zaten token'da mevcut.

### Nasıl Düzeltildi?

`JWTService`'e token'dan claim çıkarma metotları eklendi:

```java
// JWTService.java — Yeni Metotlar
public UUID getUserIdFromToken(String token) {
    String userId = getClaims(token).get("userId", String.class);
    return UUID.fromString(userId);
}

public String getRoleFromToken(String token) {
    return getClaims(token).get("role", String.class);
}

private Claims getClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(key).build()
            .parseClaimsJws(token)
            .getBody();
}
```

> **Not:** Ayrıca eski `getUsernameFromToken()` metodu da artık ortak `getClaims()` metodunu kullanıyor — kod tekrarı giderildi.

**Etkilenen Dosya:** `common/security/service/JWTService.java`

---

## 🟢 Sorun 6 — `getCurrentUserId()` Helper Eksikti

### Neydi?

Servis katmanında "şu anki authenticated kullanıcının ID'si nedir?" bilgisine erişmek için standart bir yol yoktu. Her servis kendi başına `SecurityContextHolder`'a erişmek zorundaydı.

### Nasıl Düzeltildi?

`SecurityService`'e üç yeni helper metot eklendi:

```java
// SecurityService.java — Yeni Metotlar

/**
 * JWT token'daki kullanıcı ID'sini döndürür.
 */
public UUID getCurrentUserId() {
    SecurityUser securityUser = getCurrentSecurityUser();
    return securityUser.getUserId();
}

/**
 * JWT token'daki kullanıcı e-postasını döndürür.
 */
public String getCurrentUserEmail() {
    SecurityUser securityUser = getCurrentSecurityUser();
    return securityUser.getUsername();
}

/**
 * SecurityContextHolder'dan SecurityUser nesnesini çıkarır.
 * Eğer authenticated kullanıcı yoksa IllegalStateException fırlatır.
 */
private SecurityUser getCurrentSecurityUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
        throw new IllegalStateException("No authenticated user found in SecurityContext");
    }
    return (SecurityUser) authentication.getPrincipal();
}
```

**Etkilenen Dosya:** `common/security/service/SecurityService.java`

---

## 🟡 Sorun 3 — `shouldNotFilter` Path Uyumsuzluğu

### Neydi?

`AuthenticationTokenFilter` içindeki `shouldNotFilter()` metodu yanlış path kontrolü yapıyordu:

```java
// ÖNCEKİ — Tehlikeli
return request.getRequestURI().contains("/api/auth");
```

Gerçek auth endpoint path'i `/api/v1/auth/**`. `contains()` kullanıldığı için tesadüfen çalışıyordu, ama gelecekte `/api/v2/auth-settings` gibi bir endpoint eklenirse o da yanlışlıkla exclude edilirdi.

### Nasıl Düzeltildi?

```java
// SONRAKİ — Güvenli
String path = request.getRequestURI();
return path.startsWith("/api/v1/auth/");
```

`startsWith()` kullanarak tam path eşleşmesi sağlandı.

**Etkilenen Dosya:** `common/security/filter/AuthenticationTokenFilter.java`

---

## 🟡 Sorun 4 — Logging Güvenlik ve Performans Sorunları

### Neydi?

İki ayrı sorun vardı:

1. **Authorization header loglanıyordu** — `log.info("Authorization header: {}", headerAuth)` satırı, JWT token'ı production loglarına yazdırıyordu.
2. **Yanlış log seviyeleri** — Başarılı authentication `INFO` seviyesinde loglanıyordu (DEBUG olmalı), authentication hatası da `INFO` seviyesindeydi (WARN olmalı).

### Nasıl Düzeltildi?

| Önceki | Sonraki | Neden |
|--------|---------|-------|
| `log.info("Authorization header: {}", headerAuth)` | Kaldırıldı | Token'ı loglamak güvenlik riski |
| `log.info("Authenticated user: {}", username)` | `log.debug(...)` | Her request'te loglanmamalı |
| `log.info("Failed to authenticate token: {}")` | `log.warn(...)` | Hata durumu, uyarı olmalı |
| — | `log.debug("Bearer token found in request")` | Token varsa debug logu yeterli |

**Etkilenen Dosya:** `common/security/filter/AuthenticationTokenFilter.java`

---

## 🟡 Sorun 5 — Dev Profilde CORS Aktif Değildi

### Neydi?

`ProjectDevSecurityConfig`'de `corsConfigurationSource()` bean'i tanımlıydı ama `SecurityFilterChain`'de `http.cors(...)` çağrılmamıştı. Bu yüzden CORS kuralları hiç uygulanmıyordu.

```java
// ÖNCEKİ — CORS bean tanımlı ama aktif değil!
@Bean
SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)       // ✅
        .authorizeHttpRequests(...)                   // ✅
        // .cors(...)  ← 🔴 EKSİK!
        .sessionManagement(...)
        .exceptionHandling(...);
    return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() { ... }  // Kullanılmıyor!
```

### Nasıl Düzeltildi?

```java
// SONRAKİ — CORS aktif
http
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // ✅ Eklendi
    .csrf(AbstractHttpConfigurer::disable)
    .authorizeHttpRequests(...)
    .sessionManagement(...)
    .exceptionHandling(...);
```

**Etkilenen Dosya:** `common/security/config/ProjectDevSecurityConfig.java`

---

## 📊 Değişiklik Özeti

### Değiştirilen Dosyalar

| Dosya | Tür | Açıklama |
|-------|-----|----------|
| `JWTService.java` | ✏️ Güncelleme | `getUserIdFromToken()`, `getRoleFromToken()`, `getClaims()` eklendi |
| `SecurityService.java` | ✏️ Güncelleme | `getCurrentUserId()`, `getCurrentUserEmail()`, `getCurrentSecurityUser()` eklendi |
| `AuthenticationTokenFilter.java` | ✏️ Güncelleme | `shouldNotFilter` path fix, log seviyeleri düzeltildi |
| `ProjectDevSecurityConfig.java` | ✏️ Güncelleme | CORS aktif edildi |
| `DecisionRequest.java` | ✏️ Güncelleme | `userId` alanı kaldırıldı |
| `CommentRequest.java` | ✏️ Güncelleme | `userId` alanı kaldırıldı |
| `VoteRequest.java` | ✏️ Güncelleme | `userId` alanı kaldırıldı |
| `DecisionService.java` | ✏️ Güncelleme | `SecurityService` dependency eklendi, `createDecision` güncellendi |
| `CommentService.java` | ✏️ Güncelleme | `SecurityService` dependency eklendi, `createComment` güncellendi |
| `VoteService.java` | ✏️ Güncelleme | `SecurityService` dependency eklendi, `createVote` güncellendi |
| `DecisionRequestBuilder.java` | ✏️ Test | `userId` alanı ve `withUserId()` metodu kaldırıldı |
| `DecisionServiceTest.java` | ✏️ Test | `SecurityService` mock eklendi, create testleri güncellendi |

### API Değişiklikleri (⚠️ Breaking Change)

Aşağıdaki endpoint'lerin request body'si değişti:

**POST `/api/v1/decisions`**
```diff
{
    "title": "Should I learn Spring Boot?",
    "why": "I want to improve my backend skills",
    "alternative": "Django, Node.js",
    "regretLevel": "LOW",
-   "userId": "550e8400-e29b-41d4-a716-446655440000",
    "tagIds": ["tag-uuid-1"]
}
```

**POST `/api/v1/comments`**
```diff
{
    "content": "Great decision!",
-   "userId": "550e8400-e29b-41d4-a716-446655440000",
    "decisionId": "550e8400-e29b-41d4-a716-446655440001"
}
```

**POST `/api/v1/votes`**
```diff
{
-   "userId": "550e8400-e29b-41d4-a716-446655440000",
    "decisionId": "550e8400-e29b-41d4-a716-446655440001"
}
```

> **Önemli:** Bu değişiklik geriye dönük uyumlu **DEĞİLDİR**. Frontend tarafında da bu alanların kaldırılması gerekir.

---

## 🧪 Test Durumu

| Test Sınıfı | Durum | Not |
|-------------|-------|-----|
| `DecisionServiceTest` | ✅ Geçti | `SecurityService` mock'u ile güncellendi |
| Diğer testler | ✅ Geçti | Değişiklikten etkilenmedi |

---

## 🔐 Güvenlik Akış Diyagramı (Sonraki Durum)

```
İstemci İsteği
    │
    ▼
[Authorization: Bearer <JWT>]
    │
    ▼
AuthenticationTokenFilter
    ├── JWT geçerli mi? → validateJwtToken()
    ├── Email çıkar → getUsernameFromToken()
    ├── DB'den kullanıcı yükle → loadUserByUsername()
    └── SecurityContextHolder'a kaydet
    │
    ▼
Controller (POST /api/v1/decisions)
    │ Request Body: { title, why, regretLevel, tagIds }
    │ userId YOK!
    │
    ▼
DecisionService.createDecision()
    ├── securityService.getCurrentUserId()   ← JWT'den
    ├── regularUserService.getById(userId)   ← DB doğrulama
    ├── Decision oluştur
    └── Kaydet ve döndür
```

---

**Son Güncelleme**: 2026-05-14
