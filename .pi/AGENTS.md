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
│   ├── Application.java          # Entry point
│   └── domain/
│       └── user/
│           └── User.java         # Sample entity
├── test/java/org/karar/dev/
│   └── ApplicationTests.java   # Unit tests
└── resources/
```

## Key Features
- RESTful API with Spring MVC
- H2 Console enabled for development
- Bean Validation
- OpenAPI/Swagger documentation at `/swagger-ui.html`
- Devtools for hot reload

## Code Conventions
- Package: `org.karar.dev`
- Use Lombok for boilerplate reduction
- JPA entities in `domain` package
- Controllers should have REST endpoints
