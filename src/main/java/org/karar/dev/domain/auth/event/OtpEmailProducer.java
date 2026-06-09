package org.karar.dev.domain.auth.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpEmailProducer {

    private static final String TOPIC = "otp-email";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(OtpEmailEvent event) {
        log.info("Producing OTP email event for: {}", event.email());
        kafkaTemplate.send(TOPIC, event.email(), event);
    }
}
