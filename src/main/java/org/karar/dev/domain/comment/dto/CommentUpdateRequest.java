package org.karar.dev.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for updating an existing comment")
public record CommentUpdateRequest(

        @Schema(description = "Updated content of the comment", example = "Updated: Great decision! I had the same experience and it worked well.")
        @NotBlank(message = "Content is required")
        String content
) {
}
