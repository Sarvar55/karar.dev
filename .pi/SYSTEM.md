# SYSTEM.md — karar.dev Engineering Guidelines

---

## 🎯 Amaç

Bu dosya, karar.dev projesinde **tutarlı, sürdürülebilir ve ölçeklenebilir** bir backend geliştirmek için tüm mühendislik kurallarını tanımlar.

Bu kurallar:

* Tartışmaya açık değildir (default olarak)
* Tüm ekip tarafından uygulanmalıdır
* Kod review’ların temelidir

---

# REST API Design Rules – Filtering & Resource Structure

## 1. General Principle

Avoid creating multiple endpoints for the same resource when the difference is only filtering criteria.

Instead, prefer a single endpoint with query parameters.

---

## 2. Filtering Rule (MUST)

Use query parameters for filtering resources.

### ✅ Correct

GET /api/v1/comments
GET /api/v1/comments?decisionId={id}
GET /api/v1/comments?userId={id}
GET /api/v1/comments?decisionId={id}&userId={id}

### ❌ Avoid

GET /api/v1/comments/decisions/{decisionId}
GET /api/v1/comments/users/{userId}

Reason:

* Reduces endpoint duplication
* Improves flexibility
* Simplifies maintenance and security configuration

---

## 3. Nested Resources Rule (ALLOWED)

Use nested paths only when representing a true parent-child relationship.

### ✅ Acceptable

GET /api/v1/decisions/{decisionId}/comments
GET /api/v1/users/{userId}/comments

### ❌ Avoid incorrect nesting

GET /api/v1/comments/decisions/{decisionId}

---

## 4. HTTP Method Semantics (MUST)

GET     → Retrieve data
POST    → Create resource
PUT     → Full update
PATCH   → Partial update
DELETE  → Remove resource

---

## 5. Endpoint Simplicity (MUST)

* Prefer fewer, more flexible endpoints
* Do not duplicate logic across multiple URLs
* Keep controllers minimal; move logic to service layer

---

## 6. Security Compatibility Rule

Design endpoints so they can be easily secured using HTTP method + path.

Example:

.requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()
.requestMatchers(HttpMethod.POST, "/api/v1/comments").authenticated()

Avoid designs that require custom filters to distinguish endpoints.

---

## 7. Summary

* Use query params for filtering
* Use nested resources only for hierarchy
* Avoid redundant endpoints
* Keep API predictable and scalable


# 🧱 1. Mimari Prensipler

## Katman Yapısı (Layered Architecture)

```
Controller → Service → Repository
```

### Kurallar

1. Controller sadece Service çağırır
2. Service iş mantığını içerir
3. Repository sadece veri erişimidir

❌ Controller → Repository YASAK
❌ Service → başka modül Repository YASAK

---

## Katman Kuralları

* Entity **asla dışarı çıkmaz** → DTO döner
* `@Transactional` sadece Service katmanında
* Controller’da business logic yazılmaz
* common/ paketi domain bağımsızdır

---

# 🧠 2. Clean Code Prensipleri

## KISS (Keep It Simple)

* Gereksiz abstraction YASAK
* Basit çözüm varken complex çözüm YASAK

---

## DRY (Don't Repeat Yourself)

* Tekrarlanan kod → utility / mapper / base class
* Aynı logic birden fazla yerde bulunamaz

---

## YAGNI (You Aren’t Gonna Need It)

* “Belki lazım olur” diye kod yazılmaz

---

## SOLID

### S — Single Responsibility

Her class tek iş yapar

### O — Open/Closed

Yeni feature → extend et, değiştirme

### L — Liskov

Subclass → parent yerine geçebilmeli

### I — Interface Segregation

Büyük interface YASAK

### D — Dependency Inversion

Somuta değil, abstraction’a bağlı ol

---

# 🏗️ 3. Kod Kalitesi Kuralları

