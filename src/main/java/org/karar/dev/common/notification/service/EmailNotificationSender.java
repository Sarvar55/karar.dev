package org.karar.dev.common.notification.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karar.dev.common.notification.dto.EmailTemplateModel;
import org.karar.dev.common.notification.dto.NotificationMessage;
import org.karar.dev.common.notification.enums.NotificationType;
import org.karar.dev.common.notification.mail.ThymeleafEmailTemplateEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationSender extends AbstractNotificationSender {

    private final ThymeleafEmailTemplateEngine emailTemplateEngine;
    private final JavaMailSender mailSender;

    @Override
    protected void doSend(NotificationMessage message, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(message.email());
            helper.setSubject(message.message());
            helper.setText(content, true);
            mailSender.send(mimeMessage);
            log.info("Email sent to {}", message.email());
        } catch (Exception ex) {
            log.warn("Failed to send email to {}", message.email(), ex);
            throw new RuntimeException("Failed to send email", ex);
        }
    }

    @Override
    protected String buildContent(NotificationMessage message) {
        EmailTemplateModel model = EmailTemplateModel.builder()
                .variables(Map.of("email", message.email(), "verificationUrl", message.verificationUrl()))
                .build();

        return emailTemplateEngine.render("verification-email.html", model);
    }

    @Override
    public NotificationType type() {
        return NotificationType.EMAIL;
    }
}

