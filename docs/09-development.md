# Geliştirme Ortamı

## 🔧 application.yml Konfigürasyonu

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

## 📁 Profiller

| Profil | Dosya | Amaç |
|--------|-------|------|
| local | `application-local.yml` | Yerel geliştirme |
| dev | `application-dev.yml` | Geliştirme ortamı |
| prod | `application-prod.yml` | Üretim ortamı |

## 🚀 Çalıştırma

```bash
# Geliştirme (H2)
./mvnw spring-boot:run

# Veya
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./mvnw spring-boot:run

# Belirli profil ile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📊 Log Formatı

```
10:30:15.123 [main] INFO  o.k.d.d.decision.DecisionService:42 - Decision created successfully
```

---

**Son Güncelleme**: 2026-04-24
