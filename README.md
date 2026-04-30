# 🎯 Karar.dev API

> **Karar** (Turkish for "Decision") — A social platform for sharing decisions, regrets, and community feedback.

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

---

## 📖 Quick Links

| Resource | Link |
|----------|------|
| 📘 **Full Documentation** | [docs/README.md](docs/README.md) |
| 🎯 **Project Overview** | [docs/project-idea.md](docs/project-idea.md) |
| 🔌 **API Endpoints** | [docs/07-api-endpoints.md](docs/07-api-endpoints.md) |
| 🏗️ **Architecture** | [docs/02-architecture.md](docs/02-architecture.md) |
| 🗄️ **Database Schema** | [docs/04-database-schema.md](docs/04-database-schema.md) |
| 📝 **Changelog** | [docs/10-changelog.md](docs/10-changelog.md) |
| 🌐 **Swagger UI** | http://localhost:8080/swagger-ui.html |

---

## 🎯 Overview

**Karar.dev** is a social platform where users can:

- 📝 **Share Decisions** — Post decisions with reasoning and alternatives
- 😔 **Track Regret** — Indicate regret levels (LOW, MEDIUM, HIGH)
- 👍 **Vote** — Vote on other users' decisions
- 💬 **Comment** — Provide feedback and comments
- 🏷️ **Categorize** — Use tags for organization
- 🔍 **Filter** — Search by user, tag, regret level

### ✨ Key Features

| Feature | Description |
|---------|-------------|
| **User Management** | Regular & Company users with role-based access |
| **Decision CRUD** | Full create, read, update, delete operations |
| **Voting System** | One vote per user per decision, with statistics |
| **Comment System** | Threaded comments with filtering |
| **Tag System** | Categorization with many-to-many relationships |
| **RESTful API** | Consistent, well-documented endpoints |
| **Exception Handling** | Centralized error management |
| **Swagger Docs** | Interactive API documentation |

---

## 🛠️ Technology Stack

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Language** | Java | 17 LTS | Backend development |
| **Framework** | Spring Boot | 4.0.3 | Application framework |
| **Web** | Spring MVC | 4.0.3 | RESTful API |
| **Data** | Spring Data JPA | 4.0.3 | ORM & data access |
| **Validation** | Hibernate Validator | 8.x | Input validation |
| **Database (Dev)** | H2 | 2.x | Development database |
| **Database (Prod)** | PostgreSQL | 15.x | Production database |
| **Documentation** | SpringDoc OpenAPI | 3.0.2 | Swagger UI |
| **Build Tool** | Maven | 3.9.x | Dependency management |
| **Utilities** | Lombok | 1.18.x | Boilerplate reduction |

**Why These Technologies?**

- **Java 17**: LTS version, modern features (records, sealed classes), strong type safety
- **Spring Boot**: Rapid development, convention over configuration, production-ready
- **JPA/Hibernate**: Object-relational mapping, lazy loading, caching support
- **PostgreSQL**: ACID compliant, JSONB support, high performance

---

## 📚 Comprehensive Documentation

For detailed information, visit our **[Documentation Hub](docs/README.md)**:

### 🎯 Getting Started

1. **[Project Idea](docs/project-idea.md)** — What is Karar.dev, why it exists
2. **[Overview](docs/01-overview.md)** — Project features and scope
3. **[Technology Stack](docs/03-technology-stack.md)** — Tech choices and reasons
4. **[Development Guide](docs/09-development.md)** — Setup and best practices

### 🏗️ Architecture & Design

