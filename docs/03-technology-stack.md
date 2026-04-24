# Teknoloji Stack'i

## 🛠️ Kullanılan Teknolojiler

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

## 📦 Bağımlılıklar (pom.xml)

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

**Son Güncelleme**: 2026-04-24
