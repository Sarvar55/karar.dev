package org.karar.dev.common.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repository for querying audit log entries.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    /**
     * Find audit logs by entity name (e.g., "Decision", "Comment").
     */
    Page<AuditLog> findByEntityName(String entityName, Pageable pageable);

    /**
     * Find audit logs by the user who performed the action.
     */
    Page<AuditLog> findByPerformedBy(String performedBy, Pageable pageable);

    /**
     * Find audit logs by action type (CREATE, UPDATE, DELETE).
     */
    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);

    /**
     * Find audit logs for a specific entity instance.
     */
    Page<AuditLog> findByEntityNameAndEntityId(String entityName, String entityId, Pageable pageable);

    /**
     * Find audit logs within a time range.
     */
    Page<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Find audit logs by entity name and action type.
     */
    Page<AuditLog> findByEntityNameAndAction(String entityName, AuditAction action, Pageable pageable);
}
