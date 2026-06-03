package org.karar.dev.common.notification.mail;

import lombok.RequiredArgsConstructor;
import org.karar.dev.common.notification.dto.EmailTemplateModel;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class ThymeleafEmailTemplateEngine implements EmailTemplateEngine {

    private final SpringTemplateEngine templateEngine;

    @Override
    public String render(String templateName, EmailTemplateModel model) {
        Context context = new Context();

        model.variables().forEach(context::setVariable);

        return templateEngine.process(
                templateName,
                context
        );
    }

}

