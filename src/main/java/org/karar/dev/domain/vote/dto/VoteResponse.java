package org.karar.dev.domain.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object containing vote details")
public record VoteResponse(

        @Schema(description = "Unique identifier of the vote", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the user who cast the vote", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID userId,

        @Schema(description = "Username of the voter", example = "johndoe")
        String username,

        @Schema(description = "ID of the decision that was voted on", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID decisionId,

        @Schema(description = "Title of the decision", example = "Should I learn Spring Boot?")
        String decisionTitle,

        @Schema(description = "Timestamp when the vote was cast", example = "2025-01-20T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the vote was last updated", example = "2025-01-20T10:30:00")
        LocalDateTime updatedAt
) {
}

