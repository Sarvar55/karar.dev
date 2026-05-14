package org.karar.dev.domain.base;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@Embeddable
public class DecisionTagId implements Serializable {
    private UUID decisionId;
    private UUID tagId;

    public DecisionTagId(UUID decisionId, UUID tagId) {
        this.decisionId = decisionId;
        this.tagId = tagId;
    }
    public DecisionTagId() {
    }

}
