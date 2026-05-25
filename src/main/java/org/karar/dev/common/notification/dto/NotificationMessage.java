package org.karar.dev.common.notification.dto;

import lombok.Builder;

@Builder
public record NotificationMessage(String email, String verificationUrl,String message) {
}
