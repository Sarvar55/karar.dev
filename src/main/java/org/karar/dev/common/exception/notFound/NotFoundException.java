package org.karar.dev.common.exception.notFound;

import org.karar.dev.common.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
