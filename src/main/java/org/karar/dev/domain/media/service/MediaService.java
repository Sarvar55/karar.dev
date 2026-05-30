package org.karar.dev.domain.media.service;

import org.karar.dev.domain.media.dto.MediaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface MediaService {

    /**
     * Uploads a file, saves it to MinIO, and creates a PENDING Media entity.
     * 
     * @param file   The file to upload
     * @param folder The folder in MinIO (e.g. "avatars", "attachments")
     * @param userId The ID of the user uploading the file
     * @return MediaResponse containing the ID and temporary presigned URL
     */
    MediaResponse uploadMedia(MultipartFile file, String folder, UUID userId);

    /**
     * Marks a media file as ACTIVE (meaning it's now officially in use by an
     * entity).
     * 
     * @param mediaId The UUID of the media
     */
    void confirmMedia(UUID mediaId);

    void confirmPendingMediaByUrl(String mediaUrl, UUID userId);

}
