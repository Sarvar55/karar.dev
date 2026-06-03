package org.karar.dev.common.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.notification.dto.NotificationMessage;
import org.karar.dev.common.notification.enums.NotificationType;
import org.karar.dev.domain.auth.event.EmailVerificationEvent;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRegisteredDispatcher implements NotificationDispatcher {

    private final NotificationSenderFactory notificationSenderFactory;

    @Override
    public void dispatch(Object event) {
        log.info("Dispatching user registered notification");
        EmailVerificationEvent verificationEvent = (EmailVerificationEvent) event;

        NotificationMessage message = NotificationMessage.builder().email(verificationEvent.email())
                .verificationUrl(verificationEvent.verificationUrl())
                .message("Please verify your email address")
                .build();

        NotificationSender sender = notificationSenderFactory.getSender(NotificationType.EMAIL);
        sender.send(message);
    }
}

