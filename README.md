# 🎯 Karar.dev

> **Karar** (Turkish for "Decision") — A social platform for sharing decisions, regrets, and community feedback.

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-React-black.svg)](https://nextjs.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

---

## 📖 What is Karar.dev and What Problem Does it Solve?

People make thousands of decisions daily. Over time, some decisions lead to success, while others lead to profound regret. However, there is no dedicated social platform to document these life choices, reflect on their outcomes, and share them with a broader community for feedback. 

**Karar.dev** is a social knowledge base for decision-making. It solves the problem of "isolated experiences" by allowing users to:
- **Document Decisions:** Post decisions with detailed reasoning, alternatives considered, and outcomes.
- **Track Regret Levels:** Categorize the emotional outcome of a decision (e.g., `LOW`, `MEDIUM`, `HIGH` regret).
- **Gain Community Insights:** Allow other users to vote, comment, and provide feedback on shared decisions.
- **Discover Patterns:** Use tags to find decisions on similar topics (e.g., career, finance, relationships) and learn from others' experiences before making similar choices.

Whether it is a company deciding on a new tech stack or an individual reflecting on a career change, Karar.dev provides the platform to analyze, share, and learn from past choices.

---

## 🛠️ Technology Stack

Karar.dev is a modern, full-stack application built using the following technologies:

### **Backend**
- **Java 17 LTS:** Modern Java features with strong type safety.
- **Spring Boot 4.0.3:** Core application framework for rapid development.
- **Spring Security:** Robust authentication and authorization (JWT & Opaque tokens).
- **Spring Data JPA / Hibernate:** Object-Relational Mapping (ORM) for data access.
- **Apache Kafka:** Event-driven architecture for asynchronous tasks like email verification.
- **Redis:** High-performance caching and ephemeral data storage.
- **Spring AI:** Integration with OpenAI-compatible LLMs.

### **Frontend**
- **Next.js & React:** Server-side rendered (SSR) React framework for a highly responsive UI (`karar-ui`).
- **TypeScript:** Typed JavaScript for improved developer experience and safety.
- **Tailwind CSS:** Utility-first CSS framework for rapid UI styling.

### **Database & Infrastructure**
- **PostgreSQL:** Primary relational database for production environments.
- **H2 Database:** In-memory database for rapid local development.
- **Mailpit:** Local email testing tool for the async verification workflows.
- **Maven:** Dependency and build management.

---

## 🏛️ Domain Models, Request/Response Models & Design Patterns

Karar.dev relies heavily on **Domain-Driven Design (DDD)** concepts. Below is a breakdown of the core domains, their Request/Response models, and the key design patterns applied.

### **Core Domains**
1. **User (Auth & Management):** Handles `RegularUser` (individuals) and `CompanyUser` (businesses).
2. **Decision:** The core entity representing a user's choice, its reasoning, and its regret level.
3. **Tag / DecisionTag:** A categorization engine allowing decisions to be grouped dynamically.
4. **Comment:** User-generated feedback on decisions.
5. **Vote:** A system to track community consensus on a decision.

### **Request / Response Models**
Instead of exposing raw database entities to the client, the application uses isolated Data Transfer Objects (DTOs) for incoming requests and outgoing responses (e.g., `DecisionRequest`, `DecisionResponse`, `RegisterRequest`). A unified `BaseResponse<T>` wraps all API responses to ensure a consistent contract with the frontend:
```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "2026-05-29T12:00:00",
  "status": "OK"
}
```

### **Applied Design Patterns**

- **DTO (Data Transfer Object) Pattern:** Segregates the REST API layer from the persistence layer, ensuring security and versioning flexibility.
- **Strategy & Template Method Patterns:** The `TokenStrategy` infrastructure (e.g., `AbstractJwtTokenStrategy`) uses the Template Method to define the skeleton of token generation/validation, allowing specific strategies (JWT vs. Opaque tokens) to implement the details.
- **Observer / Publish-Subscribe Pattern:** Used extensively in the asynchronous workflow. For example, upon user registration, Spring Security events trigger a Kafka producer (`EmailVerificationProducer`) which asynchronously handles sending verification emails.
- **Builder Pattern:** Provided via Lombok (`@Builder`), simplifying the instantiation of complex domain entities and DTOs.
- **MVC (Model-View-Controller) / Layered Architecture:** The codebase is strictly divided into `Controller` -> `Service` -> `Repository` layers to maintain a clean separation of concerns.

---

## 📂 Project Structure

The backend project (`karar.dev`) follows a **Domain-Based Directory Structure**, isolating features into self-contained modules.

```text
org.karar.dev/
├── common/              # Shared infrastructure
│   ├── entity/          # Abstract base entities (e.g., Auditable entities)
│   ├── enums/           # Global Enums (Role, RegretLevel)
│   ├── exception/       # Centralized error handling (GlobalExceptionHandler)
│   └── security/        # Security configurations, token strategies, custom auth checks
├── config/              # Application-wide configuration (Kafka, Redis, Swagger, Async)
└── domain/              # Feature modules (Domain-Driven Structure)
    ├── auth/            # Registration, Login, and Async Email Verification
    ├── comment/         # Decision comments (Controller, Service, Repository, DTOs)
    ├── decision/        # Core decision logic
    ├── tag/             # Categorization tags
    ├── user/            # User profile management (Regular vs Company)
    └── vote/            # Voting mechanics
```

---

## 🗄️ Database Relationships

The database is highly normalized to ensure data integrity and prevent duplication.

### **Entity Relationships Schema**

```text
┌─────────────┐     ┌─────────────┐
│RegularUser  │     │CompanyUser  │
└──────┬──────┘     └─────────────┘
       │ (Inherits from Base User)
       │ 1:N
       ▼
┌─────────────┐     ┌─────────────┐
│  Decision   │◄────│    Vote     │ (1 Vote per User per Decision)
└──────┬──────┘     └─────────────┘
       │
   ┌───┼───┬────────────┐
   │   │   │            │
   ▼   ▼   ▼            ▼
┌─────┐ │ ┌─────┐  ┌──────────┐
│Comment│ │ │Tag  │  │DecisionTag│
└─────┘ │ └──┬──┘  └──────────┘
        │    │
        └────┘ (N:M Relationship)
```

### **Relationship Details**
- **User ↔ Decision (1:N):** A user can author multiple decisions.
- **User ↔ Vote (1:N):** A user can cast multiple votes, but only **one vote per specific decision** (Composite Unique Constraint: `userId` + `decisionId`).
- **Decision ↔ Comment (1:N):** A decision can have multiple comments from various users.
- **Decision ↔ Tag (N:M):** A decision can have multiple tags, and a tag can belong to multiple decisions. This is resolved via the `DecisionTag` junction table.
- **Inheritance:** `RegularUser` and `CompanyUser` extend from a base `User` entity, utilizing JPA inheritance strategies for role-based capabilities.

---

## 🚀 Quick Start

### **1. Prerequisites**
- Java 17+
- Node.js 18+ (for frontend)
- Docker & Docker Compose (for Kafka, Redis, PostgreSQL, Mailpit)

### **2. Running the Backend**
```bash
cd karar.dev

# Start infrastructure dependencies
docker-compose up -d

# Run the Spring Boot application
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080/api/v1`.
Interactive Swagger API Docs: `http://localhost:8080/swagger-ui.html`

### **3. Running the Frontend**
```bash
cd karar-ui

# Install dependencies
npm install

# Start the Next.js development server
npm run dev
```
The application UI will be available at `http://localhost:3000`.
