package org.karar.dev.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.karar.dev.common.enums.Role;

public record RegisterRequest(
        String email,
        String password,
        Role role,
        String username,
        String companyName
) {
}
