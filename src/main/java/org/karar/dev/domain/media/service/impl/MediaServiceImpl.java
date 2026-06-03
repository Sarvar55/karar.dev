package org.karar.dev.domain.media.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.karar.dev.common.exception.notfound.ResourceNotFoundException;
import org.karar.dev.domain.media.dto.MediaResponse;
import org.karar.dev.domain.media.entity.Media;
import org.karar.dev.domain.media.enums.MediaStatus;
import org.karar.dev.domain.media.minio.MinioProperties;
import org.karar.dev.domain.media.repository.MediaRepository;
import org.karar.dev.domain.media.service.ContentTypeResolver;
import org.karar.dev.domain.media.service.MediaService;
import org.karar.dev.domain.media.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final StorageService storageService;
    private final MediaRepository mediaRepository;
    private final MinioProperties minioProperties;
    private final ContentTypeResolver contentTypeResolver;

    @Override
    @Transactional
    public MediaResponse uploadMedia(MultipartFile file, String folder, UUID userId) {

        String objectName = storageService.uploadMultipartFile(file, folder);

        String contentType = contentTypeResolver.resolve(file.getOriginalFilename());

        Media media = Media.builder()
                .objectName(objectName)
                .bucketName(minioProperties.getBucketName())
                .originalFilename(file.getOriginalFilename())
                .contentType(contentType)
                .size(file.getSize())
                .status(MediaStatus.PENDING)
                .uploadedByUserId(userId)
                .build();

        media = mediaRepository.save(media);

        String presignedUrl = storageService.getObjectUrl(objectName, 3600L);


        return MediaResponse.builder()
                .id(media.getId())
                .url(presignedUrl)
                .filename(file.getOriginalFilename())
                .contentType(contentType)
                .size(file.getSize())
                .build();
    }

    @Override
    @Transactional
    public void confirmMedia(UUID mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + mediaId));

        media.setStatus(MediaStatus.ACTIVE);
        mediaRepository.save(media);
        log.info("Media {} confirmed and set to ACTIVE", mediaId);
    }

    @Override
    @Transactional
    public void confirmPendingMediaByUrl(String mediaUrl, UUID userId) {
        if (mediaUrl == null || mediaUrl.isBlank()) {
            return;
        }

        String objectName = extractObjectName(mediaUrl);
        mediaRepository.findByStatusAndUploadedByUserIdAndObjectName(MediaStatus.PENDING, userId, objectName)
                .ifPresentOrElse(media -> {
                    media.setStatus(MediaStatus.ACTIVE);
                    mediaRepository.save(media);
                    log.info("Pending media {} confirmed for user {}", media.getId(), userId);
                }, () -> log.debug("No pending media found for user {} and object {}", userId, objectName));
    }

    private String extractObjectName(String mediaUrl) {
        String objectName = mediaUrl;

        try {
            URI uri = URI.create(mediaUrl);
            String path = uri.getPath();
            if (path != null && !path.isBlank()) {
                objectName = path.startsWith("/") ? path.substring(1) : path;

                String bucketPrefix = minioProperties.getBucketName() + "/";
                if (objectName.startsWith(bucketPrefix)) {
                    objectName = objectName.substring(bucketPrefix.length());
                }
            }
        } catch (IllegalArgumentException ex) {
            log.debug("Media URL is not a valid URI, using it as object name: {}", mediaUrl);
        }

        return URLDecoder.decode(objectName, StandardCharsets.UTF_8);
    }
}
