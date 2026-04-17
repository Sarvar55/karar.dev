package org.karar.dev.domain.auth.dto;

import org.karar.dev.common.enums.Role;
import java.util.UUID;

public record AuthResponse(
    UUID id,
    String email,
    Role role,
    String accessToken,
    String refreshToken
) {
}
