package org.karar.dev.domain.user.company.dto;

import java.util.UUID;

public record CompanyUserResponse(
    UUID id,
    String email,
    String companyName
) {}
