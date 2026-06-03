package org.karar.dev.domain.decision.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.karar.dev.common.entity.BaseEntity;
import org.karar.dev.domain.decision.entity.RegretLevel;
import org.karar.dev.domain.comment.entity.Comment;
import org.karar.dev.domain.decision.entity.DecisionTag;
import org.karar.dev.domain.user.regular.entity.RegularUser;
import org.karar.dev.domain.vote.entity.Vote;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "decisions")
public class Decision extends BaseEntity {
    private String title;
    private String why;
    private String alternative;
    @Enumerated(EnumType.STRING)
    private RegretLevel regretLevel;
    private int voteCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private RegularUser user;

    @OneToMany(mappedBy = "decision", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<Comment> comments;

    @OneToMany(mappedBy = "decision", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<DecisionTag> tags = new HashSet<>();

    @OneToMany(mappedBy = "decision", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<Vote> votes;

}

