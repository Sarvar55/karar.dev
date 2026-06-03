package org.karar.dev.domain.user.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyUserUpdateRequest(
    @NotBlank @Email String email,
    @NotBlank String companyName
) {}

