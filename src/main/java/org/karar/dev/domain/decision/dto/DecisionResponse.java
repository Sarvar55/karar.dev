package org.karar.dev.domain.decision.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.karar.dev.domain.decision.entity.RegretLevel;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Response object containing decision details")
public record DecisionResponse(

        @Schema(description = "Unique identifier of the decision", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Title of the decision", example = "Should I learn Spring Boot?")
        String title,

        @Schema(description = "Reasoning behind the decision", example = "I want to improve my backend skills")
        String why,

        @Schema(description = "Alternative options considered", example = "Django, Node.js")
        String alternative,

        @Schema(description = "Level of regret for this decision", example = "LOW")
        RegretLevel regretLevel,

        @Schema(description = "Number of votes on this decision", example = "42")
        int voteCount,

        @Schema(description = "ID of the user who created the decision", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @Schema(description = "Username of the creator", example = "john_doe")
        String username,

        @Schema(description = "Number of comments on this decision", example = "5")
        int commentCount,

        @Schema(description = "Set of tag names associated with this decision", example = "[\"programming\", \"career\"]")
        Set<String> tags,

        @Schema(description = "Timestamp when the decision was created", example = "2025-01-20T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the decision was last updated", example = "2025-01-20T10:30:00")
        LocalDateTime updatedAt
) {
}
