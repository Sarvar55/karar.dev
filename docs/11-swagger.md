# Swagger / OpenAPI Dokümantasyonu

## 📚 Swagger UI

### Konfigürasyon

**Dosya**: `src/main/java/org/karar/dev/config/OpenApiConfig.java`

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Karar Dev API")
            .version("1.0")
            .description("API documentation for Karar Dev Application")
            .contact(new Contact()
                .name("Karar Dev Team")
                .email("contact@karar.dev"))
            .license(new License()
                .name("Apache 2.0")));
}
```

### Swagger Anotasyonları

Tüm controller'larda şu anotasyonlar kullanılıyor:

| Anotasyon | Amaç |
|-----------|------|
| `@Tag(name="...", description="...")` | Controller grubu |
| `@Operation(summary="...", description="...")` | Endpoint açıklaması |
| `@ApiResponses(...)` | Olası response'lar |
| `@Parameter(description="...", required=true, example="...")` | Parametre açıklaması |
| `@Schema(description="...", example="...")` | DTO alan açıklaması |

## 🌐 Erişim

Uygulama başlatıldığında:

| URL | Açıklama |
|-----|----------|
| `http://localhost:8080/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/v3/api-docs` | OpenAPI JSON |

---

**Son Güncelleme**: 2026-04-24
