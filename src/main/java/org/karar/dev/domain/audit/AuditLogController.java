package org.karar.dev.domain.audit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.karar.dev.common.audit.AuditAction;
import org.karar.dev.common.audit.AuditLogService;
import org.karar.dev.common.audit.dto.AuditLogResponse;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.domain.base.BaseResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for querying audit logs.
 * All endpoints require ADMIN role access.
 */
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Endpoints for querying audit trail records (Admin only)")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "Get all audit logs", description = "Returns paginated list of all audit log entries, sorted by newest first")
    public ResponseEntity<BaseResponse<PageResponse<AuditLogResponse>>> getAllLogs(
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getAllLogs(pageable));
    }

    @GetMapping("/entity/{entityName}")
    @Operation(summary = "Get audit logs by entity name", description = "Filter audit logs by entity type (e.g., Decision, Comment)")
    public ResponseEntity<BaseResponse<PageResponse<AuditLogResponse>>> getLogsByEntity(
            @Parameter(description = "Name of the entity", example = "Decision")
            @PathVariable String entityName,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getLogsByEntityName(entityName, pageable));
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    @Operation(summary = "Get audit logs for a specific entity instance", description = "Returns the complete audit history for a single entity")
    public ResponseEntity<BaseResponse<PageResponse<AuditLogResponse>>> getLogsByEntityInstance(
            @Parameter(description = "Name of the entity", example = "Decision")
            @PathVariable String entityName,
            @Parameter(description = "ID of the specific entity", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String entityId,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getLogsByEntityInstance(entityName, entityId, pageable));
    }

    @GetMapping("/user/{email}")
    @Operation(summary = "Get audit logs by user", description = "Filter audit logs by the user who performed the action")
    public ResponseEntity<BaseResponse<PageResponse<AuditLogResponse>>> getLogsByUser(
            @Parameter(description = "Email of the user", example = "admin@karar.dev")
            @PathVariable String email,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getLogsByPerformedBy(email, pageable));
    }

    @GetMapping("/action/{action}")
    @Operation(summary = "Get audit logs by action type", description = "Filter audit logs by action (CREATE, UPDATE, DELETE)")
    public ResponseEntity<BaseResponse<PageResponse<AuditLogResponse>>> getLogsByAction(
            @Parameter(description = "Type of action", example = "CREATE")
            @PathVariable AuditAction action,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getLogsByAction(action, pageable));
    }
}
