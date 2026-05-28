package org.karar.dev.domain.comment.repository;
import org.karar.dev.domain.comment.entity.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByDecisionId(UUID decisionId, Pageable pageable);

    Page<Comment> findByUserId(UUID userId, Pageable pageable);

    Page<Comment> findByDecisionIdAndUserId(UUID decisionId, UUID userId, Pageable pageable);
}

