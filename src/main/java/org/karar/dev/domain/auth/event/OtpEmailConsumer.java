package org.karar.dev.domain.auth.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.notification.service.OtpEmailDispatcher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpEmailConsumer {

    private final OtpEmailDispatcher otpEmailDispatcher;

    @KafkaListener(topics = "otp-email", groupId = "karar-dev")
    public void consume(OtpEmailEvent event) {
        log.info("Consumed OTP email event for: {}", event.email());
        otpEmailDispatcher.dispatch(event);
    }
}
