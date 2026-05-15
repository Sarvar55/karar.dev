package org.karar.dev.domain.decisiontag;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.karar.dev.domain.base.DecisionTagId;
import org.karar.dev.domain.decision.Decision;
import org.karar.dev.domain.tag.Tag;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "decision_tags")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class DecisionTag {
    @EmbeddedId
    private DecisionTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("decisionId")
    private Decision decision;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
