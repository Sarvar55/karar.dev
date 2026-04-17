package org.karar.dev.domain.user.regular;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface RegularUserRepository extends JpaRepository<RegularUser, UUID> {

}
