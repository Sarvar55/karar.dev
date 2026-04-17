package org.karar.dev.domain.user.regular;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.karar.dev.common.enums.Role;
import org.karar.dev.domain.user.User;


@Entity
@Table(name = "regular_users")
@Getter
@Setter
@NoArgsConstructor
public class RegularUser extends User {

    private String username;

    public RegularUser(String email, String password, String username) {
        super(email, password, Role.USER);
        this.username = username;
    }
}
