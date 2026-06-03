package org.karar.dev.common.audit;
import org.karar.dev.domain.audit.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.karar.dev.common.entity.BaseEntity;
import org.karar.dev.common.dto.BaseResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.ObjectMapper;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {

        Object result = joinPoint.proceed();

        try {
            String performedBy = getCurrentUser();
            String ipAddress = getClientIpAddress();
            String entityId = extractEntityId(result, joinPoint.getArgs());
            String details = buildDetails(auditable.action(), joinPoint.getArgs(), result);

            auditLogService.logAction(
                    auditable.entityName(),
                    entityId,
                    auditable.action(),
                    performedBy,
                    details,
                    ipAddress
            );
        } catch (Exception e) {

            log.error("Failed to create audit log for {}.{}: {}",
                    auditable.entityName(), auditable.action(), e.getMessage());
        }

        return result;
    }

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "ANONYMOUS";
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("Could not extract IP address: {}", e.getMessage());
        }
        return "UNKNOWN";
    }

    private String extractEntityId(Object result, Object[] args) {

        if (result instanceof BaseResponse<?> baseResponse) {
            Object data = baseResponse.getData();
            if (data != null) {
                try {

                    var idMethod = data.getClass().getMethod("id");
                    Object id = idMethod.invoke(data);
                    if (id != null) return id.toString();
                } catch (NoSuchMethodException e) {

                    try {
                        var getIdMethod = data.getClass().getMethod("getId");
                        Object id = getIdMethod.invoke(data);
                        if (id != null) return id.toString();
                    } catch (Exception ignored) {

                    }
                } catch (Exception ignored) {

                }
            }
        }

        if (result instanceof BaseEntity entity && entity.getId() != null) {
            return entity.getId().toString();
        }

        for (Object arg : args) {
            if (arg instanceof java.util.UUID uuid) {
                return uuid.toString();
            }
        }

        return "UNKNOWN";
    }

    private String buildDetails(AuditAction action, Object[] args, Object result) {
        try {
            return switch (action) {
                case CREATE -> {

                    for (Object arg : args) {
                        if (arg != null && !isPrimitiveOrWrapper(arg.getClass())
                                && !(arg instanceof java.util.UUID)
                                && !(arg instanceof org.springframework.data.domain.Pageable)) {
                            yield objectMapper.writeValueAsString(
                                    java.util.Map.of("action", "CREATE", "request", arg.toString())
                            );
                        }
                    }
                    yield objectMapper.writeValueAsString(java.util.Map.of("action", "CREATE"));
                }
                case UPDATE -> {
                    for (Object arg : args) {
                        if (arg != null && !isPrimitiveOrWrapper(arg.getClass())
                                && !(arg instanceof java.util.UUID)
                                && !(arg instanceof org.springframework.data.domain.Pageable)) {
                            yield objectMapper.writeValueAsString(
                                    java.util.Map.of("action", "UPDATE", "changes", arg.toString())
                            );
                        }
                    }
                    yield objectMapper.writeValueAsString(java.util.Map.of("action", "UPDATE"));
                }
                case DELETE -> objectMapper.writeValueAsString(
                        java.util.Map.of("action", "DELETE")
                );
            };
        } catch (Exception e) {
            log.debug("Could not serialize audit details: {}", e.getMessage());
            return "{\"action\": \"" + action + "\"}";
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || type == Integer.class
                || type == Long.class
                || type == Double.class
                || type == Float.class
                || type == Boolean.class
                || type == Byte.class
                || type == Short.class
                || type == Character.class;
    }
}

