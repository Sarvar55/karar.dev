package org.karar.dev.common.exception.conflict;

import org.karar.dev.common.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}

