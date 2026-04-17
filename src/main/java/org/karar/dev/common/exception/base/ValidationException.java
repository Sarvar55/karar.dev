package org.karar.dev.common.exception.base;

import org.springframework.http.HttpStatus;
import java.util.Map;

public class ValidationException extends BaseException {
    public ValidationException(Map<String, String> errors) {
        super("Validation failed", HttpStatus.BAD_REQUEST, errors);
    }
}
