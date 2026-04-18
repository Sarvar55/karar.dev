package org.karar.dev.domain.decision;

import jakarta.persistence.*;
import lombok.*;
import org.karar.dev.common.entity.BaseEntity;
import org.karar.dev.common.enums.RegretLevel;
import org.karar.dev.domain.comment.Comment;
import org.karar.dev.domain.decisiontag.DecisionTag;
import org.karar.dev.domain.user.regular.RegularUser;
import org.karar.dev.domain.vote.Vote;

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
    private RegretLevel regretLevel;
    private int voteCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private RegularUser user;

    @OneToMany(mappedBy = "decision", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<Comment> comments;

    @OneToMany(mappedBy = "decision", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<DecisionTag> tags;

    @OneToMany(mappedBy = "decision", orphanRemoval = true, fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<Vote> votes;

}
