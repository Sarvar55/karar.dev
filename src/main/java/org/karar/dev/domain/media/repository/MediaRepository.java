package org.karar.dev.domain.media.repository;

import org.karar.dev.domain.media.entity.Media;
import org.karar.dev.domain.media.enums.MediaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {
    
    List<Media> findByStatusAndCreatedAtBefore(MediaStatus status, LocalDateTime date);

    Optional<Media> findByStatusAndUploadedByUserIdAndObjectName(
            MediaStatus status,
            UUID uploadedByUserId,
            String objectName);
    
}
