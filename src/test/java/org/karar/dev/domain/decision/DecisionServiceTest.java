package org.karar.dev.domain.decision;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.karar.dev.common.enums.RegretLevel;
import org.karar.dev.common.exception.conflict.ConflictException;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.annotation.UnitTest;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.decision.dto.DecisionRequest;
import org.karar.dev.domain.decision.dto.DecisionResponse;
import org.karar.dev.domain.decision.dto.DecisionUpdateRequest;
import org.karar.dev.domain.decisiontag.DecisionTagService;
import org.karar.dev.domain.extensions.DecisionParameterResolver;
import org.karar.dev.domain.tag.Tag;
import org.karar.dev.domain.tag.TagBuilder;
import org.karar.dev.domain.tag.TagService;
import org.karar.dev.domain.user.regular.RegularUserBuilder;
import org.karar.dev.domain.user.regular.RegularUserService;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@UnitTest
@ExtendWith(DecisionParameterResolver.class)
class DecisionServiceTest {

    @Mock
    private DecisionRepository decisionRepository;

    @Mock
    private RegularUserService regularUserService;

    @Mock
    TagService tagService;

    @Mock
    DecisionTagService decisionTagService;

    @InjectMocks
    private DecisionService decisionService;


    @Nested
    @DisplayName("Get Decisions")
    class GetDecisions {

