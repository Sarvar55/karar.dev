# SYSTEM.md — karar.dev Engineering Guidelines

---

## 🎯 Amaç

Bu doküman, karar.dev backend geliştirme sürecinde **tutarlılık, sürdürülebilirlik ve ölçeklenebilirlik** sağlamak için mühendislik kurallarını tanımlar.

Bu kurallar:

* Varsayılan olarak tartışmaya kapalıdır
* Tüm ekip tarafından uygulanmalıdır
* Code review süreçlerinin temelini oluşturur

---

# 🌐 REST API Design Rules

## 1. General Principle

Aynı resource için sadece filtre farklı diye ayrı endpoint yazılmaz.

👉 Tek endpoint + query param yaklaşımı tercih edilir.

---

## 2. Filtering Rule (MUST)

### ✅ Doğru

```
GET /api/v1/comments
GET /api/v1/comments?decisionId={id}
GET /api/v1/comments?userId={id}
GET /api/v1/comments?decisionId={id}&userId={id}
```

### ❌ Kaçınılmalı

```
GET /api/v1/comments/decisions/{decisionId}
GET /api/v1/comments/users/{userId}
```

### Neden?

* Endpoint duplication azaltır
* Daha esnek yapı sağlar
* Security config sadeleşir

---

## 3. Nested Resources Rule (ALLOWED)

Gerçek parent-child ilişkilerinde kullanılabilir.

### ✅ Doğru

```
GET /api/v1/decisions/{decisionId}/comments
GET /api/v1/users/{userId}/comments
```

### ❌ Yanlış

```
GET /api/v1/comments/decisions/{decisionId}
```

---

## 4. HTTP Method Semantics (MUST)

| Method | Açıklama           |
| ------ | ------------------ |
| GET    | Veri çekme         |
| POST   | Resource oluşturma |
| PUT    | Tam update         |
| PATCH  | Kısmi update       |
| DELETE | Silme              |

---

## 5. Endpoint Simplicity

* Az ama güçlü endpoint yaz
* Aynı logic’i farklı URL’lerde tekrar etme
* Controller sade olmalı

---

## 6. Security Compatibility

Endpoint’ler **kolay authorize edilebilir** olmalı.

### ✅ Doğru

```java
.requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()
.requestMatchers(HttpMethod.POST, "/api/v1/comments").authenticated()
```

### ❌ Kaçınılmalı

* Endpoint’i ayırt etmek için custom logic gerektiren tasarımlar

---

## 7. Summary

* Filtreleme → query param
* Hiyerarşi → nested path
* Endpoint duplication → yasak
* API → predictable olmalı

---

# 🏗️ Mimari Prensipler

## Katmanlı Mimari

```
Controller → Service → Repository
```

### Kurallar

* Controller sadece Service çağırır
* Service business logic içerir
* Repository sadece data access yapar

### Yasaklar

* ❌ Controller → Repository
* ❌ Service → başka domain Repository

---

## Katman Kuralları

* Entity dışarı çıkmaz → DTO döner
* `@Transactional` sadece Service katmanında
* Controller’da business logic yazılmaz
* `common/` domain bağımsızdır

---

# 🧼 Clean Code Prensipleri

## KISS

* Basit çözüm varken kompleks çözüm YASAK

## DRY

* Tekrar eden kod merkezi hale getirilir

## YAGNI

* “Belki lazım olur” kodu yazılmaz

---

## SOLID

* S → Single Responsibility
* O → Open/Closed
* L → Liskov
* I → Interface Segregation
* D → Dependency Inversion

---

# 🧩 Kod Kalitesi

* Method isimleri anlamlı olmalı
* Methodlar küçük olmalı
* Her public service metodu tek iş yapmalı
* Null dönülmez → Optional veya Exception
* Magic value kullanılmaz → constant

---

## Validation

* Controller’da başlar (`@Valid`)
* Service’te tekrar edilmez

---

## Exception Handling

Tüm hatalar standart formatta döner:

```json
{
  "code": "ERROR_CODE",
  "message": "Readable message",
  "fieldErrors": {}
}
```

---

# 📦 DTO Kuralları

* Her endpoint için ayrı DTO
* DTO reuse YASAK
* Request DTO → `record`
* Response DTO → immutable

---

# 🔢 Enum Kullanımı

```java
public enum Role {
    USER,
    COMPANY,
    ADMIN
}
```

---

# 📌 Constants

```java
public final class AppConstants {
    public static final int MAX_TAGS = 5;
}
```

---

# 🔐 Security Kuralları

* Authentication → JWT
* Authorization → `@PreAuthorize`
* Role prefix → `ROLE_`

```java
@PreAuthorize("hasRole('USER')")
```

---

# 📁 Folder Structure

```
com.karar.dev/
 ├── common/
 │   ├── config/
 │   ├── exception/
 │   ├── response/
 │   └── util/
 │
 └── domain/
     ├── auth/
     ├── user/
     ├── decision/
     ├── vote/
     ├── comment/
     └── tag/
```

---

# ⚙️ Configuration

## application.yml

```yaml
app:
  jwt:
    secret: ${JWT_SECRET}
    access-expiration: 900000
```

---

## Profiles

### local

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/db
  jpa:
    show-sql: true
```

### dev

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost/dev
```

### prod

```yaml
spring:
  datasource:
    url: jdbc:postgresql://prod-db
```

---

# 📊 Logging

* Controller → INFO
* Service → DEBUG
* Error → ERROR
* Stack trace client’a dönülmez

---

# 🧪 Test Stratejisi

| Katman     | Test Türü   |
| ---------- | ----------- |
| Service    | Unit        |
| Controller | WebMvcTest  |
| DB         | Integration |

---

# 🐳 Docker

### local

```yaml
SPRING_PROFILES_ACTIVE=local
```

### dev

```yaml
SPRING_PROFILES_ACTIVE=dev
```

### prod

```yaml
SPRING_PROFILES_ACTIVE=prod
```

---

# 🧱 Modülerlik

* Domain’ler bağımsızdır
* Cross dependency minimum
* Ortak kod → common

---

# 🚫 Yasaklar

* Controller’da business logic
* Entity return etmek
* Magic value
* Null return
* Copy-paste kod
* God class
* Uzun method

---

# ✅ Doğru Kod Örneği

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

# 🎯 Final

Bu sistem:

* Ölçeklenebilir
* Okunabilir
* Test edilebilir
* Maintainable

Kod yazarken sor:

👉 “Bu kod 6 ay sonra anlaşılır mı?”

Cevap hayırsa → yeniden yaz.
