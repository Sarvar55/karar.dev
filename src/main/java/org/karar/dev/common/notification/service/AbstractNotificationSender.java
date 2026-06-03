package org.karar.dev.common.notification.service;

import org.karar.dev.common.notification.dto.NotificationMessage;

public abstract class AbstractNotificationSender implements NotificationSender {

    @Override
    public void send(NotificationMessage message) {
        validate(message);

        String content = buildContent(message);

        doSend(message, content);
    }

    protected void validate(NotificationMessage message) {
        if (message.verificationUrl() == null || message.verificationUrl().isBlank()) {
            throw new IllegalArgumentException("Verification URL cannot be null or blank");
        }
        if (message.email() == null || message.email().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
    }

    protected String buildContent(NotificationMessage message) {
        return message.message();
    }

    protected abstract void doSend(NotificationMessage message, String content);
}