        @Test
        @DisplayName("Should return all decisions with pagination")
        void shouldReturnPaginatedDecisionsWhenExists(Decision decision) {
            Pageable pageable = PageRequest.of(0, 1);

            when(decisionRepository.findAll(pageable))
                    .thenReturn(new PageImpl<>(List.of(decision), pageable, 1));

            var response = decisionService.getAllDecisions(pageable);

            assertThat(response.getData().getContent())
                    .hasSize(1)
                    .first()
                    .extracting(DecisionResponse::id, DecisionResponse::userId, DecisionResponse::title)
                    .containsExactly(decision.getId(), decision.getUser().getId(), decision.getTitle());

            assertThat(response.getData().getTotalElements()).isEqualTo(1);

            verify(decisionRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Should return decision when ID exists")
        void shouldReturnDecisionWhenExists() {
            UUID decisionId = UUID.randomUUID();

            Decision decision = DecisionBuilderTest.decision()
                    .withId(decisionId)
                    .withRegretLevel(RegretLevel.HIGH)
                    .build();

            when(decisionRepository.findById(decisionId))
                    .thenReturn(Optional.of(decision));

            var response = decisionService.getDecisionById(decisionId);

            assertThat(response.getData().id()).isEqualTo(decisionId);
            assertThat(response.getData().title()).isEqualTo(decision.getTitle());
            assertThat(response.getData().regretLevel()).isEqualTo(RegretLevel.HIGH);

            verify(decisionRepository).findById(decisionId);
        }

        @Test
        @DisplayName("Should throw when decision not found by ID")
        void shouldThrowResourceNotFoundExceptionWhenDecisionDoesNotExist() {
            when(decisionRepository.findById(any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> decisionService.getById(any()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(decisionRepository, times(1))
                    .findById(any());
        }

        @Test
        @DisplayName("Should return decisions by user ID")
        void shouldReturnDecisionsByUserWhenFound() {
            Pageable pageable = PageRequest.of(0, 1);
            UUID userId = UUID.randomUUID();
            var user = RegularUserBuilder.user().withId(userId).build();
            Decision decision = DecisionBuilderTest
                    .decision()
                    .withUser(user)
                    .build();


            when(regularUserService.existsById(userId))
                    .thenReturn(true);

            when(decisionRepository.findByUserId(userId, pageable))
                    .thenReturn(new PageImpl<>(List.of(decision), pageable, 1));

            var response = decisionService.getDecisionsByUserId(userId, pageable);

            assertThat(response.getData().getContent()).hasSize(1)
                    .first()
                    .extracting(DecisionResponse::id, DecisionResponse::title, DecisionResponse::userId)
                    .containsExactly(decision.getId(), decision.getTitle(), decision.getUser().getId());

            assertThat(response.getData().getTotalElements()).isEqualTo(1);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);

            verify(decisionRepository).findByUserId(userId, pageable);
            verify(regularUserService).existsById(userId);
        }

        @Test
        @DisplayName("Should throw when user does not exist")
        void shouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
            UUID userId = UUID.randomUUID();
            when(regularUserService.existsById(userId))
                    .thenReturn(false);

            assertThatThrownBy(() -> decisionService.getDecisionsByUserId(userId, any()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(regularUserService).existsById(any());
            verify(decisionRepository, never()).findByUserId(any(), any());
        }

        @ParameterizedTest
        @EnumSource(RegretLevel.class)
        @DisplayName("Should return decisions filtered by regret level")
        void shouldReturnDecisionsByRegretLevelWhenFound(RegretLevel regretLevel, Decision decision) {

            Pageable pageable = PageRequest.of(0, 1);
            Page<Decision> decisions = new PageImpl<>(List.of(decision));

            when(decisionRepository.findByRegretLevel(regretLevel, pageable))
                    .thenReturn(decisions);

            var response = decisionService.
                    getDecisionsByRegretLevel(regretLevel, pageable);

            assertThat(response.getData().getContent())
                    .hasSize(1)
                    .first()
                    .extracting(DecisionResponse::id, DecisionResponse::title, DecisionResponse::regretLevel)
                    .containsExactly(decision.getId(), decision.getTitle(), decision.getRegretLevel());

            verify(decisionRepository).findByRegretLevel(regretLevel, pageable);
        }

        @Test
        @DisplayName("Should return decisions by tag ID")
        void shouldReturnDecisionsByTagWhenFound() {
            UUID tagId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 1);
            Tag tag = TagBuilder.tag().withId(tagId).build();
            Decision decision = DecisionBuilderTest
                    .decision()
                    .withTag(tag)
                    .build();

            Page<Decision> decisions = new PageImpl<>(List.of(decision));

            when(tagService.existsById(tagId))
                    .thenReturn(true);

            when(decisionRepository.findByTagId(tagId, pageable))
                    .thenReturn(decisions);

            var response = decisionService.getDecisionsByTagId(tagId, pageable);

            assertThat(response.getData().getContent())
                    .hasSize(1)
                    .first()
                    .extracting(DecisionResponse::tags)
                    .satisfies(tags -> {
                        assertThat(tags).contains(tag.getName());
                    });

            verify(tagService).existsById(tagId);
            verify(decisionRepository).findByTagId(tagId, pageable);
        }

        @Test
        @DisplayName("Should throw when tag does not exist")
        void shouldThrowResourceNotFoundExceptionWhenTagDoesNotExist() {
            UUID tagId = UUID.randomUUID();
            when(tagService.existsById(tagId))
                    .thenReturn(false);

            assertThatThrownBy(() -> decisionService.getDecisionsByTagId(tagId, any()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(tagService).existsById(tagId);
            verify(decisionRepository, never()).findByTagId(any(), any());
        }
    }


    @Nested
    @DisplayName("Create Decision")
    class CreateDecision {

        @Test
        @DisplayName("Should create decision successfully without tags")
        void shouldCreateDecisionWithoutTagsWhenValidRequest() {
            DecisionRequest request = DecisionRequestBuilder.aRequest()
                    .withoutTags()
                    .build();
            var user = RegularUserBuilder.user()
                    .withId(request.userId())
                    .build();

            Decision decision = DecisionBuilderTest.decision()
                    .withUser(user)
                    .build();

            when(regularUserService.getById(request.userId()))
                    .thenReturn(user);

            when(decisionRepository.existsByTitleAndUserId(request.title(), request.userId()))
                    .thenReturn(false);

            when(decisionRepository.saveAndFlush(any(Decision.class)))
                    .thenReturn(decision);

            var response = decisionService.createDecision(request);

            assertThat(response.getData().id()).isEqualTo(decision.getId());
            assertThat(response.getData().title()).isEqualTo(decision.getTitle());
            assertThat(response.getData().regretLevel()).isEqualTo(decision.getRegretLevel());
            assertThat(response.getData().userId()).isEqualTo(decision.getUser().getId());
            assertThat(response.getData().voteCount()).isEqualTo(decision.getVoteCount());

            verify(regularUserService).getById(request.userId());
            verify(decisionRepository).existsByTitleAndUserId(request.title(), request.userId());
            verify(decisionRepository).saveAndFlush(any(Decision.class));
        }

        @Test
        @DisplayName("Should create decision with tags")
        void shouldCreateDecisionWithTagWhenRequestHasTags() {
            UUID tagId = UUID.randomUUID();
            DecisionRequest request = DecisionRequestBuilder.aRequest()
                    .withTags(Set.of(tagId))
                    .build();

            var user = RegularUserBuilder.user()
                    .withId(request.userId())
                    .build();
            var tag = TagBuilder.tag().withId(tagId).build();

            Decision decision = DecisionBuilderTest.decision()
                    .withUser(user)
                    .withTag(tag)
                    .build();

            when(regularUserService.getById(request.userId()))
                    .thenReturn(user);

            when(decisionRepository.existsByTitleAndUserId(request.title(), request.userId()))
                    .thenReturn(false);

            when(decisionRepository.saveAndFlush(any(Decision.class)))
                    .thenReturn(decision);

            when(tagService.getById(tagId))
                    .thenReturn(tag);

            var response = decisionService.createDecision(request);

            assertThat(response.getData().id()).isEqualTo(decision.getId());
            assertThat(response.getData().title()).isEqualTo(decision.getTitle());
            assertThat(response.getData().regretLevel()).isEqualTo(decision.getRegretLevel());
            assertThat(response.getData().userId()).isEqualTo(decision.getUser().getId());
            assertThat(response.getData().voteCount()).isEqualTo(decision.getVoteCount());
            assertThat(response.getData().tags()).hasSize(1)
                    .first()
                    .isEqualTo(tag.getName());

            verify(regularUserService).getById(request.userId());
            verify(tagService).getById(tagId);
            verify(decisionRepository).existsByTitleAndUserId(request.title(), request.userId());
            verify(decisionRepository).saveAndFlush(any(Decision.class));
        }

        @Test
        @DisplayName("Should throw conflict when title already exists")
        void shouldThrowConflictWhenTitleAlreadyExists() {

            DecisionRequest request = DecisionRequestBuilder.aRequest().build();
            var user = RegularUserBuilder.user().withId(request.userId()).build();

            when(regularUserService.getById(request.userId())).thenReturn(user);
            when(decisionRepository.existsByTitleAndUserId(request.title(), request.userId()))
                    .thenReturn(true);

            assertThatThrownBy(() -> decisionService.createDecision(request))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("Decision with this title already exists for this user");

            verify(regularUserService).getById(request.userId());
            verify(decisionRepository).existsByTitleAndUserId(request.title(), request.userId());
            verifyNoInteractions(tagService);
            verify(decisionRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("Should handle null tag list gracefully")
        void shouldHandleNullWhenTagsAreNull() {
            UUID tagId = UUID.randomUUID();
            DecisionRequest request = DecisionRequestBuilder.aRequest()
                    .withTags(null)
                    .build();
            var user = RegularUserBuilder
                    .user().withId(request.userId()).build();

            Decision decision = DecisionBuilderTest
                    .decision()
                    .withUser(user)
                    .build();

            when(regularUserService.getById(request.userId()))
                    .thenReturn(user);
            when(decisionRepository.existsByTitleAndUserId(any(), any()))
                    .thenReturn(false);

            when(decisionRepository.saveAndFlush(any(Decision.class)))
                    .thenReturn(decision);

            var response = decisionService.createDecision(request);

            assertThat(response.getData().tags()).isEmpty();
            assertThat(response.getData().id()).isEqualTo(decision.getId());
            assertThat(response.getData().title()).isEqualTo(decision.getTitle());
            assertThat(response.getData().regretLevel()).isEqualTo(decision.getRegretLevel());
            assertThat(response.getData().userId()).isEqualTo(decision.getUser().getId());
            assertThat(response.getData().voteCount()).isEqualTo(decision.getVoteCount());

            verify(regularUserService).getById(request.userId());
            verify(decisionRepository).existsByTitleAndUserId(request.title(), request.userId());
            verify(decisionRepository).saveAndFlush(any(Decision.class));
            verifyNoInteractions(tagService);
        }
    }

    @Nested
    @DisplayName("Update Decision")
    class UpdateDecision {

        @Test
        @DisplayName("Should update decision successfully")
        void shouldUpdateDecisionWhenRequestIsValid() {
            UUID decisionId = UUID.randomUUID();
            DecisionUpdateRequest request =
                    new DecisionUpdateRequest(
                            "new-title",
                            "new-why",
                            "new-alternative",
                            RegretLevel.HIGH,
                            Set.of()
                    );

            Decision decision = DecisionBuilderTest.decision()
                    .withId(decisionId)
                    .withTitle("old-title")
                    .withWhy("old-why")
                    .withAlternative("old-alternative")
                    .withRegretLevel(RegretLevel.LOW)
                    .build();

            when(decisionRepository.findById(decisionId))
                    .thenReturn(Optional.of(decision));

            when(decisionRepository.existsByTitleAndUserId(request.title(), decision.getUser().getId()))
                    .thenReturn(false);

            when(decisionRepository.save(any(Decision.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));


            BaseResponse<DecisionResponse> response =
                    decisionService.updateDecision(decisionId, request);


            assertThat(response).isNotNull();

            assertThat(decision.getTitle())
                    .isEqualTo(request.title());

            assertThat(decision.getWhy())
                    .isEqualTo(request.why());

            assertThat(decision.getAlternative())
                    .isEqualTo(request.alternative());

            assertThat(decision.getRegretLevel())
                    .isEqualTo(request.regretLevel());


            verify(decisionRepository).findById(decisionId);
            verify(decisionRepository).existsByTitleAndUserId(request.title(), decision.getUser().getId());
            verify(decisionRepository).save(decision);

        }

        @Test
        @DisplayName("Should replace existing tags with new tags")
        void shouldReplaceExistingTagsWithNewTags() {

            // Arrange
            UUID decisionId = UUID.randomUUID();

            UUID oldTagId = UUID.randomUUID();
            UUID newTagId1 = UUID.randomUUID();
            UUID newTagId2 = UUID.randomUUID();

            Tag oldTag = TagBuilder.tag()
                    .withId(oldTagId)
                    .build();

            Tag newTag1 = TagBuilder.tag()
                    .withId(newTagId1)
                    .build();

            Tag newTag2 = TagBuilder.tag()
                    .withId(newTagId2)
                    .build();

            Decision decision = DecisionBuilderTest.decision()
                    .withId(decisionId)
                    .withTitle("old-title")
                    .withTag(oldTag)
                    .build();

            DecisionUpdateRequest request =
                    new DecisionUpdateRequest(
                            "new-title",
                            "why",
                            "alternative",
                            RegretLevel.HIGH,
                            Set.of(newTagId1, newTagId2)
                    );

            when(decisionRepository.findById(decisionId))
                    .thenReturn(Optional.of(decision));

            when(decisionRepository.existsByTitleAndUserId(
                    request.title(),
                    decision.getUser().getId()
            )).thenReturn(false);

            when(tagService.getById(newTagId1))
                    .thenReturn(newTag1);

            when(tagService.getById(newTagId2))
                    .thenReturn(newTag2);

            when(decisionRepository.save(any(Decision.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            BaseResponse<DecisionResponse> response =
                    decisionService.updateDecision(decisionId, request);

            // Assert
            assertThat(response).isNotNull();

            Set<UUID> updatedTagIds = decision.getTags()
                    .stream()
                    .map(dt -> dt.getTag().getId())
                    .collect(Collectors.toSet());

            assertThat(updatedTagIds)
                    .containsExactlyInAnyOrder(newTagId1, newTagId2);

            assertThat(updatedTagIds)
                    .doesNotContain(oldTagId);

            verify(tagService).getById(newTagId1);
            verify(tagService).getById(newTagId2);

            verify(decisionRepository).save(decision);
        }

        @Test
        @DisplayName("Should add new tags without duplicating existing ones")
        void shouldAddNewTag() {

            // Arrange
            UUID decisionId = UUID.randomUUID();

            UUID existingTagId = UUID.randomUUID();
            UUID newTagId = UUID.randomUUID();

            Tag existingTag = TagBuilder.tag()
                    .withId(existingTagId)
                    .build();

            Tag newTag = TagBuilder.tag()
                    .withId(newTagId)
                    .build();

            Decision decision = DecisionBuilderTest.decision()
                    .withId(decisionId)
                    .withTitle("old-title")
                    .withTag(existingTag)
                    .build();

            DecisionUpdateRequest request =
                    new DecisionUpdateRequest(
                            "new-title",
                            "why",
                            "alternative",
                            RegretLevel.HIGH,
                            Set.of(existingTagId, newTagId)
                    );

            when(decisionRepository.findById(decisionId))
                    .thenReturn(Optional.of(decision));

            when(decisionRepository.existsByTitleAndUserId(
                    request.title(),
                    decision.getUser().getId()
            )).thenReturn(false);

            when(tagService.getById(newTagId))
                    .thenReturn(newTag);

            when(decisionRepository.save(any(Decision.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            decisionService.updateDecision(decisionId, request);

            // Assert
            Set<UUID> updatedTagIds = decision.getTags()
                    .stream()
                    .map(dt -> dt.getTag().getId())
                    .collect(Collectors.toSet());

            assertThat(updatedTagIds)
                    .containsExactlyInAnyOrder(existingTagId, newTagId);

            assertThat(decision.getTags())
                    .hasSize(2);

            verify(tagService, never()).getById(existingTagId);

            verify(tagService).getById(newTagId);

            verify(decisionRepository).save(decision);
        }

        @Test
        @DisplayName("Should remove old tags not present in request")
        void shouldRemoveOldTagsNotPresentInRequest() {

            // Arrange
            UUID decisionId = UUID.randomUUID();

            UUID keptTagId = UUID.randomUUID();
            UUID removedTagId = UUID.randomUUID();

            Tag keptTag = TagBuilder.tag()
                    .withId(keptTagId)
                    .build();

            Tag removedTag = TagBuilder.tag()
                    .withId(removedTagId)
                    .build();

            Decision decision = DecisionBuilderTest.decision()
                    .withId(decisionId)
                    .withTitle("old-title")
                    .withTag(keptTag)
                    .withTag(removedTag)
                    .build();

            DecisionUpdateRequest request =
                    new DecisionUpdateRequest(
                            "new-title",
                            "why",
                            "alternative",
                            RegretLevel.HIGH,
                            Set.of(keptTagId)
                    );

            when(decisionRepository.findById(decisionId))
                    .thenReturn(Optional.of(decision));

            when(decisionRepository.existsByTitleAndUserId(
                    request.title(),
                    decision.getUser().getId()
            )).thenReturn(false);

            when(decisionRepository.save(any(Decision.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            decisionService.updateDecision(decisionId, request);

            // Assert
            Set<UUID> updatedTagIds = decision.getTags()
                    .stream()
                    .map(dt -> dt.getTag().getId())
                    .collect(Collectors.toSet());

            assertThat(updatedTagIds)
                    .containsExactly(keptTagId);

            assertThat(updatedTagIds)
                    .doesNotContain(removedTagId);

            assertThat(decision.getTags())
                    .hasSize(1);

            verify(tagService, never()).getById(any());

            verify(decisionRepository).save(decision);
        }

        @Test
        @DisplayName("Should throw conflict when updating title to existing one")
        void shouldThrowConflictWhenUpdatingTitleToExistingOne() {
            UUID decisionId = UUID.randomUUID();
            DecisionUpdateRequest request =
                    new DecisionUpdateRequest(
                            "new-title",
                            "why",
                            "alternative",
                            RegretLevel.HIGH,
                            Set.of()
                    );

            Decision decision = DecisionBuilderTest.decision()
                    .withId(decisionId)
                    .withTitle("old-title")
                    .build();

            when(decisionRepository.findById(decisionId))
                    .thenReturn(Optional.of(decision));


            when(decisionRepository.existsByTitleAndUserId(request.title(), decision.getUser().getId()))
                    .thenReturn(true);

            assertThatThrownBy(() -> decisionService.updateDecision(decisionId, request))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("Decision with this title already exists for this user");

            verify(decisionRepository).findById(decisionId);
            verify(decisionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when decision not found")
        void shouldThrowResourceNotFoundExceptionWhenDecisionDoesNotExist() {
            UUID decisionId = UUID.randomUUID();


            when(decisionRepository.findById(decisionId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> decisionService.updateDecision(decisionId, any()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(decisionRepository).findById(any());
            verify(decisionRepository, never()).save(any());
            verifyNoInteractions(tagService);
        }
    }

    @Nested
    @DisplayName("Delete Decision")
    class DeleteDecision {

        @Test
        @DisplayName("Should delete decision successfully")
        void shouldDeleteDecisionWhenExists() {
            UUID decisionId = UUID.randomUUID();
            Decision decision = DecisionBuilderTest.decision()
                    .withId(decisionId)
                    .build();

            when(decisionRepository.existsById(decisionId))
                    .thenReturn(true);

            doNothing().when(decisionRepository).deleteById(decisionId);

            var response = decisionService.deleteDecision(decisionId);

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

            verify(decisionRepository).existsById(decisionId);
            verify(decisionRepository).deleteById(decisionId);
        }

        @Test
        @DisplayName("Should throw when decision not found")
        void shouldThrowResourceNotFoundExceptionWhenDecisionDoesNotExist() {
            when(decisionRepository.existsById(any()))
                    .thenReturn(false);

            assertThatThrownBy(() -> decisionService.deleteDecision(any()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(decisionRepository).existsById(any());
            verify(decisionRepository, never()).deleteById(any());
        }
    }

    // -----------------------------
    // VOTE OPERATIONS
    // -----------------------------
    @Nested
    @DisplayName("Vote Operations")
    class VoteOperations {
        private UUID decisionId;

        @BeforeEach
        public void setUp() {
            decisionId = UUID.randomUUID();
        }

        @Test
        @DisplayName("Should increment vote count when decision exists")
        void shouldIncrementVoteCountWhenDecisionExists() {

            when(decisionRepository.existsById(decisionId))
                    .thenReturn(true);

            decisionService.incrementVoteCount(decisionId);

            InOrder inOrder = inOrder(decisionRepository);
            inOrder.verify(decisionRepository).existsById(decisionId);
            inOrder.verify(decisionRepository).incrementVoteCount(decisionId);
        }

        @Test
        void shouldDecrementVoteCountWhenDecisionExists() {
            when(decisionRepository.existsById(decisionId))
                    .thenReturn(true);
            decisionService.decrementVoteCount(decisionId);

            InOrder inOrder = inOrder(decisionRepository);
            inOrder.verify(decisionRepository).existsById(decisionId);
            inOrder.verify(decisionRepository).decrementVoteCount(decisionId);
        }

        @Test
        @DisplayName("Should throw when decision not found for increment")
        void shouldThrowWhenDecisionNotFoundForIncrement() {
            when(decisionRepository.existsById(decisionId))
                    .thenReturn(false);

            assertThatThrownBy(() -> decisionService.incrementVoteCount(decisionId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(decisionRepository).existsById(decisionId);
            verify(decisionRepository, never()).incrementVoteCount(decisionId);
        }

        @Test
        @DisplayName("Should throw when decision not found for decrement")
        void shouldThrowWhenDecisionNotFoundForDecrement() {
            when(decisionRepository.existsById(decisionId))
                    .thenReturn(false);

            assertThatThrownBy(() -> decisionService.decrementVoteCount(decisionId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(decisionRepository).existsById(decisionId);
            verify(decisionRepository, never()).decrementVoteCount(decisionId);
        }
    }

    @Nested
    @DisplayName("Utility Methods")
    class UtilityMethods {

        @Test
        @DisplayName("Should return true when decision exists")
        void shouldReturnTrueWhenDecisionExists() {
            Decision decision = DecisionBuilderTest.decision().build();

            when(decisionRepository.existsById(decision.getId())).thenReturn(true);

            assertThat(decisionService.existsById(decision.getId())).isTrue();

            verify(decisionRepository).existsById(decision.getId());
        }

        @Test
        @DisplayName("Should return false when decision does not exist")
        void shouldReturnFalseWhenDecisionDoesNotExist() {
            UUID decisionId = UUID.randomUUID();
            when(decisionRepository.existsById(decisionId)).thenReturn(false);

            assertThat(decisionService.existsById(decisionId)).isFalse();
            verify(decisionRepository).existsById(decisionId);
        }

        @Test
        @DisplayName("Should return decision when exists")
        void shouldReturnDecisionWhenExists() {
            Decision decision = DecisionBuilderTest.decision().build();
            when(decisionRepository.findById(decision.getId()))
                    .thenReturn(Optional.of(decision));

            assertThat(decisionService.getById(decision.getId()))
                    .isEqualTo(decision);

            verify(decisionRepository).findById(decision.getId());
        }

        @Test
        @DisplayName("Should throw when getById fails")
        void shouldThrowResourceNotFoundExceptionWhenDecisionDoesNotExist() {
            UUID decisionId = UUID.randomUUID();
            when(decisionRepository.findById(decisionId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> decisionService.getById(decisionId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(decisionRepository).findById(decisionId);
        }

        @Test
        @DisplayName("Should save decision")
        void shouldSaveDecision() {
            UUID decisionId = UUID.randomUUID();
            Decision decision = DecisionBuilderTest
                    .decision().withId(decisionId).build();

            ArgumentCaptor<Decision> decisionCaptor = ArgumentCaptor.forClass(Decision.class);

            when(decisionRepository.save(any(Decision.class)))
                    .thenAnswer(invocation -> {
                        Decision savedDecision = invocation.getArgument(0);
                        savedDecision.setId(decisionId);
                        return savedDecision;
                    });

            decisionService.save(decision);

            verify(decisionRepository).save(decisionCaptor.capture());

            assertThat(decisionCaptor.getValue().getId()).isEqualTo(decisionId);

        }
    }

}