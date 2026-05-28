package org.karar.dev.domain.vote.repository;
import org.karar.dev.domain.vote.entity.Vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    List<Vote> findByDecisionId(UUID decisionId);
    List<Vote> findByUserId(UUID userId);
    Optional<Vote> findByUserIdAndDecisionId(UUID userId, UUID decisionId);
    boolean existsByUserIdAndDecisionId(UUID userId, UUID decisionId);
    long countByDecisionId(UUID decisionId);
}
