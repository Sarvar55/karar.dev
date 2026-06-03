package org.karar.dev.common.notification.service;

import org.karar.dev.common.notification.dto.NotificationMessage;
import org.karar.dev.common.notification.enums.NotificationType;

public interface NotificationSender {
    void send(NotificationMessage message);

    NotificationType type();
}

