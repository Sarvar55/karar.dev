package org.karar.dev.common.notification.mail;

import org.karar.dev.common.notification.dto.EmailTemplateModel;

public interface EmailTemplateEngine {
    String render(String templateName, EmailTemplateModel model);
}
