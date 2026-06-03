package org.karar.dev.domain.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.karar.dev.common.audit.AuditAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response object containing audit log details")
public record AuditLogResponse(

        @Schema(description = "Unique identifier of the audit log entry")
        UUID id,

        @Schema(description = "Name of the audited entity", example = "Decision")
        String entityName,

        @Schema(description = "ID of the audited entity", example = "550e8400-e29b-41d4-a716-446655440000")
        String entityId,

        @Schema(description = "Type of action performed", example = "CREATE")
        AuditAction action,

        @Schema(description = "Email of the user who performed the action", example = "admin@karar.dev")
        String performedBy,

        @Schema(description = "JSON details of the changes made")
        String details,

        @Schema(description = "IP address of the request origin", example = "192.168.1.1")
        String ipAddress,

        @Schema(description = "Timestamp when the action was performed")
        LocalDateTime createdAt
) {
}

