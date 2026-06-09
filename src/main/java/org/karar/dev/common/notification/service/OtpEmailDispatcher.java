package org.karar.dev.common.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.notification.dto.NotificationMessage;
import org.karar.dev.common.notification.enums.NotificationType;
import org.karar.dev.domain.auth.event.OtpEmailEvent;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpEmailDispatcher implements NotificationDispatcher {

    private final NotificationSenderFactory notificationSenderFactory;

    @Override
    public void dispatch(Object event) {
        log.info("Dispatching OTP email notification");
        OtpEmailEvent otpEvent = (OtpEmailEvent) event;

        NotificationMessage message = NotificationMessage.builder()
                .email(otpEvent.email())
                .templateName("otp-email.html")
                .variables(Map.of("email", otpEvent.email(), "otpCode", otpEvent.otpCode()))
                .message("Your login code for karar.dev")
                .build();

        NotificationSender sender = notificationSenderFactory.getSender(NotificationType.EMAIL);
        sender.send(message);
    }
}
