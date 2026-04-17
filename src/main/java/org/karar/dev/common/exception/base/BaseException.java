package org.karar.dev.common.exception.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final Map<String, String> validationErrors;

    protected BaseException(String message, HttpStatus status) {
        this(message, status, null);
    }

    protected BaseException(String message, HttpStatus status, Map<String, String> validationErrors) {
        super(message);
        this.status = status;
        this.validationErrors = validationErrors;
    }
}
