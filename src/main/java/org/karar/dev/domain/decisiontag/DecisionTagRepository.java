package org.karar.dev.domain.decisiontag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.karar.dev.domain.base.DecisionTagId;

import java.util.List;
import java.util.UUID;

@Repository
public interface DecisionTagRepository extends JpaRepository<DecisionTag, DecisionTagId> {
    List<DecisionTag> findByDecisionId(UUID decisionId);
    List<DecisionTag> findByTagId(UUID tagId);
    void deleteByDecisionId(UUID decisionId);
}
