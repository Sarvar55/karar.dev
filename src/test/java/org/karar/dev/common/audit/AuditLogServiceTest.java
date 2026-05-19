package org.karar.dev.common.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.karar.dev.common.audit.dto.AuditLogResponse;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.domain.annotation.UnitTest;
import org.karar.dev.domain.base.BaseResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = AuditLog.builder()
                .entityName("Vote")
                .entityId("123")
                .action(AuditAction.CREATE)
                .performedBy("user1")
                .details("created vote")
                .ipAddress("127.0.0.1")
                .build();
    }

    @ParameterizedTest
    @EnumSource(AuditAction.class)
    void shouldSaveAuditLogForEachAction(AuditAction action) {

        auditLogService.logAction(
                auditLog.getEntityName(),
                auditLog.getEntityId(),
                action,
                auditLog.getPerformedBy(),
                auditLog.getDetails(),
                auditLog.getIpAddress()
        );

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);

        verify(auditLogRepository).save(captor.capture());

        assertThat(captor.getValue().getAction())
                .isEqualTo(action);
    }

    @Test
    void shouldCreateAuditLogSuccessfully() {

        auditLogService.logAction(
                auditLog.getEntityName(),
                auditLog.getEntityId(),
                auditLog.getAction(),
                auditLog.getPerformedBy(),
                auditLog.getDetails(),
                auditLog.getIpAddress()
        );

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);

        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();

        assertThat(saved.getEntityName()).isEqualTo(auditLog.getEntityName());
        assertThat(saved.getEntityId()).isEqualTo(auditLog.getEntityId());
        assertThat(saved.getAction()).isEqualTo(auditLog.getAction());
        assertThat(saved.getPerformedBy()).isEqualTo(auditLog.getPerformedBy());
        assertThat(saved.getDetails()).isEqualTo(auditLog.getDetails());
        assertThat(saved.getIpAddress()).isEqualTo(auditLog.getIpAddress());
    }

    @Test
    @DisplayName("Should get all logs")
    void getAllLogs() {
        Pageable pageable = PageRequest.of(0, 10);

        when(auditLogRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(auditLog), pageable, 1));

        BaseResponse<PageResponse<AuditLogResponse>> responses = auditLogService.getAllLogs(pageable);

        assertThat(responses.getData().getContent()).hasSize(1)
                .first()
                .extracting(AuditLogResponse::entityId, AuditLogResponse::action, AuditLogResponse::performedBy, AuditLogResponse::details, AuditLogResponse::ipAddress)
                .containsExactly(auditLog.getEntityId(), auditLog.getAction(), auditLog.getPerformedBy(), auditLog.getDetails(), auditLog.getIpAddress());

        assertThat(responses.getData().getTotalElements()).isEqualTo(1L);
        assertThat(responses.getData().getTotalPages()).isEqualTo(1);

        verify(auditLogRepository).findAll(pageable);
        verifyNoMoreInteractions(auditLogRepository);
    }

    @Test
    @DisplayName("Should get logs by entity name")
    void getLogsByEntityName() {
        Pageable pageable = PageRequest.of(0, 10);
        final String entityName = auditLog.getEntityName();
        when(auditLogRepository.findByEntityName(entityName, pageable))
                .thenReturn(new PageImpl<>(List.of(auditLog), pageable, 1));

        BaseResponse<PageResponse<AuditLogResponse>> responses = auditLogService.getLogsByEntityName(entityName, pageable);

        assertThat(responses.getData().getContent()).hasSize(1)
                .first()
                .extracting(AuditLogResponse::entityId, AuditLogResponse::action, AuditLogResponse::performedBy, AuditLogResponse::details, AuditLogResponse::ipAddress)
                .containsExactly(auditLog.getEntityId(), auditLog.getAction(), auditLog.getPerformedBy(), auditLog.getDetails(), auditLog.getIpAddress());

        assertThat(responses.getData().getTotalElements()).isEqualTo(1L);
        assertThat(responses.getData().getTotalPages()).isEqualTo(1);

        assertThat(responses.getData().getContent()).first().extracting(AuditLogResponse::entityName).isEqualTo(entityName);

        verify(auditLogRepository).findByEntityName(entityName, pageable);
        verifyNoMoreInteractions(auditLogRepository);
    }

    @Test
    @DisplayName("Should get logs by performed by")
    void getLogsByPerformedBy() {
        Pageable pageable = PageRequest.of(0, 10);
        final String performedBy = auditLog.getPerformedBy();
        when(auditLogRepository.findByPerformedBy(performedBy, pageable))
                .thenReturn(new PageImpl<>(List.of(auditLog), pageable, 1));

        BaseResponse<PageResponse<AuditLogResponse>> responses =
                auditLogService.getLogsByPerformedBy(performedBy, pageable);

        assertThat(responses.getData().getContent()).hasSize(1)
                .first()
                .extracting(AuditLogResponse::entityId, AuditLogResponse::action, AuditLogResponse::performedBy, AuditLogResponse::details, AuditLogResponse::ipAddress)
                .containsExactly(auditLog.getEntityId(), auditLog.getAction(), auditLog.getPerformedBy(), auditLog.getDetails(), auditLog.getIpAddress());

        assertThat(responses.getData().getTotalElements()).isEqualTo(1L);
        assertThat(responses.getData().getTotalPages()).isEqualTo(1);

        verify(auditLogRepository).findByPerformedBy(performedBy, pageable);

    }

    @Test
    @DisplayName("Should get logs by action")
    void getLogsByAction() {
        Pageable pageable = PageRequest.of(0, 10);
        final AuditAction action = auditLog.getAction();
        when(auditLogRepository.findByAction(action, pageable))
                .thenReturn(new PageImpl<>(List.of(auditLog), pageable, 1));

        BaseResponse<PageResponse<AuditLogResponse>> responses =
                auditLogService.getLogsByAction(action, pageable);

        assertThat(responses.getData().getContent()).hasSize(1)
                .first()
                .extracting(AuditLogResponse::entityId, AuditLogResponse::action, AuditLogResponse::performedBy, AuditLogResponse::details, AuditLogResponse::ipAddress)
                .containsExactly(auditLog.getEntityId(), auditLog.getAction(), auditLog.getPerformedBy(), auditLog.getDetails(), auditLog.getIpAddress());

        assertThat(responses.getData().getTotalElements()).isEqualTo(1L);
        assertThat(responses.getData().getTotalPages()).isEqualTo(1);

        verify(auditLogRepository).findByAction(action, pageable);

    }

    @Test
    @DisplayName("Should get logs by entity instance")
    void getLogsByEntityInstance() {
        Pageable pageable = PageRequest.of(0, 10);

        final String entityId = auditLog.getEntityId();
        final String entityName = auditLog.getEntityName();
        when(auditLogRepository.findByEntityNameAndEntityId(entityName, entityId, pageable))
                .thenReturn(new PageImpl<>(List.of(auditLog), pageable, 1));

        BaseResponse<PageResponse<AuditLogResponse>> responses =
                auditLogService.getLogsByEntityInstance(entityName, entityId, pageable);

        assertThat(responses.getData().getContent()).hasSize(1)
                .first()
                .extracting(AuditLogResponse::entityId, AuditLogResponse::action, AuditLogResponse::performedBy, AuditLogResponse::details, AuditLogResponse::ipAddress)
                .containsExactly(auditLog.getEntityId(), auditLog.getAction(), auditLog.getPerformedBy(), auditLog.getDetails(), auditLog.getIpAddress());

        assertThat(responses.getData().getTotalElements()).isEqualTo(1L);
        assertThat(responses.getData().getTotalPages()).isEqualTo(1);

        verify(auditLogRepository).findByEntityNameAndEntityId(entityName, entityId, pageable);

    }
}