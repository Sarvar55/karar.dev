package org.karar.dev.domain.media.service;

import org.apache.tika.Tika;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for all storage implementations (Minio, Local, AWS S3, etc.).
 * Provides shared utilities like content-type detection.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractStorageService implements StorageService {

    protected final Tika tika;

    protected String resolveContentType(String filename) {
        try {
            String detectedType = tika.detect(filename);
            if (detectedType != null) {
                return detectedType;
            }
        } catch (Exception e) {
            log.warn("Failed to detect content type for file: {}", filename, e);
        }

        return "application/octet-stream";
    }
}
