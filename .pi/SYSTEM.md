# SYSTEM.md — karar.dev Engineering Guidelines

---

## 🎯 Amaç

Bu dosya, karar.dev projesinde **tutarlı, sürdürülebilir ve ölçeklenebilir** bir backend geliştirmek için tüm mühendislik kurallarını tanımlar.

Bu kurallar:

* Tartışmaya açık değildir (default olarak)
* Tüm ekip tarafından uygulanmalıdır
* Kod review’ların temelidir

---

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
