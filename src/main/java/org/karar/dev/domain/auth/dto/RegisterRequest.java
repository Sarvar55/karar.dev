package org.karar.dev.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        String username,
        String companyName
) {
    public boolean isCompanyRegistration() {
        return companyName != null && !companyName.isBlank();
    }
}

