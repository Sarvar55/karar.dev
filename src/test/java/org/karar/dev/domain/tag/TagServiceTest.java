package org.karar.dev.domain.tag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.conflict.ConflictException;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.tag.dto.TagRequest;
import org.karar.dev.domain.tag.dto.TagResponse;
import org.karar.dev.domain.tag.dto.TagUpdateRequest;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private static final String TAG_NAME = "tag";

    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
    }

    private Tag createMockTag() {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(TAG_NAME);
        return tag;
    }

    @Nested
    class GetAllTags {
        @Test
        @DisplayName("Should return all tags")
        void getAllTagsShouldReturnAllTags() {
            Tag tag = createMockTag();
            when(tagRepository.findAll())
                    .thenReturn(List.of(tag));

            BaseResponse<List<TagResponse>> response =
                    tagService.getAllTags();

            assertNotNull(response);
            assertNotNull(response.getData());
            assertEquals(1, response.getData().size());
            assertEquals(id, response.getData().get(0).id());
            assertEquals(TAG_NAME, response.getData().get(0).name());

            verify(tagRepository).findAll();
        }
    }

    @Nested
    class GetTagById {
        @Test
        @DisplayName("Should return tag by ID successfully")
        void getTagByIdShouldReturnTagWhenFound() {
            Tag tag = createMockTag();
            when(tagRepository.findById(id))
                    .thenReturn(Optional.of(tag));

            BaseResponse<TagResponse> response =
                    tagService.getTagById(id);

            assertNotNull(response);
            assertNotNull(response.getData());
            assertEquals(id, response.getData().id());
            assertEquals(TAG_NAME, response.getData().name());

            verify(tagRepository).findById(id);
        }

        @Test
        @DisplayName("Should throws ResourceNotFoundException when tag doesn't exists")
        void getTagByIdShouldThrowsResourceNotFoundExceptionWhenTagDoesntExists() {
            Tag tag = createMockTag();
            when(tagRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> tagService.getTagById(id));

            verify(tagRepository).findById(id);
        }
    }

    @Nested
    class GetTagByName {
        @Test
        @DisplayName("Should return tag by name successfully")
        void getTagByNameShouldReturnTagWhenFound() {
            Tag tag = createMockTag();
            when(tagRepository.findByName(TAG_NAME))
                    .thenReturn(Optional.of(tag));

            BaseResponse<TagResponse> response =
                    tagService.getTagByName(TAG_NAME);

            assertNotNull(response);
            assertNotNull(response.getData());
            assertEquals(id, response.getData().id());
            assertEquals(TAG_NAME, response.getData().name());
            verify(tagRepository).findByName(TAG_NAME);
        }

        @Test
        @DisplayName("Should throws ResourceNotFoundException when tag doesn't exists")
        void getTagByNameShouldThrowsResourceNotFoundExceptionWhenTagDoesntExists() {
            when(tagRepository.findByName(TAG_NAME))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> tagService.getTagByName(TAG_NAME));

            verify(tagRepository).findByName(TAG_NAME);
        }
    }

    @Nested
    class CreateTag {
        @Test
        @DisplayName("Should return created tag successfully")
        void shouldCreateTagSuccessfully() {
            // Arrange
            TagRequest request = new TagRequest(TAG_NAME);

            Tag savedTag = new Tag();
            savedTag.setId(id);
            savedTag.setName(TAG_NAME);

            when(tagRepository.saveAndFlush(any(Tag.class)))
                    .thenReturn(savedTag);

            // Act
            BaseResponse<TagResponse> response = tagService.createTag(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getData()).isNotNull();
            assertThat(response.getData().id()).isEqualTo(id);
            assertThat(response.getData().name()).isEqualTo(TAG_NAME);

            // Verify behavior
            ArgumentCaptor<Tag> tagCaptor = ArgumentCaptor.forClass(Tag.class);
            verify(tagRepository).saveAndFlush(tagCaptor.capture());

            Tag capturedTag = tagCaptor.getValue();
            assertThat(capturedTag.getName()).isEqualTo(TAG_NAME);
        }

        @Test
        @DisplayName("Should throws ConflictException when tag already exists")
        void createTagShouldThrowsConflictExceptionWhenTagAlreadyExists() {
            TagRequest request = new TagRequest(TAG_NAME);
            when(tagRepository.existsByName(TAG_NAME))
                    .thenReturn(true);

            assertThrows(ConflictException.class, () -> tagService.createTag(request));
            verify(tagRepository).existsByName(TAG_NAME);
            verify(tagRepository, never()).saveAndFlush(any(Tag.class));

        }
    }

    @Nested
    class UpdateTag {

        @Test
        @DisplayName("Should return updated tag successfully")
        void updateTagShouldReturnUpdatedTagSuccessfully() {
            TagUpdateRequest updateRequest = new TagUpdateRequest(TAG_NAME);
            Tag tag = createMockTag();
            when(tagRepository.findById(id))
                    .thenReturn(Optional.of(tag));

            when(tagRepository.saveAndFlush(any(Tag.class)))
                    .thenReturn(tag);

            BaseResponse<TagResponse> response = tagService.updateTag(id, updateRequest);

            assertThat(response).isNotNull();
            assertThat(response.getData().id()).isEqualTo(id);
            assertThat(response.getData().name()).isEqualTo(TAG_NAME);
            verify(tagRepository).findById(id);
            verify(tagRepository).saveAndFlush(any(Tag.class));
        }

        @Test
        @DisplayName("Should throws ConflictException when tag already exists")
        void updateTagShouldThrowsConflictExceptionWhenTagAlreadyExists() {
            TagUpdateRequest updateRequest = new TagUpdateRequest(TAG_NAME);
            Tag tag = createMockTag();
            when(tagRepository.findById(id))
                    .thenReturn(Optional.of(tag));

            when(tagRepository.existsByNameAndIdNot(tag.getName(), id))
                    .thenReturn(true);


            assertThrows(ConflictException.class, () -> tagService.updateTag(id, updateRequest));
            verify(tagRepository).existsByNameAndIdNot(TAG_NAME, id);
            verify(tagRepository, never()).saveAndFlush(any(Tag.class));
        }
    }

    @Nested
    class DeleteTag {
        @Test
        @DisplayName("Should delete tag successfully")
        void deleteTagShouldDeleteTagWhenFound() {
            Tag tag = createMockTag();
            when(tagRepository.existsById(id))
                    .thenReturn(true);

            BaseResponse<Void> response = tagService.deleteTag(id);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(tagRepository).deleteById(id);
        }

        @Test
        @DisplayName("Should throws ResourceNotFoundException when tag doesn't exists")
        void deleteTagShouldThroesResourceNotFoundExceptionWhenTagDoestNotExists() {
            when(tagRepository.existsById(id))
                    .thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> tagService.deleteTag(id));
            verify(tagRepository).existsById(id);
            verify(tagRepository, never()).deleteById(id);
        }
    }

    @Nested
    class ExistsById {

        @Test
        @DisplayName("Should return true when tag exists")
        void existsByIdShouldReturnTrueWhenTagExists() {
            when(tagRepository.existsById(id)).thenReturn(true);

            boolean result = tagService.existsById(id);
            assertThat(result).isTrue();
            verify(tagRepository).existsById(id);
        }
    }

    @Nested
    class GetById {
        @Test
        @DisplayName("Should return tag when found")
        void getByIdShouldReturnTagWhenFound() {
            Tag tag = createMockTag();
            when(tagRepository.findById(id))
                    .thenReturn(Optional.of(tag));

            Tag tagFromDb = tagService.getById(id);
            assertThat(tagFromDb).isNotNull();
            assertThat(tagFromDb.getId()).isEqualTo(id);
            assertThat(tagFromDb.getName()).isEqualTo(TAG_NAME);
            verify(tagRepository).findById(id);
        }

        @Test
        @DisplayName("Should throws ResourceNotFoundException when tag doesn't exists")
        void getByIdShouldThrowsResourceNotFoundExceptionWhenTagDoesntExists() {
            when(tagRepository.findById(id))
                    .thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> tagService.getById(id));
            verify(tagRepository).findById(id);
        }
    }
}