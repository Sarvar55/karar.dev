package org.karar.dev.common.notification.service;

import org.karar.dev.common.notification.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationSenderFactory {
    private final Map<NotificationType, NotificationSender> senderMap;

    public NotificationSenderFactory(List<NotificationSender> senders) {
        this.senderMap = senders
                .stream()
                .collect(Collectors.toMap(NotificationSender::type, Function.identity()));
    }

    public NotificationSender getSender(NotificationType type) {
        return senderMap.get(type);
    }


}
