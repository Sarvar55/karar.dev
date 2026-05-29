package org.karar.dev.domain.media.minio.service;

import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.tika.Tika;
import org.karar.dev.domain.media.minio.MinioProperties;
import org.karar.dev.domain.media.minio.exception.StorageException;
import org.karar.dev.domain.media.service.AbstractStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MinioStorageService extends AbstractStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public MinioStorageService(Tika tika, MinioClient minioClient, MinioProperties minioProperties) {
        super(tika);
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @PostConstruct
    public void init() {

        try {

            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .build());

            if (!exists) {

                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioProperties.getBucketName())
                                .build());

                log.debug(
                        "Bucket created: {}",
                        minioProperties.getBucketName());
            }

        } catch (Exception e) {

            log.error(
                    "Failed to initialize MinIO bucket",
                    e);

            throw new StorageException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to initialize MinIO bucket");
        }
    }

    @Override
    public InputStream download(String objectName) {

        try {

            log.info(
                    "Downloading object: {}",
                    objectName);

            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build());

        } catch (Exception e) {

            log.error(
                    "Failed to download object: {}",
                    objectName,
                    e);

            throw new StorageException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to download object");
        }
    }

    @Override
    public boolean exists(String objectName) {

        try {

            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build());

            return true;

        } catch (ErrorResponseException e) {

            return false;

        } catch (Exception e) {

            log.warn(
                    "Failed to check object existence: {}",
                    objectName,
                    e);

            throw new StorageException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to check object existence");
        }
    }

    @Override
    public void delete(String objectName) {

        try {

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build());

            log.info(
                    "Deleted object: {}",
                    objectName);

        } catch (Exception e) {

            log.error(
                    "Failed to delete object: {}",
                    objectName,
                    e);

            throw new StorageException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete object");
        }
    }

    @Override
    public String upload(
            InputStream inputStream,
            String folder,
            String filename) {

        try {

            String objectName = generateObjectName(
                    folder,
                    filename);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .stream(
                                    inputStream,
                                    -1,
                                    10 * 1024 * 1024)
                            .contentType(
                                    resolveContentType(filename))
                            .build());

            log.info(
                    "Uploaded object: {}",
                    objectName);

            return objectName;

        } catch (Exception e) {

            log.error(
                    "Failed to upload object: {}",
                    filename,
                    e);

            throw new StorageException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to upload object");
        }
    }

    @Override
    public String getObjectUrl(
            String objectName,
            Long expireInSeconds) {

        return getPresignedUrl(
                minioProperties.getBucketName(),
                objectName,
                expireInSeconds);
    }

    @Override
    public String getPresignedUrl(String objectName) {

        return getPresignedUrl(
                minioProperties.getBucketName(),
                objectName,
                3600L);
    }

    @Override
    public String getPresignedUrl(
            String bucketName,
            String objectName,
            Long expireInSeconds) {

        try {

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expireInSeconds.intValue())
                            .build());

        } catch (Exception e) {

            log.error(
                    "Failed to generate presigned URL: {}",
                    objectName,
                    e);

            throw new StorageException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate presigned URL");
        }
    }

    private String generateObjectName(
            String folder,
            String filename) {

        String extension = FileNameUtils.getExtension(filename);

        return folder +
                "/" +
                UUID.randomUUID() +
                "." +
                extension;
    }

}