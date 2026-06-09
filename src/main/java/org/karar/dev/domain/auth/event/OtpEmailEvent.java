package org.karar.dev.domain.auth.event;

public record OtpEmailEvent(
        String email,
        String otpCode
) {
}
