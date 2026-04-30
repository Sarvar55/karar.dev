package org.karar.dev.domain.user.regular;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserUpdateRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.karar.dev.domain.comment.dto.CommentResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Regular User Management", description = "CRUD operations for regular users")
public class RegularUserController {

    private final RegularUserService regularUserService;
    private final UserCommentService userCommentService;

    @GetMapping("/{userId}/comments")
    @Operation(summary = "Get comments for a user")
    public ResponseEntity<BaseResponse<PageResponse<CommentResponse>>> getUserComments(
            @PathVariable UUID userId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(userCommentService.getCommentsByUserId(userId, pageable));
    }

    @GetMapping
    @Operation(summary = "Get all regular users")
    public ResponseEntity<BaseResponse<PageResponse<RegularUserResponse>>> getAll(
            @ParameterObject @PageableDefault(value = 5, sort = "createdAt") Pageable pageable) {
        BaseResponse<PageResponse<RegularUserResponse>> response = regularUserService.getAll(pageable);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get regular user by ID")
    public ResponseEntity<BaseResponse<RegularUserResponse>> getById(@PathVariable UUID id) {
        BaseResponse<RegularUserResponse> response = regularUserService.getUserById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update regular user")
    @PreAuthorize("@securityService.isAdminOrSelf(authentication, #id)")
    public ResponseEntity<BaseResponse<RegularUserResponse>> update(@PathVariable UUID id, @Valid @RequestBody RegularUserUpdateRequest request) {
        BaseResponse<RegularUserResponse> response = regularUserService.update(id, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete regular user")
    @PreAuthorize("@securityService.isAdminOrSelf(authentication, #id)")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        BaseResponse<Void> response = regularUserService.delete(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
