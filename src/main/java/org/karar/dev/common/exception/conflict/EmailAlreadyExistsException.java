package org.karar.dev.common.exception.conflict;

import org.karar.dev.common.exception.ExceptionMessages;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException(String email) {
        super(ExceptionMessages.EMAIL_ALREADY_EXISTS.format(email));
    }
}

