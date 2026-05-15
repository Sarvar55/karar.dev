package org.karar.dev.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class that enables JPA Auditing with the custom AuditorAware implementation.
 * This allows automatic population of @CreatedBy and @LastModifiedBy fields
 * in entities that extend BaseEntity.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaAuditingConfig {
}
