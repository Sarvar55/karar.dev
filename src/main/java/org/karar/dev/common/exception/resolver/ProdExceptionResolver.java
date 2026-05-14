package org.karar.dev.common.exception.resolver;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class ProdExceptionResolver implements ExceptionMessageResolver {
    private static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred";

    @Override
    public String resolve(Exception e) {
        return DEFAULT_ERROR_MESSAGE;
    }
}
