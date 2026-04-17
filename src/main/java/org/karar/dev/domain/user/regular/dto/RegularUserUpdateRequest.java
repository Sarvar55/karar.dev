package org.karar.dev.domain.user.regular.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegularUserUpdateRequest(
    @NotBlank @Email String email,
    @NotBlank String username
) {}
