package org.karar.dev.domain.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.comment.dto.CommentRequest;
import org.karar.dev.domain.comment.dto.CommentResponse;
import org.karar.dev.domain.comment.dto.CommentUpdateRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Comment Management", description = "RESTful API for Comment resources")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "List comments")
    public ResponseEntity<BaseResponse<PageResponse<CommentResponse>>> getComments(
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllComments(pageable));
    }

    @GetMapping("/comments/{id}")
    @Operation(summary = "Get comment by ID")
    public ResponseEntity<BaseResponse<CommentResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @GetMapping("/decisions/{decisionId}/comments")
    @Operation(summary = "List comments by decision")
    public ResponseEntity<BaseResponse<PageResponse<CommentResponse>>> getByDecision(
            @PathVariable UUID decisionId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByDecisionId(decisionId, pageable));
    }

    @GetMapping("/users/{userId}/comments")
    @Operation(summary = "List comments by user")
    public ResponseEntity<BaseResponse<PageResponse<CommentResponse>>> getByUser(
            @PathVariable UUID userId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId, pageable));
    }

    @PostMapping("/comments")
    @Operation(summary = "Create comment")
    public ResponseEntity<BaseResponse<CommentResponse>> create(@Valid @RequestBody CommentRequest request) {
        BaseResponse<CommentResponse> response = commentService.createComment(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/comments/{id}")
    @Operation(summary = "Update comment")
    public ResponseEntity<BaseResponse<CommentResponse>> update(@PathVariable UUID id, @Valid @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(commentService.updateComment(id, request));
    }

    @DeleteMapping("/comments/{id}")
    @Operation(summary = "Delete comment")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(commentService.deleteComment(id));
    }
}
