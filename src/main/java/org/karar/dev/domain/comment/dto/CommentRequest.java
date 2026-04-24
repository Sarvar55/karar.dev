package org.karar.dev.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request object for creating a new comment")
public record CommentRequest(

        @Schema(description = "Content of the comment", example = "Great decision! I had the same experience.")
        @NotBlank(message = "Content is required")
        String content,

        @Schema(description = "ID of the user creating the comment", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "User ID is required")
        UUID userId,

        @Schema(description = "ID of the decision being commented on", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotNull(message = "Decision ID is required")
        UUID decisionId
) {
}
