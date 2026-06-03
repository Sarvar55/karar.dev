package org.karar.dev.domain.media;

import org.karar.dev.domain.media.entity.Media;
import org.karar.dev.domain.media.enums.MediaStatus;

import java.util.UUID;

public class MediaFixture {

    public static Media pendingMedia() {
        return media(MediaStatus.PENDING);
    }

    public static Media activeMedia() {
        return media(MediaStatus.ACTIVE);
    }

    public static Media media(MediaStatus status) {
        Media m = new Media();
        m.setId(UUID.randomUUID());
        m.setObjectName("avatars/photo.jpg");
        m.setBucketName("karar-bucket");
        m.setOriginalFilename("photo.jpg");
        m.setContentType("image/jpeg");
        m.setSize(1024L);
        m.setStatus(status);
        m.setUploadedByUserId(UUID.randomUUID());
        return m;
    }
}
