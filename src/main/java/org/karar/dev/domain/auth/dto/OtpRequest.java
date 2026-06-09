package org.karar.dev.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpRequest(
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email
) {
}
