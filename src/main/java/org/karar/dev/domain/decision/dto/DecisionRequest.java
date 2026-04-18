package org.karar.dev.domain.decision.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.karar.dev.common.enums.RegretLevel;

import java.util.Set;
import java.util.UUID;

@Schema(description = "Request object for creating a new decision")
public record DecisionRequest(

        @Schema(description = "Title of the decision", example = "Should I learn Spring Boot?")
        @NotBlank(message = "Title is required")
        String title,

        @Schema(description = "Reasoning behind the decision", example = "I want to improve my backend skills")
        @NotBlank(message = "Why is required")
        String why,

        @Schema(description = "Alternative options considered", example = "Django, Node.js")
        String alternative,

        @Schema(description = "Level of regret for this decision", example = "LOW")
        @NotNull(message = "Regret level is required")
        RegretLevel regretLevel,

        @Schema(description = "ID of the user creating the decision", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "User ID is required")
        UUID userId,

        @Schema(description = "Set of tag IDs to associate with this decision", example = "[\"550e8400-e29b-41d4-a716-446655440001\"]")
        Set<UUID> tagIds
) {
}
