package org.karar.dev.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object containing comment details")
public record CommentResponse(

                @Schema(description = "Unique identifier of the comment", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,

                @Schema(description = "Content of the comment", example = "Great decision! I had the same experience.") String content,

                @Schema(description = "ID of the user who created the comment", example = "550e8400-e29b-41d4-a716-446655440001") UUID userId,

                @Schema(description = "Username of the user who created the comment", example = "johndoe") String username,

                @Schema(description = "ID of the decision being commented on", example = "550e8400-e29b-41d4-a716-446655440002") UUID decisionId,

                @Schema(description = "Title of the decision being commented on", example = "Should I learn Spring Boot?") String decisionTitle,

                @Schema(description = "Timestamp when the comment was created", example = "2025-01-20T10:30:00") LocalDateTime createdAt,

                @Schema(description = "Timestamp when the comment was last updated", example = "2025-01-20T10:30:00") LocalDateTime updatedAt) {
}

