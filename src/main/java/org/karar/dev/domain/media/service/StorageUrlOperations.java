package org.karar.dev.domain.media.service;

public interface StorageUrlOperations {
    String getPresignedUrl(String objectName);

    String getPresignedUrl(String bucketName, String objectName, Long expireInSeconds);

    String getObjectUrl(String objectName, Long expireInSeconds);

}
