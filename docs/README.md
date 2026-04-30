# 📚 Karar.dev — Dokümantasyon Rehberi

> **Son Güncelleme**: 2026-04-26  
> **Dokümantasyon Versiyonu**: 1.0.0

---

## 🎯 Bu Dokümantasyonu Kimler Kullanmalı?

| Rol | Önerilen Okuma Sırası |
|-----|----------------------|
| **Yeni Geliştirici** | project-idea → 01-overview → 03-tech-stack → 09-development |
| **Backend Developer** | 02-architecture → 07-api-endpoints → 08-dtos → 06-exception-handling |
| **API Consumer** | 07-api-endpoints → 11-swagger → 05-baseresponse |
| **Database Developer** | 04-database-schema → 02-architecture |
| **Tech Lead / Architect** | project-idea → 02-architecture → 13-service-refactoring → 14-roadmap |

---

## 📖 Dokümantasyon Haritası

```
docs/
│
├── 📋 README.md (bu dosya)
│   └── Dokümantasyon rehberi ve okuma sırası
│
├── 🎯 project-idea.md
│   ├── Proje nedir, neden var?
│   ├── Tech stack detayları
│   ├── Functional & Non-functional requirements
│   ├── Prensipler ve kurallar
│   ├── Services & endpoints özeti
│   ├── Entities ve ilişkiler (ERD)
│   └── Database schema (SQL)
│
├── 📖 Temel Dokümantasyon (01-10)
│   │
│   ├── 01-overview.md
│   │   └── Proje genel bakış ve özellikler
│   │
│   ├── 02-architecture.md
│   │   ├── Proje yapısı (package structure)
│   │   ├── Mimari prensipler
│   │   ├── Repository/Service kuralları
│   │   └── Dependency flow
│   │
│   ├── 03-technology-stack.md
│   │   ├── Kullanılan teknolojiler
│   │   ├── Bağımlılıklar (pom.xml)
│   │   └── Teknoloji seçim nedenleri
│   │
│   ├── 04-database-schema.md
│   │   ├── Entity İlişki Diyagramı (ERD)
│   │   ├── Entity açıklamaları
│   │   └── SQL schema
│   │
│   ├── 05-baseresponse.md
│   │   ├── BaseResponse yapısı
│   │   ├── Response format standardı
│   │   └── Factory metodları
│   │
│   ├── 06-exception-handling.md
│   │   ├── Global exception handler
│   │   ├── Exception tipleri
│   │   ├── Error response format
│   │   └── HTTP status codes
│   │
│   ├── 07-api-endpoints.md
│   │   ├── REST design principles
│   │   ├── Tüm endpoints (detaylı)
│   │   ├── Request/Response örnekleri
│   │   ├── Authentication usage
│   │   └── Pagination examples
│   │
│   ├── 08-dtos.md
│   │   ├── DTO pattern açıklaması
│   │   ├── Request DTOs (records)
│   │   ├── Response DTOs (immutable)
│   │   └── DTO kuralları
│   │
│   ├── 09-development.md
│   │   ├── Development setup
│   │   ├── Coding standards
│   │   ├── Git workflow
│   │   └── Best practices
│   │
│   └── 10-changelog.md
│       ├── Versiyon geçmişi
│       ├── Yapılan değişiklikler
│       └── Breaking changes
│
├── 🔧 İleri Seviye Dokümantasyon (11-13)
│   │
│   ├── 11-swagger.md
│   │   ├── Swagger UI kullanımı
│   │   ├── OpenAPI annotations
│   │   └── API testing
│   │
│   ├── 12-modules.md
│   │   ├── Domain modülleri
│   │   ├── Module responsibilities
│   │   └── Cross-module communication
│   │
│   └── 13-service-refactoring-analysis.md
│       ├── Service analiz raporu
│       ├── Refactoring önerileri
│       └── Performance considerations
│
├── 🗺️ Yol Haritası (14)
│   │
│   └── 14-roadmap.md
│       ├── Teknoloji yol haritası (3 phase)
│       ├── JWT, Flyway, Docker, Kafka, Redis...
│       ├── Dependency ve kod örnekleri
│       └── Hedef mimari diyagramı
│
└── 📄 Diğer Dosyalar
    │
    ├── concurrency_management.md
    │   └── Concurrency handling ve thread safety
    │
    └── README.md
        └── Bu dosya
```

---

## 🚀 Hızlı Başlangıç

### İlk Kez Mi Başlıyorsunuz?

