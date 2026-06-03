package org.karar.dev.domain.media;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.notfound.ResourceNotFoundException;
import org.karar.dev.domain.annotation.UnitTest;
import org.karar.dev.domain.extensions.MediaParameterResolver;
import org.karar.dev.domain.media.dto.MediaResponse;
import org.karar.dev.domain.media.entity.Media;
import org.karar.dev.domain.media.enums.MediaStatus;
import org.karar.dev.domain.media.minio.MinioProperties;
import org.karar.dev.domain.media.repository.MediaRepository;
import org.karar.dev.domain.media.service.ContentTypeResolver;
import org.karar.dev.domain.media.service.StorageService;
import org.karar.dev.domain.media.service.impl.MediaServiceImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@UnitTest
@ExtendWith(MediaParameterResolver.class)
class MediaServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private MinioProperties minioProperties;

    @Mock
    private ContentTypeResolver contentTypeResolver;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private MediaServiceImpl mediaService;


    @Nested
    @DisplayName("uploadMedia()")
    class UploadMedia {

        @Test
        @DisplayName("Should upload file, save Media entity with PENDING status and return MediaResponse")
        void shouldUploadAndReturnResponse(Media media) {
            UUID userId = media.getUploadedByUserId();
            String folder = "images";
            String presignedUrl = "https://minio.local/karar-bucket/avatars/photo.jpg?X-Amz-Signature=abc";

            when(multipartFile.getOriginalFilename())
                    .thenReturn(media.getOriginalFilename());

            when(multipartFile.getSize())
                    .thenReturn(media.getSize());

            when(storageService.uploadMultipartFile(multipartFile, folder))
                    .thenReturn(media.getObjectName());

            when(contentTypeResolver.resolve(multipartFile.getOriginalFilename()))
                    .thenReturn(media.getContentType());

            when(mediaRepository.save(any(Media.class)))
                    .thenReturn(media);

            when(storageService.getObjectUrl(media.getObjectName(), 3600L))
                    .thenReturn(presignedUrl);

            MediaResponse response = mediaService.uploadMedia(multipartFile, folder, userId);

            assertThat(response.getId()).isEqualTo(media.getId());
            assertThat(response.getUrl()).isEqualTo(presignedUrl);
            assertThat(response.getFilename()).isEqualTo(media.getOriginalFilename());
            assertThat(response.getContentType()).isEqualTo(media.getContentType());
            assertThat(response.getSize()).isEqualTo(media.getSize());


            ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
            verify(mediaRepository).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(MediaStatus.PENDING);
            assertThat(captor.getValue().getUploadedByUserId()).isEqualTo(userId);

            verify(storageService).uploadMultipartFile(multipartFile, folder);
            verify(contentTypeResolver).resolve(media.getOriginalFilename());
            verify(storageService).getObjectUrl(media.getObjectName(), 3600L);
        }

        @Test
        @DisplayName("Should use ContentTypeResolver to determine content type")
        void shouldResolveContentTypeViaResolver(Media media) {
            when(multipartFile.getOriginalFilename())
                    .thenReturn(media.getOriginalFilename());

            when(multipartFile.getSize())
                    .thenReturn(media.getSize());

            when(storageService.uploadMultipartFile(any(), any()))
                    .thenReturn(media.getObjectName());

            when(contentTypeResolver.resolve(media.getOriginalFilename()))
                    .thenReturn(media.getContentType());

            when(minioProperties.getBucketName())
                    .thenReturn(media.getBucketName());

            when(mediaRepository.save(any()))
                    .thenReturn(media);

            when(storageService.getObjectUrl(any(), anyLong()))
                    .thenReturn("https://minio.local/url");

            MediaResponse response = mediaService
                    .uploadMedia(multipartFile, "docs", media.getUploadedByUserId());

            assertThat(response.getContentType()).isEqualTo(media.getContentType());
            verify(contentTypeResolver).resolve(media.getOriginalFilename());
        }
    }


    @Nested
    @DisplayName("confirmMedia()")
    class ConfirmMedia {

        @Test
        @DisplayName("Should set status to ACTIVE when media exists")
        void shouldSetStatusToActiveWhenMediaFound(Media media) {
            when(mediaRepository.findById(media.getId()))
                    .thenReturn(Optional.of(media));

            mediaService.confirmMedia(media.getId());

            assertThat(media.getStatus()).isEqualTo(MediaStatus.ACTIVE);
            verify(mediaRepository).findById(media.getId());
            verify(mediaRepository).save(media);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when media does not exist")
        void shouldThrowWhenMediaNotFound() {
            UUID mediaId = UUID.randomUUID();
            when(mediaRepository.findById(mediaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mediaService.confirmMedia(mediaId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(mediaId.toString());

            verify(mediaRepository).findById(mediaId);
            verify(mediaRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("confirmPendingMediaByUrl()")
    class ConfirmPendingMediaByUrl {

        @Test
        @DisplayName("Should confirm pending media and set it to ACTIVE when found")
        void shouldConfirmMediaWhenFound(Media media) {

            String mediaUrl = "http://minio.local/" + media.getBucketName() + "/" + media.getObjectName();

            when(minioProperties.getBucketName())
                    .thenReturn(media.getBucketName());

            when(mediaRepository.findByStatusAndUploadedByUserIdAndObjectName(
                    MediaStatus.PENDING, media.getUploadedByUserId(), media.getObjectName()))
                    .thenReturn(Optional.of(media));

            mediaService.confirmPendingMediaByUrl(mediaUrl, media.getUploadedByUserId());

            assertThat(media.getStatus()).isEqualTo(MediaStatus.ACTIVE);
            verify(mediaRepository).save(media);
        }

        @Test
        @DisplayName("Should do nothing when no pending media found for URL")
        void shouldDoNothingWhenNoPendingMediaFound(Media media) {
            String mediaUrl = "http://minio.local/" + media.getBucketName() + "/" + media.getObjectName();

            when(minioProperties.getBucketName()).thenReturn(media.getBucketName());
            when(mediaRepository.findByStatusAndUploadedByUserIdAndObjectName(
                    MediaStatus.PENDING, media.getUploadedByUserId(), media.getObjectName()))
                    .thenReturn(Optional.empty());

            mediaService.confirmPendingMediaByUrl(mediaUrl, media.getUploadedByUserId());

            verify(mediaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return immediately when mediaUrl is null")
        void shouldReturnImmediatelyWhenUrlIsNull() {
            mediaService.confirmPendingMediaByUrl(null, UUID.randomUUID());

            verifyNoInteractions(mediaRepository);
            verifyNoInteractions(minioProperties);
        }

        @Test
        @DisplayName("Should return immediately when mediaUrl is blank")
        void shouldReturnImmediatelyWhenUrlIsBlank() {
            mediaService.confirmPendingMediaByUrl("   ", UUID.randomUUID());

            verifyNoInteractions(mediaRepository);
            verifyNoInteractions(minioProperties);
        }

        @Test
        @DisplayName("Should handle plain object name (non-URI) without throwing")
        void shouldHandlePlainObjectNameGracefully(Media media) {

            String rawObjectName = media.getObjectName();

            when(minioProperties.getBucketName()).thenReturn(media.getBucketName());

            when(mediaRepository.findByStatusAndUploadedByUserIdAndObjectName(
                    MediaStatus.PENDING, media.getUploadedByUserId(), rawObjectName))
                    .thenReturn(Optional.empty());

            mediaService.confirmPendingMediaByUrl(rawObjectName, media.getUploadedByUserId());

            verify(mediaRepository).findByStatusAndUploadedByUserIdAndObjectName(
                    MediaStatus.PENDING, media.getUploadedByUserId(), rawObjectName);
        }
    }
}
