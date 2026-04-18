package org.karar.dev.domain.decision;

import org.karar.dev.common.enums.RegretLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, UUID> {

    List<Decision> findByUserId(UUID userId);

    List<Decision> findByRegretLevel(RegretLevel regretLevel);

    boolean existsByTitleAndUserId(String title, UUID userId);
}
