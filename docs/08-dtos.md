# DTO'lar (Data Transfer Objects)

## 🔐 Auth DTO'ları

### RegisterRequest
```java
record RegisterRequest(
    @NotBlank String email,
    @NotBlank String password,
    @NotNull Role role,
    String username,        // USER rolü için zorunlu
    String companyName      // COMPANY rolü için zorunlu
)
```

### AuthResponse
```java
record AuthResponse(
    UUID id,
    String email,
    Role role,
    String accessToken,
    String refreshToken
)
```

## 🎯 Decision DTO'ları

### DecisionRequest (Create)
```java
record DecisionRequest(
    @NotBlank String title,
    @NotBlank String why,
    String alternative,
    @NotNull RegretLevel regretLevel,
    @NotNull UUID userId,
    Set<UUID> tagIds
)
```

### DecisionUpdateRequest
```java
record DecisionUpdateRequest(
    @NotBlank String title,
    @NotBlank String why,
    String alternative,
    @NotNull RegretLevel regretLevel,
    Set<UUID> tagIds
)
```

### DecisionResponse
```java
record DecisionResponse(
    UUID id,
    String title,
    String why,
    String alternative,
    RegretLevel regretLevel,
    int voteCount,
    UUID userId,
    String username,
    int commentCount,
    Set<String> tags,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
)
```

## 👤 User DTO'ları

### RegularUserResponse
```java
record RegularUserResponse(
    UUID id,
    String email,
    String username
)
```

### RegularUserUpdateRequest
```java
record RegularUserUpdateRequest(
    @NotBlank @Email String email,
    @NotBlank String username
)
```

### CompanyUserResponse
```java
record CompanyUserResponse(
    UUID id,
    String email,
    String companyName
)
```

### CompanyUserUpdateRequest
```java
record CompanyUserUpdateRequest(
    @NotBlank @Email String email,
    @NotBlank String companyName
)
```

## 💬 Comment DTO'ları

### CommentRequest (Create)
```java
record CommentRequest(
    @NotBlank String content,
    @NotNull UUID userId,
    @NotNull UUID decisionId
)
```

### CommentUpdateRequest
```java
record CommentUpdateRequest(
    @NotBlank String content
)
```

### CommentResponse
```java
record CommentResponse(
    UUID id,
    String content,
    UUID userId,
    String username,
    UUID decisionId,
    String decisionTitle,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
)
```

## 🗳️ Vote DTO'ları

### VoteRequest (Create)
```java
record VoteRequest(
    @NotNull UUID userId,
    @NotNull UUID decisionId
)
```

### VoteResponse
```java
record VoteResponse(
    UUID id,
    UUID userId,
    String username,
    UUID decisionId,
    String decisionTitle,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
)
```

### VoteCountResponse (Statistics)
```java
record VoteCountResponse(
    UUID decisionId,
    long voteCount,
    boolean hasVoted
)
```

## 🏷️ Tag DTO'ları

### TagRequest (Create)
```java
record TagRequest(
    @NotBlank String name
)
```

### TagUpdateRequest
```java
record TagUpdateRequest(
    @NotBlank String name
)
```

### TagResponse
```java
record TagResponse(
    UUID id,
    String name,
    LocalDateTime createdAt
)
```

---

**Son Güncelleme**: 2026-04-24
