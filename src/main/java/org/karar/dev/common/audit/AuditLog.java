package org.karar.dev.common.audit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an audit log entry.
 * Stores a record of every CREATE, UPDATE, and DELETE operation
 * performed on any audited entity in the system.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_entity_name", columnList = "entityName"),
        @Index(name = "idx_audit_performed_by", columnList = "performedBy"),
        @Index(name = "idx_audit_action", columnList = "action"),
        @Index(name = "idx_audit_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The name of the entity that was modified (e.g., "Decision", "Comment").
     */
    @Column(nullable = false)
    private String entityName;

    /**
     * The ID of the entity that was modified, stored as a string for flexibility.
     */
    @Column(nullable = false)
    private String entityId;

    /**
     * The type of action performed: CREATE, UPDATE, or DELETE.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    /**
     * The email/identifier of the user who performed the action.
     * "SYSTEM" if performed by an unauthenticated or system process.
     */
    @Column(nullable = false)
    private String performedBy;

    /**
     * JSON-formatted details about the changes made.
     * For CREATE: the created entity's key fields.
     * For UPDATE: before/after values of changed fields.
     * For DELETE: the deleted entity's key fields.
     */
    @Column(columnDefinition = "TEXT")
    private String details;

    /**
     * The IP address from which the request originated.
     */
    private String ipAddress;

    /**
     * The timestamp when this audit log entry was created.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