```
1. project-idea.md        → Projeyi anlayın
2. 01-overview.md         → Genel bakış alın
3. 03-technology-stack.md → Teknolojileri öğrenin
4. 09-development.md      → Development setup yapın
```

### API Geliştiriyor Musunuz?

```
1. 02-architecture.md     → Mimariyi anlayın
2. 07-api-endpoints.md    → Endpoint'leri inceleyin
3. 08-dtos.md             → DTO yapılarını öğrenin
4. 06-exception-handling.md → Error handling'i kavrayın
```

### Veritabanı ile Mi Çalışıyorsunuz?

```
1. 04-database-schema.md  → Schema'yı inceleyin
2. 02-architecture.md     → Entity ilişkilerini anlayın
3. project-idea.md        → Business domain'i öğrenin
```

---

## 📊 Dokümantasyon Özeti

### Temel Kavramlar

| Doküman | Okuma Süresi | Zorluk | Öncelik |
|---------|--------------|--------|---------|
| project-idea.md | 15 dk | ⭐⭐ | 🔴 Yüksek |
| 01-overview.md | 5 dk | ⭐ | 🔴 Yüksek |
| 02-architecture.md | 10 dk | ⭐⭐⭐ | 🔴 Yüksek |
| 03-technology-stack.md | 5 dk | ⭐ | 🟡 Orta |
| 04-database-schema.md | 10 dk | ⭐⭐⭐ | 🔴 Yüksek |
| 05-baseresponse.md | 5 dk | ⭐ | 🟡 Orta |
| 06-exception-handling.md | 10 dk | ⭐⭐ | 🟡 Orta |
| 07-api-endpoints.md | 20 dk | ⭐⭐ | 🔴 Yüksek |
| 08-dtos.md | 10 dk | ⭐⭐ | 🟡 Orta |
| 09-development.md | 10 dk | ⭐ | 🟢 Düşük |
| 10-changelog.md | 10 dk | ⭐ | 🟢 Düşük |
| 14-roadmap.md | 15 dk | ⭐⭐ | 🟡 Orta |

**Toplam Okuma Süresi**: ~2 saat

---

## 🎓 Öğrenme Yolları

### 🟢 Beginner Path (Yeni Başlayanlar)

```
Day 1:
  ├─ project-idea.md (Proje nedir?)
  ├─ 01-overview.md (Özellikler)
  └─ 03-technology-stack.md (Teknolojiler)

Day 2:
  ├─ 02-architecture.md (Mimari)
  ├─ 04-database-schema.md (Veritabanı)
  └─ 09-development.md (Setup)

Day 3:
  ├─ 07-api-endpoints.md (API'ler)
  ├─ 08-dtos.md (DTO'lar)
  └─ 05-baseresponse.md (Response format)

Day 4:
  ├─ 06-exception-handling.md (Hata yönetimi)
  ├─ 11-swagger.md (API testing)
  └─ Pratik: İlk endpoint'i yaz
```

### 🟡 Intermediate Path (Orta Seviye)

```
Day 1:
  ├─ 02-architecture.md (Derinlemesine mimari)
  ├─ 12-modules.md (Modül yapısı)
  └─ 13-service-refactoring.md (Refactoring)

Day 2:
  ├─ 07-api-endpoints.md (REST design)
  ├─ project-idea.md (Prensipler)
  └─ Pratik: Yeni feature ekle

Day 3:
  ├─ 04-database-schema.md (Performance)
  ├─ concurrency_management.md (Concurrency)
  └─ Pratik: Query optimization
```

### 🔴 Advanced Path (İleri Seviye)

```
Day 1:
  ├─ 13-service-refactoring-analysis.md
  ├─ 02-architecture.md (Scalability)
  └─ project-idea.md (NFR'ler)

Day 2:
  ├─ 10-changelog.md (Evolution)
  ├─ Concurrency patterns
  └─ Pratik: System design

Day 3:
  ├─ Code review
  ├─ Performance tuning
  └─ Documentation contribution
```

---

## 🔍 Hızlı Referans

### Endpoint Arama

```bash
# Karar ile ilgili endpoint'ler
grep -r "decisions" docs/07-api-endpoints.md

# Comment ile ilgili endpoint'ler
grep -r "comments" docs/07-api-endpoints.md

# Vote ile ilgili endpoint'ler
grep -r "votes" docs/07-api-endpoints.md
```

### Entity İlişkileri

