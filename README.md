# Karar.dev API

A REST API for sharing decisions and regrets. Users can post decisions they've made, rate their regret level, vote on others' decisions, and categorize them with tags.

## Overview

Karar.dev ("Karar" means "Decision" in Turkish) is a social platform where users can:
- Share decisions they've made and their outcomes
- Indicate regret levels (LOW, MEDIUM, HIGH)
- Vote on other users' decisions
- Comment on decisions
- Categorize decisions with tags

## Technology Stack

- **Java 17**
- **Spring Boot 4.0.3**
- **Spring Data JPA**
- **H2 Database** (development)
- **Lombok**
- **OpenAPI (Swagger) 3.0.2**
- **Maven**

## Features

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

## Getting Started

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

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user/company |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/users` | Get all regular users |
| GET | `/api/v1/users/{id}` | Get user by ID |
| PUT | `/api/v1/users/{id}` | Update user |
| DELETE | `/api/v1/users/{id}` | Delete user |

### Decisions
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/decisions` | Get all decisions |
| GET | `/api/v1/decisions/{id}` | Get decision by ID |
| GET | `/api/v1/decisions/user/{userId}` | Get user's decisions |
| GET | `/api/v1/decisions/regret-level/{level}` | Filter by regret level |
| POST | `/api/v1/decisions` | Create decision |
| PUT | `/api/v1/decisions/{id}` | Update decision |
| DELETE | `/api/v1/decisions/{id}` | Delete decision |

### Tags
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/tags` | Get all tags |
| GET | `/api/v1/tags/{id}` | Get tag by ID |
| GET | `/api/v1/tags/name/{name}` | Get tag by name |
| POST | `/api/v1/tags` | Create tag |
| PUT | `/api/v1/tags/{id}` | Update tag |
| DELETE | `/api/v1/tags/{id}` | Delete tag |

### Comments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/comments` | Get all comments |
| GET | `/api/v1/comments/{id}` | Get comment by ID |
| GET | `/api/v1/comments/decision/{decisionId}` | Get decision's comments |
| GET | `/api/v1/comments/user/{userId}` | Get user's comments |
| POST | `/api/v1/comments` | Create comment |
| PUT | `/api/v1/comments/{id}` | Update comment |
| DELETE | `/api/v1/comments/{id}` | Delete comment |

### Votes
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/votes` | Get all votes |
| GET | `/api/v1/votes/{id}` | Get vote by ID |
| GET | `/api/v1/votes/decision/{decisionId}` | Get decision's votes |
| GET | `/api/v1/votes/user/{userId}` | Get user's votes |
| GET | `/api/v1/votes/decision/{decisionId}/count` | Get vote count & user status |
| GET | `/api/v1/votes/check` | Check if user has voted |
| POST | `/api/v1/votes` | Cast a vote |
| DELETE | `/api/v1/votes/{id}` | Delete vote by ID |
| DELETE | `/api/v1/votes` | Delete vote (unvote by user & decision) |

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

## Database Schema

### Entity Relationships

```
RegularUser 1:N Decision
Decision 1:N Comment
Decision 1:N Vote
Decision N:M Tag (via DecisionTag)
```

### Key Entities

- **User**: Base entity for authentication
- **RegularUser**: Individual users with username
- **CompanyUser**: Corporate users with company name
- **Decision**: User decisions with title, reasoning, regret level
- **Tag**: Categorization labels
- **Vote**: User votes on decisions
- **Comment**: Comments on decisions

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

## Recent Changes

### Version 0.0.1-SNAPSHOT

- ✅ Improved BaseResponse structure with error data support
- ✅ Integrated BaseResponse into Auth module
- ✅ Added complete CRUD for Decision module
- ✅ Added complete CRUD for Tag module
- ✅ Added complete CRUD for Comment module
- ✅ Added complete CRUD for Vote module (with automatic vote count updates)
- ✅ **Fixed Decision-Tag relationship** (now properly creates associations via DecisionTag junction table)
- ✅ Fixed entity mappings and timestamp persistence
- ✅ Added comprehensive API documentation

## Future Enhancements

- JWT-based authentication and authorization
- Pagination and sorting for list endpoints
- File upload for decision images
- Advanced search with filters
- Email notifications
- Rate limiting
- Soft delete implementation

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Contact

- **Email**: contact@karar.dev
- **Project**: https://github.com/karar-dev/karar.dev

---

Built with ❤️ using Spring Boot
