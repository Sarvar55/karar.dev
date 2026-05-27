package org.karar.dev.domain.auth.event;

/**
 * Kafka event for email verification.
 */
public record EmailVerificationEvent(
        String email,
        String token,
        String verificationUrl
) {
}
