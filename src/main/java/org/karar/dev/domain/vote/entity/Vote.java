package org.karar.dev.domain.vote.entity;

import jakarta.persistence.*;
import lombok.*;
import org.karar.dev.common.entity.BaseEntity;
import org.karar.dev.domain.decision.entity.Decision;
import org.karar.dev.domain.user.regular.entity.RegularUser;

@Entity
@Table(
        name = "votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "decision_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private RegularUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", nullable = false)
    private Decision decision;
}