1. **[Architecture](docs/02-architecture.md)** — Layered architecture, principles
2. **[Database Schema](docs/04-database-schema.md)** — ERD, entity relationships
3. **[Entities](docs/project-idea.md#entities--ilişkiler)** — Detailed entity descriptions

### 🔌 API Reference

1. **[API Endpoints](docs/07-api-endpoints.md)** — Complete endpoint reference
2. **[DTOs](docs/08-dtos.md)** — Request/Response DTO structures
3. **[BaseResponse](docs/05-baseresponse.md)** — Standard response format
4. **[Exception Handling](docs/06-exception-handling.md)** — Error management

### 📝 Additional Resources

1. **[Changelog](docs/10-changelog.md)** — Version history and changes
2. **[Swagger Guide](docs/11-swagger.md)** — API testing with Swagger
3. **[Modules](docs/12-modules.md)** — Domain module structure

### User Management
- Support for Regular (individual) and Company users
- Role-based registration (USER, COMPANY)
- Email uniqueness validation

### Decision Management
- CRUD operations for decisions
- Regret level tracking (LOW, MEDIUM, HIGH)
- Vote counting
- Filtering by regret level and user
- Title uniqueness per user

### Tag Management
- Tag creation and management
- Case-insensitive tag names
- Automatic association with decisions
- Usage count tracking

### Comment Management
- Full CRUD operations for comments
- Filter comments by decision or user
- Content validation
- Automatic user and decision association

### Vote Management
- Cast votes on decisions (one vote per user per decision)
- Vote statistics endpoint (count + user vote status)
- Check if user has voted on a decision
- "Unvote" functionality (remove vote)
- Automatic vote count updates on decisions
- Duplicate vote prevention

### Consistent API Response
All API responses follow a unified structure using `BaseResponse<T>`:

```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "2025-01-20T10:30:00",
  "status": "OK"
}
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17
- Maven

### Running the Application

```bash
# Clone the repository
git clone <repository-url>
cd karar.dev

# Run with Maven
./mvnw spring-boot:run

# Or with Java
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### API Documentation

Once the application is running, access:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

---

## 🔌 API Endpoints Summary

### Base URL

```
http://localhost:8080/api/v1
```

### Authentication

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/auth/register` | Register new user/company | ❌ Public |

### Decisions (`/decisions`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/decisions` | List all (query: `userId`, `regretLevel`, `tagId`) | ❌ |
| GET | `/decisions/{id}` | Get decision by ID | ❌ |
| GET | `/decisions/{decisionId}/comments` | Get decision's comments | ❌ |
| GET | `/decisions/{decisionId}/tags` | Get decision's tags | ❌ |
| POST | `/decisions` | Create decision | ✅ |
| PUT | `/decisions/{id}` | Update decision | ✅ |
| DELETE | `/decisions/{id}` | Delete decision | ✅ |

### Comments (`/comments`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/comments` | List all (query: `decisionId`, `userId`) | ❌ |
| GET | `/comments/{id}` | Get comment by ID | ❌ |
| POST | `/comments` | Create comment | ✅ |
| PUT | `/comments/{id}` | Update comment | ✅ |
| DELETE | `/comments/{id}` | Delete comment | ✅ |

### Votes (`/votes`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/votes` | List all (query: `decisionId`, `userId`) | ❌ |
| GET | `/votes/{id}` | Get vote by ID | ❌ |
| GET | `/votes/decisions/{decisionId}/count` | Vote count + user status | ❌ |
| POST | `/votes` | Cast a vote | ✅ |
| DELETE | `/votes/{id}` | Delete vote by ID | ✅ |
| DELETE | `/votes/users/{userId}/decisions/{decisionId}` | Unvote | ✅ |

### Tags (`/tags`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/tags` | List all tags | ❌ |
| GET | `/tags/{id}` | Get tag by ID | ❌ |
| GET | `/tags/name/{name}` | Get tag by name | ❌ |
| POST | `/tags` | Create tag | ✅ |
| PUT | `/tags/{id}` | Update tag | ✅ |
| DELETE | `/tags/{id}` | Delete tag | ✅ |

### Users (`/users`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/users` | List all regular users | ❌ |
| GET | `/users/{id}` | Get user by ID | ❌ |
| GET | `/users/{userId}/comments` | Get user's comments | ❌ |
| PUT | `/users/{id}` | Update user | ✅ |
| DELETE | `/users/{id}` | Delete user | ✅ |

### Companies (`/companies`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/companies` | List all companies | ❌ |
| GET | `/companies/{id}` | Get company by ID | ❌ |
| PUT | `/companies/{id}` | Update company | ✅ |
| DELETE | `/companies/{id}` | Delete company | ✅ |

📖 **Full API documentation**: [docs/07-api-endpoints.md](docs/07-api-endpoints.md)

## Project Structure

```
org.karar.dev/
├── common/
│   ├── entity/         # Base entity classes
│   ├── enums/          # Enums (Role, RegretLevel)
│   └── exception/      # Exception handling
│       ├── base/       # BaseException
│       ├── conflict/   # Conflict exceptions (409)
│       ├── handler/    # GlobalExceptionHandler
│       ├── notFound/   # Not found exceptions (404)
│       └── validation/ # Validation exceptions (400)
├── config/             # Configuration classes
└── domain/
    ├── auth/           # Authentication module
    ├── base/           # BaseResponse
    ├── comment/        # Comment system
    │   ├── dto/        # CommentRequest, CommentResponse
    │   ├── CommentController.java
    │   ├── CommentService.java
    │   └── CommentRepository.java
    ├── decision/       # Decision management
    │   ├── dto/        # DecisionRequest, DecisionResponse
    │   ├── DecisionController.java
    │   ├── DecisionService.java
    │   └── DecisionRepository.java
    ├── decisiontag/    # Decision-Tag relationship
    ├── tag/            # Tag management
    │   ├── dto/        # TagRequest, TagResponse
    │   ├── TagController.java
    │   ├── TagService.java
    │   └── TagRepository.java
    ├── user/           # User management
    │   ├── company/    # Company users
    │   └── regular/    # Regular users
    └── vote/           # Voting system
        ├── dto/        # VoteRequest, VoteResponse
        ├── VoteController.java
        ├── VoteService.java
        └── VoteRepository.java
```

---

## 🗄️ Database Schema

### Entity Relationships

```
┌─────────────┐     ┌─────────────┐
│RegularUser  │     │CompanyUser  │
└──────┬──────┘     └─────────────┘
       │
       │ 1:N
       ▼
┌─────────────┐     ┌─────────────┐
│  Decision   │◄────│    Vote     │
└──────┬──────┘     └─────────────┘
       │
   ┌───┼───┬────────────┐
   │   │   │            │
   ▼   ▼   ▼            ▼
┌─────┐ │ ┌─────┐  ┌──────────┐
│Comment│ │ │Tag  │  │DecisionTag│
└─────┘ │ └──┬──┘  └──────────┘
        │    │
        └────┘ (N:M)
```

### Key Entities

| Entity | Description |
|--------|-------------|
| **User** | Abstract base entity for authentication |
| **RegularUser** | Individual users with username |
| **CompanyUser** | Corporate users with company name |
| **Decision** | User decisions with title, reasoning, regret level |
| **Tag** | Categorization labels (unique names) |
| **DecisionTag** | Junction table for Decision-Tag N:M relationship |
| **Vote** | User votes on decisions (unique: user + decision) |
| **Comment** | Comments on decisions |

📖 **Full schema**: [docs/04-database-schema.md](docs/04-database-schema.md) | [docs/project-idea.md](docs/project-idea.md#entities--ilişkiler)

## Exception Handling

All exceptions are handled centrally by `GlobalExceptionHandler`:

| Exception | HTTP Status | Description |
|-------------|-------------|-------------|
| ResourceNotFoundException | 404 | Entity not found |
| ConflictException | 409 | Duplicate or conflict |
| ValidationException | 400 | Validation failed |
| Generic Exception | 500 | Internal server error |

Response format:
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

## Configuration

### Application Properties

```yaml
spring:
  application:
    name: karar.dev
  datasource:
    url: jdbc:h2:mem:test
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080
```

### Profiles

- `local` (default): H2 in-memory database
- `dev`: Development environment
- `prod`: Production environment

## Documentation

For detailed API documentation, see:
- [docs/api-documentation.md](docs/api-documentation.md)

---

## 📝 Recent Changes

### v1.0.0 — REST API Redesign (2026-04-26) 🎉

**Major Changes**:
- ✅ **REST Design Principles Applied** — Filtering via query params, nested resources
- ✅ **Endpoint Consolidation** — Removed duplicate endpoints, unified filtering
- ✅ **Service Composition** — Controllers call appropriate services for related resources
- ✅ **Comprehensive Documentation** — New project-idea.md, updated API docs

**Breaking Changes**:
```bash
# Old (Removed)
GET /api/v1/decisions/users/{userId}
GET /api/v1/decisions/regret-levels/{level}
GET /api/v1/decisions/tags/{tagId}
GET /api/v1/comments/decision/{decisionId}
GET /api/v1/comments/user/{userId}

# New (Query Params)
GET /api/v1/decisions?userId={id}
GET /api/v1/decisions?regretLevel={level}
GET /api/v1/decisions?tagId={id}
GET /api/v1/comments?decisionId={id}
GET /api/v1/comments?userId={id}
```

**New Features**:
- ✅ `GET /api/v1/decisions/{decisionId}/tags` — Get decision's tags
- ✅ `GET /api/v1/comments?decisionId={id}&userId={id}` — Combined filtering

📖 **Full changelog**: [docs/10-changelog.md](docs/10-changelog.md)

## Future Enhancements

- JWT-based authentication and authorization
- Pagination and sorting for list endpoints
- File upload for decision images
- Advanced search with filters
- Email notifications
- Rate limiting
- Soft delete implementation

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow [SYSTEM.md](.pi/SYSTEM.md) engineering guidelines
- Write unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages

📖 **Development guide**: [docs/09-development.md](docs/09-development.md)

---

## 📞 Contact

| Resource | Link |
|----------|------|
| **Website** | https://karar.dev |
| **Email** | contact@karar.dev |
| **GitHub** | https://github.com/karar-dev/karar.dev |
| **Documentation** | [docs/README.md](docs/README.md) |

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

---

**Built with ❤️ using Spring Boot**

© 2026 Karar.dev Team
