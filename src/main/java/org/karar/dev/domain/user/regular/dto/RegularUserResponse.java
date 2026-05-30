package org.karar.dev.domain.user.regular.dto;

import java.util.UUID;

public record RegularUserResponse(
                UUID id,
                String email,
                String username) {
}
