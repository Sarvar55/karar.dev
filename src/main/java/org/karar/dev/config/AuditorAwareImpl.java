package org.karar.dev.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of Spring Data JPA's AuditorAware interface.
 * Provides the currently authenticated user's email to populate
 * {@code @CreatedBy} and {@code @LastModifiedBy} fields in entities.
 *
 * <p>If no user is authenticated (e.g., system processes, seed data),
 * returns "SYSTEM" as the auditor.</p>
 */
@Component("auditorAwareImpl")
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of("SYSTEM");
        }

        return Optional.of(authentication.getName());
    }
}
