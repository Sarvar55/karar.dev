package org.karar.dev.domain.user.company;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, UUID> {
}
