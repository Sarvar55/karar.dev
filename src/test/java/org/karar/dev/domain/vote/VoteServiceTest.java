package org.karar.dev.domain.vote;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.conflict.ConflictException;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.common.security.service.SecurityService;
import org.karar.dev.domain.annotation.UnitTest;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.decision.Decision;
import org.karar.dev.domain.decision.DecisionBuilderTest;
import org.karar.dev.domain.decision.DecisionService;
import org.karar.dev.domain.extensions.VoteParameterResolver;
import org.karar.dev.domain.user.regular.RegularUser;
import org.karar.dev.domain.user.regular.RegularUserBuilder;
import org.karar.dev.domain.user.regular.RegularUserService;
import org.karar.dev.domain.vote.dto.VoteCountResponse;
import org.karar.dev.domain.vote.dto.VoteRequest;
import org.karar.dev.domain.vote.dto.VoteResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@ExtendWith(VoteParameterResolver.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private RegularUserService regularUserService;

    @Mock
    private DecisionService decisionService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private VoteService voteService;

    @Test
    void getAllVotes(Vote vote) {
        List<Vote> votes = List.of(vote);

        when(voteRepository.findAll())
                .thenReturn(votes);

        BaseResponse<List<VoteResponse>> voteList =
                voteService.getAllVotes();

        assertThat(voteList.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(voteList.getData().size()).isEqualTo(1);
        assertThat(voteList.getData().get(0).decisionId()).isEqualTo(vote.getDecision().getId());
        assertThat(voteList.getData().get(0).userId()).isEqualTo(vote.getUser().getId());

        verify(voteRepository).findAll();
    }

    @Nested
    @DisplayName("getVoteById()")
    class GetById {

        @AfterEach
        void setUp() {
            verify(voteRepository).findById(any());
        }


        @Test
        @DisplayName("Should return vote by ID successfully")
        void shouldReturnVoteWhenExists(Vote vote) {
            when(voteRepository.findById(vote.getId()))
                    .thenReturn(Optional.of(vote));

            BaseResponse<VoteResponse> voteResponse =
                    voteService.getVoteById(vote.getId());

            assertThat(voteResponse.getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(voteResponse.getData().decisionId()).isEqualTo(vote.getDecision().getId());
            assertThat(voteResponse.getData().userId()).isEqualTo(vote.getUser().getId());

        }

        @Test
        @DisplayName("Should throw exception when vote not found")
        void shouldThrowResourceNotFoundExceptionWhenVoteNotFound() {
            when(voteRepository.findById(any()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> voteService.getVoteById(any()))
                    .isInstanceOf(ResourceNotFoundException.class);

        }
    }


    @Nested
    @DisplayName("getVotesByDecisionId()")
    class GetByDecisionId {

        private final UUID decisionId = UUID.randomUUID();
        private final UUID userId = UUID.randomUUID();
        private final long voteCount = 10L;

        @Test
        @DisplayName("Should return votes by decision ID successfully")
        void shouldReturnVotesByDecisionIdWhenExists() {
            when(decisionService.existsById(decisionId))
                    .thenReturn(true);

            when(voteRepository.countByDecisionId(decisionId))
                    .thenReturn(voteCount);

            when(voteRepository.existsByUserIdAndDecisionId(userId, decisionId))
                    .thenReturn(true);

            BaseResponse<VoteCountResponse> response
                    = voteService.getVoteCountByDecisionId(decisionId, userId);

            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(response.getData())
                    .extracting(VoteCountResponse::voteCount, VoteCountResponse::decisionId)
                    .containsExactly(voteCount, decisionId);

            verify(decisionService).existsById(decisionId);
            verify(voteRepository).countByDecisionId(decisionId);
            verify(voteRepository).existsByUserIdAndDecisionId(userId, decisionId);
        }

        @Test
        @DisplayName("Should throw exception when decision not found")
        void shouldThrowResourceNotFoundExceptionWhenDecisionNotFound() {

            when(decisionService.existsById(decisionId))
                    .thenReturn(false);
            assertThatThrownBy(() -> voteService.getVoteCountByDecisionId(decisionId, UUID.randomUUID()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(decisionService).existsById(decisionId);
        }

        @Test
        @DisplayName("Should handle null userId")
        void shouldHandleNullUserId() {


            when(decisionService.existsById(decisionId)).thenReturn(true);
            when(voteRepository.countByDecisionId(decisionId)).thenReturn(voteCount);

            BaseResponse<VoteCountResponse> response =
                    voteService.getVoteCountByDecisionId(decisionId, null);

            assertThat(response.getData().hasVoted()).isFalse();

            verify(voteRepository, never())
                    .existsByUserIdAndDecisionId(any(), any());
        }

        @Test
        @DisplayName("Should return hasVoted false when userId is null")
        void shouldReturnHasVotedFalse() {

            when(decisionService.existsById(decisionId)).thenReturn(true);
            when(voteRepository.countByDecisionId(decisionId)).thenReturn(voteCount);
            when(voteRepository.existsByUserIdAndDecisionId(userId, decisionId))
                    .thenReturn(false);

            BaseResponse<VoteCountResponse> response =
                    voteService.getVoteCountByDecisionId(decisionId, userId);

            assertThat(response.getData().hasVoted()).isFalse();
            assertThat(voteCount).isEqualTo(response.getData().voteCount());
        }
    }

    @Nested
    @DisplayName("getVotesByDecisionId()")
    class GetVotesByDecisionId {

        @Test
        @DisplayName("Should return mapped votes by decision id")
        void shouldReturnMappedVotesByDecisionId() {

            UUID decisionId = UUID.randomUUID();

            Vote vote = VoteMother.aDefaultVote();

            when(decisionService.existsById(decisionId))
                    .thenReturn(true);

            when(voteRepository.findByDecisionId(decisionId))
                    .thenReturn(List.of(vote));

            BaseResponse<List<VoteResponse>> response =
                    voteService.getVotesByDecisionId(decisionId);

            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);

            assertThat(response.getData())
                    .isNotNull();

            VoteResponse voteResponse = response.getData().get(0);

            assertThat(voteResponse.decisionId())
                    .isEqualTo(vote.getDecision().getId());

            assertThat(voteResponse.userId())
                    .isEqualTo(vote.getUser().getId());

            verify(decisionService).existsById(decisionId);
            verify(voteRepository).findByDecisionId(decisionId);

            verifyNoMoreInteractions(decisionService, voteRepository);
        }

        @Test
        @DisplayName("Should throw exception when decision not found")
        void shouldThrowResourceNotFoundExceptionWhenDecisionNotFound() {
            when(decisionService.existsById(any()))
                    .thenReturn(false);

            assertThatThrownBy(() -> voteService.getVotesByDecisionId(any()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(decisionService).existsById(any());
            verify(voteRepository, never()).findByDecisionId(any());
            verifyNoMoreInteractions(decisionService, voteRepository);
        }
    }


    @Nested
    @DisplayName("getVotesByUserId()")
    class GetVotesByUserId {
        @Test
        @DisplayName("Should return votes by user id")
        void shouldReturnVotesByUserId() {
            UUID userId = UUID.randomUUID();
            Vote vote = VoteMother.aDefaultVote();

            when(regularUserService.existsById(userId))
                    .thenReturn(true);

            when(voteRepository.findByUserId(userId))
                    .thenReturn(List.of(vote));

            BaseResponse<List<VoteResponse>> response =
                    voteService.getVotesByUserId(userId);

            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(response.getData())
                    .isNotNull();

            assertThat(response.getData().size()).isEqualTo(1);

            assertThat(response.getData().get(0).decisionId())
                    .isEqualTo(vote.getDecision().getId());

            assertThat(response.getData().get(0).userId())
                    .isEqualTo(vote.getUser().getId());

            verify(regularUserService).existsById(userId);
            verify(voteRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
            when(regularUserService.existsById(any()))
                    .thenReturn(false);
            assertThatThrownBy(() -> voteService.getVotesByUserId(any()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(regularUserService).existsById(any());
            verify(voteRepository, never()).findByUserId(any());
            verifyNoMoreInteractions(regularUserService, voteRepository);
        }
    }


    @Nested
    @DisplayName("hasUserVoted()")
    class HasUserVoted {
        @Test
        void shouldReturnTrueWhenUserHasVoted() {
            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();
            Vote vote = VoteMother.aDefaultVote();

            when(regularUserService.existsById(userId))
                    .thenReturn(true);

            when(decisionService.existsById(decisionId))
                    .thenReturn(true);

            when(voteRepository.existsByUserIdAndDecisionId(userId, decisionId))
                    .thenReturn(true);

            BaseResponse<Boolean> response = voteService.hasUserVoted(userId, decisionId);

            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(response.getData()).isTrue();

            verify(voteRepository).existsByUserIdAndDecisionId(userId, decisionId);
            verifyNoMoreInteractions(voteRepository);
        }

        @Test
        @DisplayName("Should return false when user has not voted")
        void shouldReturnFalseWhenUserHasNotVoted() {
            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            when(regularUserService.existsById(userId))
                    .thenReturn(true);

            when(decisionService.existsById(decisionId))
                    .thenReturn(true);

            when(voteRepository.existsByUserIdAndDecisionId(userId, decisionId))
                    .thenReturn(false);

            BaseResponse<Boolean> response = voteService.hasUserVoted(userId, decisionId);

            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
            assertThat(response.getData()).isFalse();

            verify(voteRepository).existsByUserIdAndDecisionId(userId, decisionId);
            verifyNoMoreInteractions(voteRepository);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            when(regularUserService.existsById(userId))
                    .thenReturn(false);

            assertThatThrownBy(() -> voteService.hasUserVoted(userId, decisionId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(regularUserService).existsById(userId);
            verify(voteRepository, never()).existsByUserIdAndDecisionId(any(), any());
            verifyNoMoreInteractions(regularUserService, voteRepository);
        }

        @Test
        @DisplayName("Should throw exception when decision not found")
        void shouldThrowResourceNotFoundExceptionWhenDecisionNotFound() {
            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            when(regularUserService.existsById(userId)).thenReturn(true);
            when(decisionService.existsById(decisionId)).thenReturn(false);

            assertThatThrownBy(() -> voteService.hasUserVoted(userId, decisionId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(regularUserService).existsById(userId);
            verify(decisionService).existsById(decisionId);
            verify(voteRepository, never()).existsByUserIdAndDecisionId(any(), any());
            verifyNoMoreInteractions(regularUserService, decisionService, voteRepository);
        }
    }

    @Nested
    @DisplayName("createVote()")
    class CreateVote {

        @Test
        void shouldCreateVoteSuccessfully() {

            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            VoteRequest request = new VoteRequest(decisionId);

            RegularUser user = RegularUserBuilder.user()
                    .withId(userId)
                    .build();

            Decision decision = DecisionBuilderTest.decision()
                    .withId(decisionId)
                    .build();

            Vote savedVote = Vote.builder()
                    .user(user)
                    .decision(decision)
                    .build();

            when(securityService.getCurrentUserId())
                    .thenReturn(userId);

            when(regularUserService.getById(userId))
                    .thenReturn(user);

            when(decisionService.getById(decisionId))
                    .thenReturn(decision);

            when(voteRepository.existsByUserIdAndDecisionId(userId, decisionId))
                    .thenReturn(false);

            when(voteRepository.saveAndFlush(any(Vote.class)))
                    .thenReturn(savedVote);

            // when
            BaseResponse<VoteResponse> response =
                    voteService.createVote(request);

            // then - CAPTURE THE OBJECT SENT TO DB
            ArgumentCaptor<Vote> voteCaptor = ArgumentCaptor.forClass(Vote.class);

            verify(voteRepository).saveAndFlush(voteCaptor.capture());

            Vote capturedVote = voteCaptor.getValue();

            assertThat(capturedVote.getUser()).isEqualTo(user);
            assertThat(capturedVote.getDecision()).isEqualTo(decision);

            verify(decisionService).incrementVoteCount(decisionId);

            assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getData()).isNotNull();
        }

        @Test
        @DisplayName("Should throw ConflictException when user already voted")
        void shouldThrowConflictExceptionWhenUserAlreadyVoted() {

            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            VoteRequest request = new VoteRequest(decisionId);

            when(securityService.getCurrentUserId())
                    .thenReturn(userId);

            when(voteRepository.existsByUserIdAndDecisionId(userId, decisionId))
                    .thenReturn(true);

            assertThatThrownBy(() -> voteService.createVote(request))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("already voted");

            verify(voteRepository, never()).saveAndFlush(any());
            verify(decisionService, never()).incrementVoteCount(any());
        }

        @Test
        @DisplayName("Should fail when user not authenticated")
        void shouldFailWhenUserNotAuthenticated() {

            UUID decisionId = UUID.randomUUID();
            VoteRequest request = new VoteRequest(decisionId);

            when(securityService.getCurrentUserId())
                    .thenReturn(null);

            when(regularUserService.getById(null))
                    .thenThrow(new RuntimeException("User not found"));

            assertThatThrownBy(() -> voteService.createVote(request))
                    .isInstanceOf(RuntimeException.class);

            verifyNoInteractions(voteRepository);
            verifyNoInteractions(decisionService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user not found")
        void shouldThrowWhenDecisionNotFound() {

            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            VoteRequest request = new VoteRequest(decisionId);

            when(securityService.getCurrentUserId())
                    .thenReturn(userId);

            when(regularUserService.getById(userId))
                    .thenReturn(new RegularUser());

            when(decisionService.getById(decisionId))
                    .thenThrow(new ResourceNotFoundException("Decision", "id", decisionId));

            assertThatThrownBy(() -> voteService.createVote(request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(voteRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("deleteVote()")
    class DeleteVote {

        @Test
        @DisplayName("Should delete vote successfully")
        void shouldDeleteVoteSuccessfullyWhenDecisionExists() {

            UUID voteId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            Decision decision = DecisionBuilderTest.decision().withId(decisionId)
                    .build();

            Vote vote = Vote.builder()
                    .decision(decision)
                    .build();

            when(voteRepository.findById(voteId))
                    .thenReturn(Optional.of(vote));

            BaseResponse<Void> response = voteService.deleteVote(voteId);

            verify(decisionService).decrementVoteCount(decisionId);
            verify(voteRepository).deleteById(voteId);

            assertThat(response.getStatus())
                    .isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("Should delete vote successfully")
        void shouldThrowWhenVoteNotFound() {

            UUID voteId = UUID.randomUUID();

            when(voteRepository.findById(voteId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> voteService.deleteVote(voteId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(decisionService);
            verify(voteRepository, never()).deleteById(any());
        }

        @Test
        void shouldDeleteVoteWithoutDecision() {

            UUID voteId = UUID.randomUUID();

            Vote vote = Vote.builder()
                    .decision(null)
                    .build();

            when(voteRepository.findById(voteId))
                    .thenReturn(Optional.of(vote));

            BaseResponse<Void> response = voteService.deleteVote(voteId);

            verify(decisionService, never()).decrementVoteCount(any());
            verify(voteRepository).deleteById(voteId);

            assertThat(response.getStatus())
                    .isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("Should fail when delete fails")
        void shouldFailWhenDeleteFails() {

            UUID voteId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            Decision decision = DecisionBuilderTest.decision().withId(decisionId).build();

            Vote vote = Vote.builder()
                    .decision(decision)
                    .build();

            when(voteRepository.findById(voteId))
                    .thenReturn(Optional.of(vote));

            doThrow(new RuntimeException("DB error"))
                    .when(voteRepository).deleteById(voteId);

            assertThatThrownBy(() -> voteService.deleteVote(voteId))
                    .isInstanceOf(RuntimeException.class);

            verify(decisionService).decrementVoteCount(decisionId);
        }
    }

    @Nested
    @DisplayName("deleteVoteByUserAndDecision()")
    class DeleteVoteByUserAndDecisionTest {
        @Test
        @DisplayName("Should delete vote by user and decision successfully")
        void shouldDeleteVoteByUserAndDecisionSuccessfully() {

            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            Vote vote = VoteMother.aDefaultVote();

            when(regularUserService.existsById(userId))
                    .thenReturn(true);

            when(decisionService.existsById(decisionId))
                    .thenReturn(true);

            when(voteRepository.findByUserIdAndDecisionId(userId, decisionId))
                    .thenReturn(Optional.of(vote));

            BaseResponse<Void> response =
                    voteService.deleteVoteByUserAndDecision(userId, decisionId);

            verify(decisionService).decrementVoteCount(decisionId);
            verify(voteRepository).delete(vote);

            assertThat(response.getStatus())
                    .isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            when(regularUserService.existsById(userId))
                    .thenReturn(false);

            assertThatThrownBy(() -> voteService.deleteVoteByUserAndDecision(userId, decisionId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found");

            verify(regularUserService).existsById(userId);
            verify(decisionService, never()).decrementVoteCount(any());
            verify(voteRepository, never()).delete(any());
            verifyNoMoreInteractions(regularUserService, decisionService, voteRepository);
        }
        @Test
        @DisplayName("Should throw exception when decision not found")
        void shouldThrowResourceNotFoundExceptionWhenDecisionNotFound(){
            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();

            when(regularUserService.existsById(userId)).thenReturn(true);
            when(decisionService.existsById(decisionId)).thenReturn(false);

            assertThatThrownBy(() -> voteService.deleteVoteByUserAndDecision(userId, decisionId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Decision not found");

            verify(regularUserService).existsById(userId);
            verify(decisionService).existsById(decisionId);
            verify(voteRepository, never()).delete(any());
            verifyNoMoreInteractions(regularUserService, decisionService, voteRepository);
        }
        @Test
        @DisplayName("Should throw exception when vote not found")
        void shouldThrowResourceNotFoundExceptionWhenVoteNotFound(){
            UUID userId = UUID.randomUUID();
            UUID decisionId = UUID.randomUUID();
            when(regularUserService.existsById(userId)).thenReturn(true);
            when(decisionService.existsById(decisionId)).thenReturn(true);
            when(voteRepository.findByUserIdAndDecisionId(userId, decisionId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> voteService.deleteVoteByUserAndDecision(userId, decisionId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Vote not found");

            verify(regularUserService).existsById(userId);
            verify(decisionService).existsById(decisionId);
            verify(voteRepository, never()).delete(any());
            verifyNoMoreInteractions(regularUserService, decisionService, voteRepository);
        }
    }
}