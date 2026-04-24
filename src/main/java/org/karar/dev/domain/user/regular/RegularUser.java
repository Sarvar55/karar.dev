package org.karar.dev.domain.user.regular;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.karar.dev.common.enums.Role;
import org.karar.dev.domain.comment.Comment;
import org.karar.dev.domain.decision.Decision;
import org.karar.dev.domain.user.User;
import org.karar.dev.domain.vote.Vote;

import java.util.Set;


@Entity
@Table(name = "regular_users")
@Getter
@Setter
@NoArgsConstructor
public class RegularUser extends User {

    private String username;

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<Decision> decisions;

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<Comment> comments;

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<Vote> votes;

    public RegularUser(String email, String password, String username) {
        super(email, password, Role.USER);
        this.username = username;
    }

    public RegularUser(String email, String password, Role role) {
        this.setEmail(email);
        this.setRole(role);
        this.setPassword(password);
    }
}
