package org.karar.dev.common.notification.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record NotificationMessage(
        String email, 
        String verificationUrl, // Keeping this for backward compatibility if needed, but variables is better
        String message,
        String templateName,
        Map<String, Object> variables
) {
}

