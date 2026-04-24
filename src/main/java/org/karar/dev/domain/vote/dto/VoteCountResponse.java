package org.karar.dev.domain.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response object containing vote statistics for a decision")
public record VoteCountResponse(

        @Schema(description = "ID of the decision", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID decisionId,

        @Schema(description = "Total number of votes for this decision", example = "42")
        long voteCount,

        @Schema(description = "Whether the current user has voted on this decision", example = "true")
        boolean hasVoted
) {
}
