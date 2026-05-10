package org.karar.dev.domain.tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.karar.dev.common.exception.conflict.ConflictException;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.tag.dto.TagRequest;
import org.karar.dev.domain.tag.dto.TagResponse;
import org.karar.dev.domain.tag.dto.TagUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public BaseResponse<List<TagResponse>> getAllTags() {
        log.debug("Getting all tags");
        List<Tag> tags = tagRepository.findAll();
        List<TagResponse> responseList = tags.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.debug("Tags retrieved successfully: {}", responseList);
        return BaseResponse.success(responseList);
    }

    @Transactional(readOnly = true)
    public BaseResponse<TagResponse> getTagById(UUID id) {
        log.debug("Getting tag by id: {}", id);
        Tag tag = findTagOrThrow(id);
        log.debug("Tag retrieved successfully: {}", tag);
        return BaseResponse.success(mapToResponse(tag));
    }

    @Transactional(readOnly = true)
    public BaseResponse<TagResponse> getTagByName(String name) {
        log.debug("Getting tag by name: {}", name);
        Tag tag = tagRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "name", name));
        return BaseResponse.success(mapToResponse(tag));
    }

    @Transactional
    public BaseResponse<TagResponse> createTag(TagRequest request) {
        log.info("Creating tag: {}", request);
        if (tagRepository.existsByName(request.name().toLowerCase().trim())) {
            log.warn("Tag with name '{}' already exists", request.name());
            throw new ConflictException("Tag with name '" + request.name() + "' already exists");
        }

        Tag tag = new Tag();
        tag.setName(request.name().toLowerCase().trim());

        Tag savedTag = tagRepository.saveAndFlush(tag);
        log.debug("Tag created successfully: {}", savedTag);
        return BaseResponse.success(mapToResponse(savedTag), HttpStatus.CREATED);
    }

    @Transactional
    public BaseResponse<TagResponse> updateTag(UUID id, TagUpdateRequest request) {
        log.debug("Updating tag: {}, {}", id, request);
        Tag tag = findTagOrThrow(id);

        // Check if another tag with the same name exists
        if (tagRepository.existsByNameAndIdNot(request.name(), id)) {
            log.warn("Tag with name '{}' already exists", request.name());
            throw new ConflictException("Tag with name '" + request.name() + "' already exists");
        }

        tag.setName(request.name().toLowerCase().trim());

        Tag updatedTag = tagRepository.saveAndFlush(tag);
        log.debug("Tag updated successfully: {}", updatedTag);
        return BaseResponse.success(mapToResponse(updatedTag));
    }

    @Transactional
    public BaseResponse<Void> deleteTag(UUID id) {
        log.debug("Deleting tag: {}", id);
        if (!tagRepository.existsById(id)) {
            log.warn("Tag not found: {}", id);
            throw new ResourceNotFoundException("Tag", "id", id);
        }
        tagRepository.deleteById(id);
        log.debug("Tag deleted successfully: {}", id);
        return BaseResponse.success(null, HttpStatus.NO_CONTENT);
    }

    public boolean existsById(UUID id) {
        log.debug("Checking if tag exists by id: {}", id);
        return tagRepository.existsById(id);
    }

    public Tag getById(UUID id) {
        log.debug("Getting tag by id: {}", id);
        return findTagOrThrow(id);
    }

    private Tag findTagOrThrow(UUID id) {
        log.debug("Finding tag by id: {}", id);
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
    }

    private TagResponse mapToResponse(Tag tag) {

        return new TagResponse(
                tag.getId(),
                tag.getName(),
                tag.getDecisionTags() != null ? tag.getDecisionTags().size() : 0,
                tag.getCreatedAt(),
                tag.getUpdatedAt());
    }
}
