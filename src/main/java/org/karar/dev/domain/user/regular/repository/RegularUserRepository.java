package org.karar.dev.domain.user.regular.repository;
import org.karar.dev.domain.user.regular.entity.RegularUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RegularUserRepository extends JpaRepository<RegularUser, UUID> {

}
