package org.karar.dev.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request object for updating a user")
public record UserUpdateRequest(
    @Schema(example = "johndoe")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,

    @Schema(example = "john@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @Schema(example = "false")
    boolean anonymous
) {
}
