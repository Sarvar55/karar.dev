package org.karar.dev.domain.tag.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object containing tag details")
public record TagResponse(

        @Schema(description = "Unique identifier of the tag", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Name of the tag", example = "programming")
        String name,

        @Schema(description = "Number of decisions using this tag", example = "15")
        int decisionCount,

        @Schema(description = "Timestamp when the tag was created", example = "2025-01-20T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the tag was last updated", example = "2025-01-20T10:30:00")
        LocalDateTime updatedAt
) {
}
