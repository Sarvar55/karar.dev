package org.karar.dev.domain.media.service;

import org.karar.dev.domain.media.dto.MediaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface MediaService {

    MediaResponse uploadMedia(MultipartFile file, String folder, UUID userId);

    void confirmMedia(UUID mediaId);

    void confirmPendingMediaByUrl(String mediaUrl, UUID userId);

}

