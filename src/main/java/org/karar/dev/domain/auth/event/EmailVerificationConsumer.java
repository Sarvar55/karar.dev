package org.karar.dev.domain.auth.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.notification.service.UserRegisteredDispatcher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationConsumer {

    private final UserRegisteredDispatcher userRegisteredDispatcher;

    @KafkaListener(topics = "email-verification", groupId = "karar-dev")
    public void consume(EmailVerificationEvent event) {
        log.info("Consumed email verification event for: {}", event.email());
        userRegisteredDispatcher.dispatch(event);
    }
}
