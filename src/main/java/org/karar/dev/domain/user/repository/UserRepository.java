package org.karar.dev.domain.user.repository;
import org.karar.dev.domain.user.entity.User;

import org.karar.dev.common.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByRole(Role role);
}
