package org.karar.dev.domain.media.minio.exception;

import org.springframework.http.HttpStatus;

import org.karar.dev.common.exception.base.BaseException;

public class StorageException extends BaseException {

    public StorageException(HttpStatus status, String message) {
        super(message, status);
    }

}
