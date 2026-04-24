# BaseResponse Yapısı

## 📦 BaseResponse<T>

Tüm API response'ları tutarlı bir yapıda `BaseResponse<T>` sınıfı ile döndürülür.

## Yapı

```java
{
    "success": true/false,              // İşlem başarılı mı?
    "data": T,                          // Response data (null olabilir)
    "error": {                          // Hata durumunda dolu olur
        "code": "ExceptionClassName",   // Hata kodu
        "message": "Error message",     // Hata mesajı
        "details": { ... },             // Ek detaylar (opsiyonel)
        "validationErrors": {           // Validasyon hataları
            "field1": "error message",
            "field2": "error message"
        }
    },
    "timestamp": "2025-01-20T10:30:00", // ISO 8601 format
    "status": "OK/CREATED/BAD_REQUEST/..." // HTTP status
}
```

## Factory Metodları

| Metod | Açıklama | HTTP Status |
|-------|----------|-------------|
| `BaseResponse.success(T data)` | Başarılı response | 200 OK |
| `BaseResponse.success(T data, HttpStatus status)` | Özel status ile başarılı | Belirtilen status |
| `BaseResponse.error(BaseException ex, HttpStatus status)` | Exception'dan error | Belirtilen status |
| `BaseResponse.error(String code, String message, HttpStatus status)` | Manuel error | Belirtilen status |
| `BaseResponse.validationError(String message, Map<String,String> errors)` | Validasyon error | 400 BAD_REQUEST |

## Örnek Response'lar

### Başarılı (200 OK)

```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Should I learn Spring Boot?",
    "regretLevel": "LOW",
    "voteCount": 42
  },
  "timestamp": "2025-01-20T10:30:00",
  "status": "OK"
}
```

### Validasyon Hatası (400 BAD_REQUEST)

```json
{
  "success": false,
  "error": {
    "code": "ValidationError",
    "message": "Validation failed",
    "validationErrors": {
      "title": "Title is required",
      "email": "Email format is invalid"
    }
  },
  "timestamp": "2025-01-20T10:30:00",
  "status": "BAD_REQUEST"
}
```

### Kaynak Bulunamadı (404 NOT_FOUND)

```json
{
  "success": false,
  "error": {
    "code": "ResourceNotFoundException",
    "message": "Decision not found with id : 'xxx'",
    "validationErrors": null
  },
  "timestamp": "2025-01-20T10:30:00",
  "status": "NOT_FOUND"
}
```

---

**Dosya**: `src/main/java/org/karar/dev/domain/base/BaseResponse.java`  
**Son Güncelleme**: 2026-04-24
