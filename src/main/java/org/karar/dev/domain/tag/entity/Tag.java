package org.karar.dev.domain.tag.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.karar.dev.common.entity.BaseEntity;
import org.karar.dev.domain.decision.entity.DecisionTag;

import java.util.Set;

@Data
@Entity
@Table(name = "tags")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Tag extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private Set<DecisionTag> decisionTags;
}

