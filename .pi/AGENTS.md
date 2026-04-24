# karar.dev - Project Context

## Project Overview
Spring Boot 4.0.3 REST API application with H2 database and Spring Data JPA.

## Technology Stack
- **Language:** Java 17
- **Framework:** Spring Boot 4.0.3
- **Build Tool:** Maven
- **Database:** H2 (in-memory)
- **ORM:** Spring Data JPA
- **Validation:** Jakarta Validation
- **API Documentation:** SpringDoc OpenAPI UI
- **Utilities:** Lombok

## Build Commands
```bash
./mvnw clean package   # Build project
./mvnw test            # Run tests
./mvnw spring-boot:run # Run application
./mvnw clean           # Clean build artifacts
```

## Project Structure
```
src/
├── main/java/org/karar/dev/
│   ├── Application.java              # Entry point
│   ├── common/
│   │   ├── entity/BaseEntity.java
│   │   ├── enums/Role.java, RegretLevel.java
│   │   └── exception/
│   ├── config/OpenApiConfig.java
│   └── domain/
│       ├── auth/                     # Authentication module
│       ├── base/BaseResponse.java    # Unified API response
│       ├── comment/                  # Comment CRUD module
│       ├── decision/                 # Decision CRUD module (with Tag support)
│       ├── decisiontag/              # Decision-Tag Many-to-Many junction
│       ├── tag/                      # Tag CRUD module
│       ├── user/                     # User management
│       │   ├── regular/              # Individual users
│       │   └── company/              # Corporate users
│       └── vote/                     # Vote entity
├── test/java/org/karar/dev/
│   └── ApplicationTests.java
└── resources/
```

## Key Features
- RESTful API with Spring MVC
- H2 Console enabled for development
- Bean Validation
- OpenAPI/Swagger documentation at `/swagger-ui.html`
- Devtools for hot reload
- Centralized exception handling with GlobalExceptionHandler
- Consistent BaseResponse wrapper for all API responses

## Implemented Modules (CRUD Complete)
| Module | Status | Endpoints |
|--------|--------|-----------|
| Auth | ✅ | POST /api/v1/auth/register |
| User | ✅ | GET, PUT, DELETE /api/v1/users |
| Decision | ✅ | GET, POST, PUT, DELETE /api/v1/decisions |
| Tag | ✅ | GET, POST, PUT, DELETE /api/v1/tags |
| Comment | ✅ | GET, POST, PUT, DELETE /api/v1/comments |
| Vote | ✅ | GET, POST, DELETE /api/v1/votes |

## Code Conventions
- Package: `org.karar.dev`
- Use Lombok for boilerplate reduction
- JPA entities in `domain` package
- Controllers must use BaseResponse<T> for all responses
- All endpoints must have Swagger/OpenAPI annotations
- Use constructor injection with @RequiredArgsConstructor
- Repository methods: findByX, findAll, existsByX, save, deleteById