```bash
# ERD görüntüleme
cat docs/04-database-schema.md | grep -A 50 "Entity İlişki Diyagramı"

# Entity detayları
cat docs/project-idea.md | grep -A 10 "Entity Detayları"
```

### Code Examples

```bash
# Request/Response örnekleri
cat docs/07-api-endpoints.md | grep -A 20 "Request Body"

# Error handling örnekleri
cat docs/06-exception-handling.md | grep -A 10 "Response"
```

---

## 📝 Dokümantasyon Standartları

### Dosya İsimlendirme

```
Format: {sıra-numarası}-{konu}.md

Örnekler:
  01-overview.md
  07-api-endpoints.md
  10-changelog.md
```

**Neden Sıra Numarası?**
- Okuma sırasını gösterir
- Kolay bulma sağlar
- Mantıksal gruplama yapar

### İçerik Yapısı

Her doküman şu yapıyı takip eder:

```markdown
# Başlık

> Son Güncelleme: YYYY-MM-DD
> Versiyon: X.X.X

---

## Bölüm 1

Açıklama...

## Bölüm 2

Tablolar, kod blokları, örnekler...

---

**Son Güncelleme**: YYYY-MM-DD
```

### Kod Blokları

````markdown
### Java

```java
@Service
public class DecisionService {
    // Code here
}
```

### Bash

```bash
curl -X GET http://localhost:8080/api/v1/decisions
```

### JSON

```json
{
  "success": true,
  "data": {}
}
```
````

---

## 🔄 Güncelleme Süreci

### Ne Zaman Güncellenir?

| Değişiklik Tipi | Dokümantasyon |
|-----------------|---------------|
| Yeni endpoint | 07-api-endpoints.md |
| Entity değişikliği | 04-database-schema.md, project-idea.md |
| Yeni feature | project-idea.md, 10-changelog.md |
| Breaking change | 10-changelog.md, 07-api-endpoints.md |
| Architecture change | 02-architecture.md, project-idea.md |

### Güncelleme Checklist

```markdown
- [ ] Değişikliği 10-changelog.md'e ekle
- [ ] İlgili dokümanları güncelle
- [ ] Version numarasını artır
- [ ] Son güncelleme tarihini değiştir
- [ ] README.md haritasını kontrol et
```

---

## 📞 Katkıda Bulunma

### Dokümantasyon Hatası Bildir

1. GitHub Issues aç
2. `[DOCS]` prefix'i kullan
3. Dosya adı ve satır numarası belirt

### Dokümantasyon Katkısı

1. Fork oluştur
2. Değişiklikleri yap
3. PR aç
4. Review sürecini bekle

---

## 🔗 Dış Kaynaklar

### Spring Boot

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)

### REST API Design

- [REST API Tutorial](https://restfulapi.net/)
- [Microsoft REST Guidelines](https://github.com/microsoft/api-guidelines)

### Java

- [Java 17 Docs](https://docs.oracle.com/en/java/javase/17/)
- [Effective Java](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)

---

## 📊 İstatistikler

| Metrik | Değer |
|--------|-------|
| Toplam Doküman | 15+ |
| Toplam Satır | 5000+ |
| Ortalama Okuma Süresi | 2 saat |
| Son Güncelleme | 2026-04-26 |
| Versiyon | 1.0.0 |

---

## ✅ Checklist — İlk Kez Okuyanlar İçin

```markdown
## Temel Kavramlar
- [ ] project-idea.md okundu
- [ ] 01-overview.md okundu
- [ ] 03-technology-stack.md okundu

## Mimari
- [ ] 02-architecture.md okundu
- [ ] 04-database-schema.md okundu

## API
- [ ] 07-api-endpoints.md okundu
- [ ] 05-baseresponse.md okundu
- [ ] 06-exception-handling.md okundu

## Development
- [ ] 09-development.md okundu
- [ ] 08-dtos.md okundu
- [ ] İlk endpoint yazıldı
```

---

## 🎯 Sonraki Adımlar

1. **Yeni Başlıyorsanız**: [project-idea.md](project-idea.md) ile başlayın
2. **API Geliştirecekseniz**: [07-api-endpoints.md](07-api-endpoints.md) inceleyin
3. **Veritabanı Çalışması**: [04-database-schema.md](04-database-schema.md) okuyun
4. **Genel Bakış**: [01-overview.md](01-overview.md) okuyun

---

**Happy Reading! 📖**

© 2026 Karar.dev Team
