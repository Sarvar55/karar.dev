package org.karar.dev.common.exception.conflict;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email already exists: '%s'", email));
    }
}
