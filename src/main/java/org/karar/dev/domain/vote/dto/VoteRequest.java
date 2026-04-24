package org.karar.dev.domain.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request object for creating a vote on a decision")
public record VoteRequest(

        @Schema(description = "ID of the user voting", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "User ID is required")
        UUID userId,

        @Schema(description = "ID of the decision being voted on", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotNull(message = "Decision ID is required")
        UUID decisionId
) {
}
