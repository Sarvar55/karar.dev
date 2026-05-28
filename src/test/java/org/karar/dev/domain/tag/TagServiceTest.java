package org.karar.dev.domain.tag;
import org.karar.dev.domain.tag.repository.TagRepository;
import org.karar.dev.domain.tag.service.TagService;
import org.karar.dev.domain.tag.entity.Tag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.conflict.ConflictException;
import org.karar.dev.common.exception.notfound.ResourceNotFoundException;
import org.karar.dev.domain.annotation.UnitTest;
import org.karar.dev.common.dto.BaseResponse;
import org.karar.dev.domain.extensions.TagServiceParameterResolver;
import org.karar.dev.domain.tag.dto.TagRequest;
import org.karar.dev.domain.tag.dto.TagResponse;
import org.karar.dev.domain.tag.dto.TagUpdateRequest;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@ExtendWith(TagServiceParameterResolver.class)
class TagServiceTest extends TagTestFixture {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;


    @Nested
    @DisplayName("getAll()")
    class GetAll {
        @Test
        @DisplayName("Should return all tags")
        void shouldReturnTagWhenTagsExists(Tag tag) {

            final UUID id = tag.getId();

            when(tagRepository.findAll())
                    .thenReturn(List.of(tag));

            BaseResponse<List<TagResponse>> response =
                    tagService.getAllTags();


            assertThat(response.getData())
                    .hasSize(1)
                    .first().extracting(TagResponse::id, TagResponse::name)
                    .containsExactly(id, tag.getName());

            verify(tagRepository).findAll();
        }
    }

    @Nested
    class GetTagById {
        @Test
        @DisplayName("Should return tag by ID successfully")
        void shouldReturnTagWhenTagExists() {
            Tag tag = TagMother.javaTag();

            when(tagRepository.findById(tag.getId()))
                    .thenReturn(Optional.of(tag));

            BaseResponse<TagResponse> response =
                    tagService.getTagById(tag.getId());

            assertThat(response.getData())
                    .extracting(TagResponse::id, TagResponse::name)
                    .containsExactly(tag.getId(), tag.getName());

            verify(tagRepository).findById(tag.getId());
        }

        @Test
        @DisplayName("Should throws ResourceNotFoundException when tag doesn't exists")
        void shouldThrowsResourceNotFoundExceptionWhenTagDoesntExists(Tag tag) {
            final UUID id = tag.getId();
            when(tagRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> tagService.getTagById(id))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(tagRepository).findById(id);
        }
    }

    @Nested
    class GetTagByName {
        @Test
        @DisplayName("Should return tag by name successfully")
        void shouldReturnTagWhenTagNameExists(Tag tag) {
            final String tagName = tag.getName();

            when(tagRepository.findByName(tagName))
                    .thenReturn(Optional.of(tag));

            BaseResponse<TagResponse> response =
                    tagService.getTagByName(tagName);

            assertThat(response.getData())
                    .extracting(TagResponse::id, TagResponse::name)
                    .containsExactly(tag.getId(), tag.getName());

            verify(tagRepository).findByName(tagName);
        }

