package org.karar.dev.domain.decision;

import org.karar.dev.common.enums.RegretLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, UUID> {

    Page<Decision> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT d FROM Decision d JOIN FETCH d.user WHERE d.regretLevel = :regretLevel")
    Page<Decision> findByRegretLevel(RegretLevel regretLevel, Pageable pageable);


    @Query("SELECT d FROM Decision d JOIN d.tags dt WHERE dt.tag.id = :tagId")
    Page<Decision> findByTagId(@Param("tagId") UUID tagId, Pageable pageable);

    boolean existsByTitleAndUserId(String title, UUID userId);

    @Query("select d from Decision d join fetch d.tags where d.id =:decisionId")
    Optional<Decision> getDecisionsWithTags(@Param("decisionId") UUID decisionId);

    @Query("select d from Decision d join fetch d.tags")
    List<Decision> getAllDecisionsWithTags();

    @Modifying
    @Query("UPDATE Decision d SET d.voteCount = d.voteCount + 1 WHERE d.id = :id")
    void incrementVoteCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE Decision d SET d.voteCount = CASE WHEN d.voteCount > 0 THEN d.voteCount - 1 ELSE 0 END WHERE d.id = :id")
    void decrementVoteCount(@Param("id") UUID id);
}
