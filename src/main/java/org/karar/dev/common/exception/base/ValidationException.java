package org.karar.dev.common.exception.base;

import org.karar.dev.common.exception.ExceptionMessages;
import org.springframework.http.HttpStatus;
import java.util.Map;

public class ValidationException extends BaseException {
    public ValidationException(Map<String, String> errors) {
        super(ExceptionMessages.VALIDATION_FAILED.getMessage(), HttpStatus.BAD_REQUEST, errors);
    }
}

