package org.karar.dev.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for creating a user")
public record UserCreateRequest(
    @Schema(example = "johndoe", description = "Unique username")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,

    @Schema(example = "secret123", description = "User password")
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,

    @Schema(example = "john@example.com", description = "User email address")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @Schema(example = "false")
    boolean anonymous
) {
}
