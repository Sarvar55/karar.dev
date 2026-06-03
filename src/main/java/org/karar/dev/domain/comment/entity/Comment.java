package org.karar.dev.domain.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.karar.dev.common.entity.BaseEntity;
import org.karar.dev.domain.decision.entity.Decision;
import org.karar.dev.domain.user.regular.entity.RegularUser;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comment extends BaseEntity {
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private RegularUser user;

    @ManyToOne
    @JoinColumn(name = "decision_id")
    private Decision decision;
}

