package org.karar.dev.domain.user.regular.controller;
import org.karar.dev.domain.user.regular.service.UserCommentService;
import org.karar.dev.domain.user.regular.service.RegularUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karar.dev.common.dto.PageResponse;
import org.karar.dev.common.dto.BaseResponse;
import org.karar.dev.domain.comment.dto.CommentResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserUpdateRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Regular User Management", description = "CRUD operations for regular users")
public class RegularUserController {

    private final RegularUserService regularUserService;
    private final UserCommentService userCommentService;

    @GetMapping(value = "/{userId}/comments", produces = "application/vnd.karar.dev+json;v=1.0")
    @Operation(summary = "Get comments for a user")
    public ResponseEntity<BaseResponse<PageResponse<CommentResponse>>> getUserComments(
            @PathVariable UUID userId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(userCommentService.getCommentsByUserId(userId, pageable));
    }

    @GetMapping(produces = "application/vnd.karar.dev+json;v=1.0")
    @Operation(summary = "Get all regular users")
    public ResponseEntity<BaseResponse<PageResponse<RegularUserResponse>>> getAll(
            @ParameterObject @PageableDefault(value = 5, sort = "createdAt") Pageable pageable) {
        BaseResponse<PageResponse<RegularUserResponse>> response = regularUserService.getAll(pageable);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
    @Operation(summary = "Get regular user by ID")
    public ResponseEntity<BaseResponse<RegularUserResponse>> getById(@PathVariable UUID id) {
        BaseResponse<RegularUserResponse> response = regularUserService.getUserById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
    @Operation(summary = "Update regular user")
    @PreAuthorize("@securityService.isAdminOrSelf(authentication, #id)")
    public ResponseEntity<BaseResponse<RegularUserResponse>> update(@PathVariable UUID id,
            @Valid @RequestBody RegularUserUpdateRequest request) {
        BaseResponse<RegularUserResponse> response = regularUserService.update(id, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping(value = "/{id}", produces = "application/vnd.karar.dev+json;v=1.0")
    @Operation(summary = "Delete regular user")
    @PreAuthorize("@securityService.isAdminOrSelf(authentication, #id)")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        BaseResponse<Void> response = regularUserService.delete(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
