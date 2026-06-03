package org.karar.dev.domain.auth.event;

public record EmailVerificationEvent(
        String email,
        String token,
        String verificationUrl
) {
}

