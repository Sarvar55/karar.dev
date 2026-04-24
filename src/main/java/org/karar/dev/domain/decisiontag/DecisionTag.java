package org.karar.dev.domain.decisiontag;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.karar.dev.domain.base.DecisionTagId;
import org.karar.dev.domain.decision.Decision;
import org.karar.dev.domain.tag.Tag;

import java.time.LocalDateTime;

@Entity
@Table(name = "decision_tags")
@Getter
@Setter
public class DecisionTag {
    @EmbeddedId
    private DecisionTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("decisionId")
    private Decision decision;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