        @Test
        @DisplayName("Should throws ResourceNotFoundException when tag doesn't exists")
        void shouldThrowsResourceNotFoundExceptionWhenTagDoesntExists() {
            final String tagName = "nonExistentTag";

            when(tagRepository.findByName(tagName))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> tagService.getTagByName(tagName))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(tagRepository).findByName(tagName);
        }
    }

    @Nested
    class CreateTag {
        @RepeatedTest(value = 10, name = RepeatedTest.LONG_DISPLAY_NAME)
        @DisplayName("Should return created tag successfully")
        void shouldCreateTagWhenValidRequest() {

            final String tagName = "java";
            Tag savedTag = TagMother.javaTag();

            TagRequest request = new TagRequest(tagName);

            when(tagRepository.existsByName(tagName))
                    .thenReturn(false);

            when(tagRepository.saveAndFlush(any()))
                    .thenReturn(savedTag);

            // Act
            BaseResponse<TagResponse> response = tagService.createTag(request);

            // Assert
            assertThat(response.getData().name()).isEqualTo(tagName);
            assertThat(response.getData().id()).isEqualTo(savedTag.getId());

            // Verify behavior
            ArgumentCaptor<Tag> tagCaptor = ArgumentCaptor.forClass(Tag.class);
            verify(tagRepository).saveAndFlush(tagCaptor.capture());

            Tag capturedTag = tagCaptor.getValue();
            assertThat(capturedTag.getName()).isEqualTo(tagName);
        }

        @Test
        @DisplayName("Should throws ConflictException when tag already exists")
        void shouldThrowsConflictExceptionWhenTagAlreadyExists() {
            final String tagName = "java";

            TagRequest request = new TagRequest(tagName);

            when(tagRepository.existsByName(tagName))
                    .thenReturn(true);

            assertThatThrownBy(() -> tagService.createTag(request))
                    .isInstanceOf(ConflictException.class);

            verify(tagRepository).existsByName(anyString());
            verify(tagRepository, never()).saveAndFlush(any(Tag.class));

        }
    }

    @Nested
    class UpdateTag {

        @Test
        @DisplayName("Should return updated tag successfully")
        void shouldUpdateTagWhenValidRequest(Tag tag) {
            final String tagName = "java";
            final UUID id = tag.getId();

            TagUpdateRequest updateRequest = new TagUpdateRequest(tagName);

            when(tagRepository.findById(id))
                    .thenReturn(Optional.of(tag));

            when(tagRepository.saveAndFlush(any(Tag.class)))
                    .thenReturn(tag);

            BaseResponse<TagResponse> response = tagService.updateTag(id, updateRequest);

            assertThat(response).isNotNull();
            assertThat(response.getData().id()).isEqualTo(id);
            assertThat(response.getData().name()).isEqualTo(tagName);

            verify(tagRepository).findById(any());
            verify(tagRepository).saveAndFlush(any(Tag.class));
        }

        @Test
        @DisplayName("Should throws ConflictException when tag already exists")
        void shouldThrowsConflictExceptionWhenTagAlreadyExists(Tag existingTag) {
            UUID id = existingTag.getId();
            String newTagName = "spring";

            TagUpdateRequest request = new TagUpdateRequest(newTagName);

            when(tagRepository.findById(id))
                    .thenReturn(Optional.of(existingTag));

            when(tagRepository.existsByNameAndIdNot(newTagName, id))
                    .thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> tagService.updateTag(id, request))
                    .isInstanceOf(ConflictException.class);

            // Verify
            verify(tagRepository).existsByNameAndIdNot(newTagName, id);
            verify(tagRepository, never()).saveAndFlush(any(Tag.class));
        }
    }

    @Nested
    class DeleteTag {
        @Test
        @DisplayName("Should delete tag successfully")
        void shouldDeleteTagWhenTagExists(Tag tag) {
            final UUID id = tag.getId();

            when(tagRepository.existsById(id))
                    .thenReturn(true);

            BaseResponse<Void> response = tagService.deleteTag(id);

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(tagRepository).deleteById(id);
        }

        @Test
        @DisplayName("Should throws ResourceNotFoundException when tag doesn't exists")
        void shouldThrowsResourceNotFoundExceptionWhenTagDoesntExists(Tag tag) {
            final UUID id = tag.getId();

            when(tagRepository.existsById(id))
                    .thenReturn(false);

            assertThatThrownBy(() -> tagService.deleteTag(id))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(tagRepository).existsById(id);
            verify(tagRepository, never()).deleteById(id);
        }
    }

    @Nested
    class ExistsById {

        @Test
        @DisplayName("Should return true when tag exists")
        void shouldReturnTrueWhenTagExists() {
            final UUID id = id();
            when(tagRepository.existsById(id))
                    .thenReturn(true);

            boolean result = tagService.existsById(id);

            assertThat(result).isTrue();
            verify(tagRepository).existsById(id);
        }
    }

    @Nested
    class GetById {
        @Test
        @DisplayName("Should return tag when found")
        void shouldReturnTagWhenFound(Tag tag) {
            final UUID id = tag.getId();

            when(tagRepository.findById(id))
                    .thenReturn(Optional.of(tag));

            Tag response = tagService.getById(id);
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(id);
            assertThat(response.getName()).isEqualTo(tag.getName());
            assertThat(response.getCreatedAt()).isEqualTo(tag.getCreatedAt());
            verify(tagRepository).findById(id);
        }

        @Test
        @DisplayName("Should throws ResourceNotFoundException when tag doesn't exists")
        void shouldThrowsResourceNotFoundExceptionWhenTagDoesntExists() {
            final UUID id = id();
            when(tagRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> tagService.getById(id))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(tagRepository).findById(id);
        }
    }
}