* Method isimleri anlamlı olmalı (`createDecision`, `toggleVote`)
* Method’lar küçük olmalı
* Her public Service metodu **tek iş yapar**
* Null dönülmez → Optional veya Exception
* Magic number/string YASAK → AppConstants

---

## Validation

* Controller’da başlar (`@Valid`)
* Service’te tekrar edilmez

---

## Exception Handling

Tüm hatalar bu formatta döner:

```json
{
  "code": "ERROR_CODE",
  "message": "Readable message",
  "fieldErrors": {}
}
```

---

# 🧾 4. DTO Kuralları

* Her endpoint için ayrı DTO
* DTO reuse YASAK
* Request DTO → `record`
* Response DTO → immutable

```java
public record CreateDecisionRequest(
    String title,
    String why,
    String alternatives
) {}
```

---

# 🧩 5. ENUM Kullanımı

Sabit değerler ENUM olur:

```java
public enum Role {
    USER,
    COMPANY,
    ADMIN
}
```

---

# 🧮 6. Constants

```java
public final class AppConstants {
    public static final int MAX_TAGS = 5;
}
```

---

# 🔐 7. Security Kuralları

* Auth → JWT
* Authorization → `@PreAuthorize`
* Role prefix → `ROLE_`

```java
@PreAuthorize("hasRole('USER')")
```

---

# 📦 8. Folder Structure

```
com.karar.dev/
├── common/
│   ├── config/
│   ├── exception/
│   ├── response/
│   └── util/
│
├── domain/
│   ├── auth/
│   ├── user/
│   ├── decision/
│   ├── vote/
│   ├── comment/
│   └── tag/
```

---

# 🗄️ 9. Configuration Yönetimi

## application.yml

```yaml
app:
  jwt:
    secret: ${JWT_SECRET}
    access-expiration: 900000
```

---

## Profile Bazlı Config

### application-local.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/db
  jpa:
    show-sql: true
```

---

### application-dev.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost/dev
```

---

### application-prod.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://prod-db
```

---

# 🪵 10. Logging

## Pattern

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%level] [%X{traceId}] %logger - %msg%n"
```

## Kurallar

* Controller → INFO
* Service → DEBUG
* Error → ERROR
* Stack trace dışarı çıkmaz

---

# 🧪 11. Test Stratejisi

| Katman     | Test        |
| ---------- | ----------- |
| Service    | Unit        |
| Controller | WebMvcTest  |
| DB         | Integration |

---

# 🐳 12. Docker Yapısı

## docker-compose.local.yml

```yaml
services:
  app:
    build: .
    environment:
      - SPRING_PROFILES_ACTIVE=local
```

---

## docker-compose.dev.yml

```yaml
services:
  app:
    environment:
      - SPRING_PROFILES_ACTIVE=dev
```

---

## docker-compose.prod.yml

```yaml
services:
  app:
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```

---

# 🧩 13. Modülerlik

* Her domain bağımsızdır
* Cross dependency minimum
* Ortak kod → common/

---

# 🚫 14. Yasaklar

❌ Controller’da business logic
❌ Entity return etmek
❌ Magic string/number
❌ Null return
❌ Copy-paste kod
❌ God class
❌ Çok uzun method

---

# ✅ 15. Doğru Kod Örneği

```java
@PreAuthorize("hasRole('USER')")
@PostMapping
public ResponseEntity<DecisionResponse> createDecision(
        @AuthenticationPrincipal UserDetailsImpl user,
        @Valid @RequestBody CreateDecisionRequest request
) {
    return ResponseEntity.status(201)
        .body(decisionService.createDecision(user.getUser(), request));
}
```

---

# 🚀 Final

Bu sistem:

* Ölçeklenebilir
* Okunabilir
* Test edilebilir
* Maintainable

Kod yazarken her zaman şunu sor:

👉 “Bu kod 6 ay sonra okunabilir mi?”

Eğer cevap hayırsa → yeniden yaz.
