package org.karar.dev.common.exception.notFound;

import org.karar.dev.common.exception.ExceptionMessages;

public class ResourceNotFoundException extends NotFoundException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(ExceptionMessages.RESOURCE_NOT_FOUND.format(resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
