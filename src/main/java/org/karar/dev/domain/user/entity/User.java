package org.karar.dev.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.karar.dev.common.entity.BaseEntity;
import org.karar.dev.common.enums.Role;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public abstract class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean emailVerified;

    private boolean accountLocked;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    private LocalDateTime lockedUntil;

    protected User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public boolean isAccountLocked() {
        if (lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now())) {
            return true;
        }

        if (lockedUntil != null && lockedUntil.isBefore(LocalDateTime.now())) {
            this.accountLocked = false;
            this.lockedUntil = null;
            this.failedLoginAttempts = 0;
        }
        return accountLocked;
    }
}

