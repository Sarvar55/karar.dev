package org.karar.dev.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "User details response")
public record UserResponse(
    @Schema(description = "User unique identifier")
    UUID userId,
    @Schema(example = "johndoe")
    String username,
    @Schema(example = "john@example.com")
    String email,
    @Schema(example = "false")
    boolean anonymous
) {
}
