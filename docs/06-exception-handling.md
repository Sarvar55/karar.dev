# Exception Handling

## ⚠️ Exception Hiyerarşisi

```
RuntimeException
    └── BaseException (abstract)
        ├── ConflictException (409)
        │   └── EmailAlreadyExistsException
        ├── NotFoundException (404)
        │   └── ResourceNotFoundException
        └── ValidationException (400)
```

## GlobalExceptionHandler

Tüm exception'lar `@RestControllerAdvice` ile merkezi olarak yönetilir.

| Exception | Handler Metodu | Response |
|-----------|----------------|----------|
| `BaseException` | `handleBaseException` | BaseResponse.error(ex, status) |
| `MethodArgumentNotValidException` | `handleValidationException` | BaseResponse.validationError(...) |
| `Exception` (genel) | `handleGenericException` | BaseResponse.error(...) 500 |

## Exception Sınıfları

### BaseException
Abstract temel exception sınıfı. Tüm custom exception'lar bunu extend eder.

### ConflictException (409)
Kaynak çakışması durumları için.

**Alt Sınıflar:**
- `EmailAlreadyExistsException` - Email zaten kayıtlı

### NotFoundException (404)
Kaynak bulunamadı durumları için.

**Alt Sınıflar:**
- `ResourceNotFoundException` - Genel kaynak bulunamadı

### ValidationException (400)
Validasyon hataları için.

---

**Dosya**: `src/main/java/org/karar/dev/common/exception/handler/GlobalExceptionHandler.java`  
**Son Güncelleme**: 2026-04-24
