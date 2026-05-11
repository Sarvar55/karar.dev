package org.karar.dev.common.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to mark service methods for automatic audit logging.
 * When placed on a method, the AuditAspect will intercept the call
 * and create an AuditLog entry with the specified action and entity name.
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code @Auditable(action = AuditAction.CREATE, entityName = "Decision")}
 * public Decision createDecision(DecisionRequest request) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * The type of audit action being performed.
     */
    AuditAction action();

    /**
     * The name of the entity being audited (e.g., "Decision", "Comment").
     */
    String entityName();
}
