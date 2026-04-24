package org.karar.dev.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.karar.dev.common.enums.Role;

public record RegisterRequest(
        @Email
        @NotBlank
        @NotNull
        String email,
        @NotBlank
        @NotNull
        String password,
        Role role,
        String username,
        String companyName
) {
}
