package org.karar.dev.domain.auth.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationProducer {

    private static final String TOPIC = "email-verification";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(EmailVerificationEvent event) {
        log.info("Producing email verification event for: {}", event.email());
        kafkaTemplate.send(TOPIC, event.email(), event);
    }
}
