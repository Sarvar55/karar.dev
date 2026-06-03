package org.karar.dev.domain.comment.controller;
import org.karar.dev.domain.comment.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.common.dto.PageResponse;
import org.karar.dev.common.dto.BaseResponse;
import org.karar.dev.domain.comment.dto.CommentRequest;
import org.karar.dev.domain.comment.dto.CommentResponse;
import org.karar.dev.domain.comment.dto.CommentUpdateRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Comment Management", description = "RESTful API for Comment resources")
public class CommentController {

        private final CommentService commentService;

        @Operation(summary = "List comments", description = "Retrieve paginated comments with optional filtering. Examples:\n"
                        +
                        "- /api/comments?page=0&size=10&sort=createdAt,desc\n" +
                        "- /api/comments?decisionId={id}\n" +
                        "- /api/comments?userId={id}\n" +
                        "- /api/comments?decisionId={id}&userId={id}")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved comments", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @GetMapping(produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<PageResponse<CommentResponse>>> getComments(
                        @Parameter(description = "Filter by decision ID") @RequestParam(required = false) UUID decisionId,
                        @Parameter(description = "Filter by user ID") @RequestParam(required = false) UUID userId,
                        @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
                BaseResponse<PageResponse<CommentResponse>> response;
                if (decisionId != null && userId != null) {
                        response = commentService.getCommentsByDecisionIdAndUserId(decisionId, userId, pageable);
                } else if (decisionId != null) {
                        response = commentService.getCommentsByDecisionId(decisionId, pageable);
                } else if (userId != null) {
                        response = commentService.getCommentsByUserId(userId, pageable);
                } else {
                        response = commentService.getAllComments(pageable);
                }
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Get comment by ID", description = "Retrieve a specific comment by its unique identifier")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Comment found successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @GetMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<CommentResponse>> getCommentById(
                        @Parameter(description = "UUID of the comment to retrieve", required = true) @PathVariable UUID id) {
                BaseResponse<CommentResponse> response = commentService.getCommentById(id);
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Create a new comment", description = "Create a new comment with content for a specific decision")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Comment created successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "User or Decision not found", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @PostMapping(produces = "application/vnd.karar.dev+json;v=1.0")
        public ResponseEntity<BaseResponse<CommentResponse>> createComment(
                        @Valid @RequestBody CommentRequest request) {
                BaseResponse<CommentResponse> response = commentService.createComment(request);
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Update an existing comment", description = "Update the content of an existing comment by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Comment updated successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @PutMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
        @PreAuthorize("@securityService.isAdminOrOwnerOfComment(authentication, #id)")
        public ResponseEntity<BaseResponse<CommentResponse>> updateComment(
                        @Parameter(description = "UUID of the comment to update", required = true) @PathVariable UUID id,
                        @Valid @RequestBody CommentUpdateRequest request) {
                BaseResponse<CommentResponse> response = commentService.updateComment(id, request);
                return ResponseEntity.status(response.getStatus()).body(response);
        }

        @Operation(summary = "Delete a comment", description = "Remove a comment from the system by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Comment deleted successfully", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
        })
        @DeleteMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
        @PreAuthorize("@securityService.isAdminOrOwnerOfComment(authentication, #id)")
        public ResponseEntity<BaseResponse<Void>> deleteComment(
                        @Parameter(description = "UUID of the comment to delete", required = true) @PathVariable UUID id) {
                BaseResponse<Void> response = commentService.deleteComment(id);
                return ResponseEntity.status(response.getStatus()).body(response);
        }
}

