package org.karar.dev.domain.user.regular.service;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.dto.PageResponse;
import org.karar.dev.common.dto.BaseResponse;
import org.karar.dev.domain.comment.repository.CommentRepository;
import org.karar.dev.domain.comment.dto.CommentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommentService {

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<CommentResponse>> getCommentsByUserId(UUID userId, Pageable pageable) {
        return BaseResponse.success(new PageResponse<>(
                commentRepository.findByUserId(userId, pageable)
                        .map(comment -> new CommentResponse(
                                comment.getId(),
                                comment.getContent(),
                                comment.getUser() != null ? comment.getUser().getId() : null,
                                comment.getUser() != null ? comment.getUser().getUsername() : null,
                                comment.getDecision() != null ? comment.getDecision().getId() : null,
                                comment.getDecision() != null ? comment.getDecision().getTitle() : null,
                                comment.getCreatedAt(),
                                comment.getUpdatedAt()))));
    }
}
