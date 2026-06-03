package org.karar.dev.common.exception.resolver;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevExceptionResolver implements ExceptionMessageResolver {
    @Override
    public String resolve(Exception e) {
        return e.getMessage();
    }
}

