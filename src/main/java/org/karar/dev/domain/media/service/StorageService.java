package org.karar.dev.domain.media.service;

import org.karar.dev.domain.media.minio.exception.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService extends StorageReadOperations, StorageWriteOperations, StorageUrlOperations {

    default String uploadMultipartFile(MultipartFile file, String folder) {
        try {
            return upload(file.getInputStream(), folder, file.getOriginalFilename());
        } catch (Exception e) {
            throw new FileUploadException(e.getMessage());
        }
    }
}

