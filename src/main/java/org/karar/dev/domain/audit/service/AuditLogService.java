package org.karar.dev.domain.audit.service;
import org.karar.dev.common.audit.AuditAction;
import org.karar.dev.domain.audit.repository.AuditLogRepository;
import org.karar.dev.domain.audit.entity.AuditLog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.domain.audit.dto.AuditLogResponse;
import org.karar.dev.common.dto.PageResponse;
import org.karar.dev.common.dto.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for creating and querying audit log entries.
 * Uses REQUIRES_NEW propagation for log creation to ensure
 * audit records are persisted even if the parent transaction rolls back.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Creates an audit log entry in a new transaction.
     * This ensures the audit record is saved even if the calling transaction fails.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(String entityName,
                          String entityId,
                          AuditAction action,
                          String performedBy,
                          String details,
                          String ipAddress) {

        AuditLog auditLog = AuditLog.builder()
                .entityName(entityName)
                .entityId(entityId)
                .action(action)
                .performedBy(performedBy != null ? performedBy : "SYSTEM")
                .details(details)
                .ipAddress(ipAddress)
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit log created: {} {} on {} [{}]", action, entityName, entityId, performedBy);
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<AuditLogResponse>> getAllLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAll(pageable);
        return BaseResponse.success(mapToPageResponse(page.map(this::mapToResponse)));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<AuditLogResponse>> getLogsByEntityName(String entityName, Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findByEntityName(entityName, pageable);
        return BaseResponse.success(mapToPageResponse(page.map(this::mapToResponse)));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<AuditLogResponse>> getLogsByPerformedBy(String performedBy, Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findByPerformedBy(performedBy, pageable);
        return BaseResponse.success(mapToPageResponse(page.map(this::mapToResponse)));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<AuditLogResponse>> getLogsByAction(AuditAction action, Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findByAction(action, pageable);
        return BaseResponse.success(mapToPageResponse(page.map(this::mapToResponse)));
    }

    @Transactional(readOnly = true)
    public BaseResponse<PageResponse<AuditLogResponse>> getLogsByEntityInstance(String entityName, String entityId, Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findByEntityNameAndEntityId(entityName, entityId, pageable);
        return BaseResponse.success(mapToPageResponse(page.map(this::mapToResponse)));
    }

    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getEntityName(),
                auditLog.getEntityId(),
                auditLog.getAction(),
                auditLog.getPerformedBy(),
                auditLog.getDetails(),
                auditLog.getIpAddress(),
                auditLog.getCreatedAt()
        );
    }

    private <T> PageResponse<T> mapToPageResponse(Page<T> page) {
        return new PageResponse<>(page);
    }
}
