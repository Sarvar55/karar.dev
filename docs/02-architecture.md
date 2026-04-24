# Mimari Yapı ve Prensipler

## 🏗️ Proje Yapısı

```
org.karar.dev
├── common
│   ├── entity          # Temel entity sınıfları
│   ├── enums           # Enum tanımlamaları
│   └── exception       # Exception handling
│       ├── base        # Temel exception sınıfları
│       ├── conflict    # Conflict (409) exceptions
│       ├── dto         # Error response DTO
│       ├── handler     # Global exception handler
│       ├── notFound    # Not found (404) exceptions
│       └── validation  # Validation exceptions
├── config              # Konfigürasyon sınıfları
├── domain              # Domain modülleri
│   ├── auth            # Kimlik doğrulama
│   ├── base            # BaseResponse ve yardımcı sınıflar
│   ├── comment         # Yorum sistemi
│   ├── decision        # Karar yönetimi
│   ├── decisiontag     # Karar-etiket ilişkisi
│   ├── tag             # Etiket yönetimi
│   ├── user            # Kullanıcı yönetimi
│   │   ├── company     # Şirket kullanıcıları
│   │   └── regular     # Bireysel kullanıcılar
│   └── vote            # Oy sistemi
└── Application.java    # Spring Boot başlangıç sınıfı
```

---

## 🎯 Architectural Prensipler

### 1. Repository'ler Birbirini Kullanmamalıdır ❌

**Yanlış Yaklaşım:**
```java
// ❌ Repository'ler birbirini kullanmamalı!
@Service
public class DecisionService {
    private final DecisionRepository decisionRepository;
    private final TagRepository tagRepository;  // ❌ Başka servisin repository'si!
    private final UserRepository userRepository;  // ❌ Başka servisin repository'si!
}
```

**Doğru Yaklaşım:**
```java
// ✅ Servisler birbiriyle haberleşmeli
@Service
public class DecisionService {
    private final DecisionRepository decisionRepository;
    private final DecisionTagRepository decisionTagRepository;
    private final TagService tagService;  // ✅ Servis üzerinden erişim
    private final UserService userService;  // ✅ Servis üzerinden erişim
}
```

### 2. Servisler Birbiriyle Haberleşmelidir ✅

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Servis A   │────▶│  Servis B   │────▶│  Servis C   │
│  (Repo A)   │◄────│  (Repo B)   │◄────│  (Repo C)   │
└─────────────┘     └─────────────┘     └─────────────┘
```

### 3. Cross-Cutting Concerns

| Katman | Sorumluluk |
|--------|------------|
| **Controller** | HTTP isteklerini karşıla, response formatı |
| **Service** | Business logic, transaction boundary |
| **Repository** | Veritabanı erişimi, sadece kendi entity'si |

### 4. Dependency Flow

```
Controller → Service → Repository
                ↓
           Other Services (NOT Repositories!)
```

---

## 📝 Kurallar

### Repository Kuralları

1. **Bir repository sadece kendi entity'sini yönetir**
2. **Repository'ler birbirini inject etmez**
3. **Repository'lerde business logic olmaz**

### Service Kuralları

1. **Servisler birbirini inject edebilir**
2. **Bir servis kendi repository'sini ve diğer servisleri kullanır**
3. **Transaction boundary servis katmanında yönetilir**

### Örnek Doğru Kullanım

```java
@Service
@RequiredArgsConstructor
public class DecisionService {
    private final DecisionRepository decisionRepository;
    private final DecisionTagRepository decisionTagRepository;
    
    // ✅ Diğer servisler inject edilir
    private final TagService tagService;
    private final UserService userService;
    
    @Transactional
    public DecisionResponse createDecision(DecisionRequest request) {
        // ✅ User servisi üzerinden kullanıcı doğrulanır
        User user = userService.findById(request.userId());
        
        // ✅ Tag servisi üzerinden tagler doğrulanır
        Set<Tag> tags = tagService.findAllById(request.tagIds());
        
        // ...
    }
}
```

---

**Son Güncelleme**: 2026-04-24
