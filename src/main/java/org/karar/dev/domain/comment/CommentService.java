package org.karar.dev.domain.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.common.security.service.SecurityService;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.comment.dto.CommentRequest;
import org.karar.dev.domain.comment.dto.CommentResponse;
import org.karar.dev.domain.comment.dto.CommentUpdateRequest;
import org.karar.dev.domain.decision.Decision;
import org.karar.dev.domain.decision.DecisionService;
import org.karar.dev.domain.user.regular.RegularUser;
import org.karar.dev.domain.user.regular.RegularUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final RegularUserService regularUserService;
    private final DecisionService decisionService;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<CommentResponse>> getAllComments(Pageable pageable) {
        Page<CommentResponse> responses = commentRepository.findAll(pageable)
                .map(this::mapToResponse);
        return BaseResponse.success(new PageResponse<>(responses));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<CommentResponse>> getCommentsByDecisionId(UUID decisionId, Pageable pageable) {
        if (!decisionService.existsById(decisionId)) {
            throw new ResourceNotFoundException("Decision", "id", decisionId);
        }
        Page<CommentResponse> responses = commentRepository.findByDecisionId(decisionId, pageable)
                .map(this::mapToResponse);
        return BaseResponse.success(new PageResponse<>(responses));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<CommentResponse>> getCommentsByUserId(UUID userId, Pageable pageable) {
        if (!regularUserService.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Page<CommentResponse> responses = commentRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
        return BaseResponse.success(new PageResponse<>(responses));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<CommentResponse>> getCommentsByDecisionIdAndUserId(UUID decisionId, UUID userId, Pageable pageable) {
        if (!decisionService.existsById(decisionId)) {
            throw new ResourceNotFoundException("Decision", "id", decisionId);
        }
        if (!regularUserService.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Page<CommentResponse> responses = commentRepository.findByDecisionIdAndUserId(decisionId, userId, pageable)
                .map(this::mapToResponse);
        return BaseResponse.success(new PageResponse<>(responses));
    }

    @Transactional(readOnly = true)
    public BaseResponse<CommentResponse> getCommentById(UUID id) {
        Comment comment = findCommentOrThrow(id);
        return BaseResponse.success(mapToResponse(comment));
    }

    @Transactional
    public BaseResponse<CommentResponse> createComment(CommentRequest request) {
        UUID currentUserId = securityService.getCurrentUserId();
        log.debug("Creating comment for authenticated user: {}", currentUserId);

        RegularUser user = regularUserService.getById(currentUserId);
        if (user == null) {
            throw new ResourceNotFoundException("User", "id", currentUserId);
        }

        Decision decision = decisionService.getById(request.decisionId());
        if (decision == null) {
            throw new ResourceNotFoundException("Decision", "id", request.decisionId());
        }

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setUser(user);
        comment.setDecision(decision);

        Comment savedComment = commentRepository.saveAndFlush(comment);
        log.info("Comment created successfully: {}", savedComment.getId());
        return BaseResponse.success(mapToResponse(savedComment), HttpStatus.CREATED);
    }

    @Transactional
    public BaseResponse<CommentResponse> updateComment(UUID id, CommentUpdateRequest request) {
        Comment comment = findCommentOrThrow(id);

        comment.setContent(request.content());

        Comment updatedComment = commentRepository.saveAndFlush(comment);
        return BaseResponse.success(mapToResponse(updatedComment));
    }

    @Transactional
    public BaseResponse<Void> deleteComment(UUID id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment", "id", id);
        }
        commentRepository.deleteById(id);
        return BaseResponse.success(null, HttpStatus.NO_CONTENT);
    }

    private Comment findCommentOrThrow(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
    }

    private <T> PageResponse<T> mapToPageResponse(Page<T> page) {
        return new PageResponse<>(page);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getUser() != null ? comment.getUser().getId() : null,
                comment.getUser() != null ? comment.getUser().getUsername() : null,
                comment.getDecision() != null ? comment.getDecision().getId() : null,
                comment.getDecision() != null ? comment.getDecision().getTitle() : null,
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
