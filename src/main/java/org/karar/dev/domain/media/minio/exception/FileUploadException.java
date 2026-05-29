package org.karar.dev.domain.media.minio.exception;

import org.springframework.http.HttpStatus;

public class FileUploadException extends StorageException {

    public FileUploadException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

}
