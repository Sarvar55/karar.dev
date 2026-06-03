package org.karar.dev.domain.auth.dto;

import java.util.UUID;

public record RegisterResponse(UUID id, String email, String message) {
}

