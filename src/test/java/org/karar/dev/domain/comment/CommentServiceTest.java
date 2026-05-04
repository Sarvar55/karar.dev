package org.karar.dev.domain.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.annotation.UnitTest;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.comment.dto.CommentRequest;
import org.karar.dev.domain.comment.dto.CommentResponse;
import org.karar.dev.domain.comment.dto.CommentUpdateRequest;
import org.karar.dev.domain.decision.Decision;
import org.karar.dev.domain.decision.DecisionService;
import org.karar.dev.domain.extensions.CommentParameterResolver;
import org.karar.dev.domain.user.regular.RegularUser;
import org.karar.dev.domain.user.regular.RegularUserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.karar.dev.domain.comment.CommentFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@ExtendWith(CommentParameterResolver.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RegularUserService regularUserService;

    @Mock
    private DecisionService decisionService;

    @InjectMocks
    private CommentService commentService;


    @Nested
    @DisplayName("getAllComments()")
    class GetAllComments {

        @Test
        @DisplayName("Should return all comments")
        void shouldReturnPaginatedCommentsWhenCommentsExists(Comment comment) {
            Pageable pageable = PageRequest.of(0, 1);

            when(commentRepository.findAll(pageable))
                    .thenReturn(new PageImpl<>(List.of(comment), pageable, 1));
            BaseResponse<PageResponse<CommentResponse>> response =
                    commentService.getAllComments(pageable);

            assertThat(response.getData().getContent()).hasSize(1)
                    .first()
                    .extracting(CommentResponse::id, CommentResponse::content)
                    .containsExactly(comment.getId(), comment.getContent());

            verify(commentRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("getCommentById()")
    class GetById {
        @Test
        @DisplayName("Should return comment by ID successfully")
        void shouldReturnCommentWhenExists(Comment comment) {
            UUID id = randomId();
            when(commentRepository.findById(id))
                    .thenReturn(Optional.of(comment));
            BaseResponse<CommentResponse> response
                    = commentService.getCommentById(id);

            assertThat(response.getData())
                    .extracting(CommentResponse::id, CommentResponse::content)
                    .containsExactly(comment.getId(), comment.getContent());

            verify(commentRepository).findById(id);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when comment not found")
        void shouldThrowResourceNotFoundExceptionWhenCommentDoesntExist() {
            UUID id = randomId();
            when(commentRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> commentService.getCommentById(id))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(commentRepository).findById(id);
        }
    }

    @Nested
    @DisplayName("getCommentByDecision")
    class GetByDecision {

        @Test
        @DisplayName("Should return comment by decision successfully")
        void shouldReturnCommentWhenDecisionExists(Comment comment) {
            UUID decisionId = randomId();
            Pageable pageable = PageRequest.of(0, 1);

            when(decisionService.existsById(decisionId))
                    .thenReturn(true);
            when(commentRepository.findByDecisionId(decisionId, pageable))
                    .thenReturn(new PageImpl<>(List.of(comment), pageable, 1));

            BaseResponse<PageResponse<CommentResponse>> response =
                    commentService.getCommentsByDecisionId(decisionId, pageable);

            assertThat(response.getData().getContent()).hasSize(1)
                    .first()
                    .extracting(CommentResponse::id, CommentResponse::content)
                    .containsExactly(comment.getId(), comment.getContent());

            verify(commentRepository).findByDecisionId(any(), any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when decision not found")
        void shouldThrowResourceNotFoundExceptionWhenDecisionDoesntExist() {
            UUID decisionId = randomId();
            Pageable pageable = PageRequest.of(0, 1);

            when(decisionService.existsById(decisionId))
                    .thenReturn(false);
            assertThatThrownBy(() -> commentService.getCommentsByDecisionId(decisionId, pageable))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(decisionService).existsById(decisionId);
            verify(commentRepository, never()).findByDecisionId(any(), any());
        }
    }

    @Nested
    @DisplayName("getCommentByUser")
    class GetByUser {

        @Test
        @DisplayName("Should return comment by user successfully")
        void shouldReturnCommentWhenUserExists(Comment comment) {
            UUID userId = randomId();
            Pageable pageable = PageRequest.of(0, 1);

            when(regularUserService.existsById(userId))
                    .thenReturn(true);

            when(commentRepository.findByUserId(userId, pageable))
                    .thenReturn(new PageImpl<>(List.of(comment), pageable, 1));

            BaseResponse<PageResponse<CommentResponse>> response =
                    commentService.getCommentsByUserId(userId, pageable);

            assertThat(response.getData().getContent()).hasSize(1)
                    .first()
                    .extracting(CommentResponse::id, CommentResponse::content)
                    .containsExactly(comment.getId(), comment.getContent());

            verify(commentRepository).findByUserId(any(), any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user not found")
        void shouldThrowResourceNotFoundExceptionWhenUserDoesntExist() {
            UUID userId = randomId();
            Pageable pageable = PageRequest.of(0, 1);
            when(regularUserService.existsById(userId))
                    .thenReturn(false);
            assertThatThrownBy(() -> commentService.getCommentsByUserId(userId, pageable))
                    .isInstanceOf(ResourceNotFoundException.class);
            verify(regularUserService).existsById(userId);
            verify(commentRepository, never()).findByUserId(any(), any());
        }
    }

    @Nested
    @DisplayName("getCommentByUserAndDecision")
    class GetByDecisionAndUser {
        @Test
        @DisplayName("Should return comment by user and decision successfully")
        void shouldReturnCommentWhenUserAndDecisionExists(Comment comment) {
            UUID userId = randomId();
            UUID decisionId = randomId();
            Pageable pageable = PageRequest.of(0, 1);

            when(regularUserService.existsById(userId))
                    .thenReturn(true);

            when(decisionService.existsById(decisionId))
                    .thenReturn(true);

            when(commentRepository.findByDecisionIdAndUserId(decisionId, userId, pageable))
                    .thenReturn(new PageImpl<>(List.of(comment), pageable, 1));


            BaseResponse<PageResponse<CommentResponse>> response =
                    commentService.getCommentsByDecisionIdAndUserId(decisionId, userId, pageable);

            assertThat(response.getData().getContent())
                    .hasSize(1)
                    .first()
                    .extracting(CommentResponse::id, CommentResponse::content)
                    .containsExactly(comment.getId(), comment.getContent());

            assertThat(response.getData().getTotalElements()).isEqualTo(1);

            verify(commentRepository).findByDecisionIdAndUserId(any(), any(), any());
            verify(regularUserService).existsById(any());
            verify(decisionService).existsById(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user or decision not found")
        void shouldThrowResourceNotFoundExceptionWhenUserDoesntExist() {
            UUID userId = randomId();
            UUID decisionId = randomId();
            Pageable pageable = PageRequest.of(0, 1);
            when(decisionService.existsById(decisionId))
                    .thenReturn(true);

            when(regularUserService.existsById(userId))
                    .thenReturn(false);

            assertThatThrownBy(() -> commentService.getCommentsByDecisionIdAndUserId(decisionId, userId, pageable))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(regularUserService).existsById(userId);
            verify(decisionService).existsById(any());
            verify(commentRepository, never()).findByDecisionIdAndUserId(any(), any(), any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user or decision not found")
        void shouldThrowResourceNotFoundExceptionWhenDecisionDoesntExist() {
            UUID userId = randomId();
            UUID decisionId = randomId();
            Pageable pageable = PageRequest.of(0, 1);

            when(decisionService.existsById(decisionId))
                    .thenReturn(false);

            assertThatThrownBy(() -> commentService.getCommentsByDecisionIdAndUserId(decisionId, userId, pageable))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(decisionService).existsById(decisionId);
            verify(commentRepository, never()).findByDecisionIdAndUserId(any(), any(), any());
            verify(regularUserService, never()).existsById(any());
        }
    }


    @Nested
    class CreateComment {
        @Test
        @DisplayName("Should create comment successfully")
        void shouldCreateCommentWhenValidRequest(RegularUser user, Decision decision, Comment comment) {
            UUID userId = randomId();
            UUID decisionId = randomId();

            CommentRequest request = createRequest(user.getId(), decision.getId());

            Comment savedComment = comment(user, decision);

            when(regularUserService.getById(request.userId()))
                    .thenReturn(user);

            when(decisionService.getById(request.decisionId()))
                    .thenReturn(decision);

            when(commentRepository.saveAndFlush(any(Comment.class)))
                    .thenReturn(savedComment);

            BaseResponse<CommentResponse> response =
                    commentService.createComment(request);

            assertThat(response.getData().id())
                    .isEqualTo(savedComment.getId());

            assertThat(response.getData().content())
                    .isEqualTo(savedComment.getContent());

            verify(regularUserService).getById(request.userId());
            verify(decisionService).getById(request.decisionId());
            verify(commentRepository).saveAndFlush(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user or decision not found")
        void shouldThrowResourceNotFoundExceptionWhenUserOrDecisionDoesntExist() {
            CommentRequest request = createRequest(randomId(), randomId());

            when(regularUserService.getById(request.userId()))
                    .thenReturn(null);

            when(decisionService.getById(request.decisionId()))
                    .thenReturn(null);

            assertThatThrownBy(() -> commentService.createComment(request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(regularUserService).getById(request.userId());
            verify(decisionService).getById(request.decisionId());
            verify(commentRepository, never()).saveAndFlush(any());

        }
    }

    @Nested
    @DisplayName("updateComment()")
    class Update {
        @Test
        @DisplayName("Should update comment successfully")
        void shouldUpdateCommentWhenValidRequest(Comment comment) {
            UUID id = comment.getId();

            CommentUpdateRequest updateRequest =
                    createUpdateRequest(comment.getContent());


            when(commentRepository.findById(id))
                    .thenReturn(Optional.of(comment));

            when(commentRepository.saveAndFlush(any(Comment.class)))
                    .thenReturn(comment);

            BaseResponse<CommentResponse> response =
                    commentService.updateComment(id, updateRequest);

            assertThat(response.getData().content())
                    .isEqualTo(updateRequest.content());

            verify(commentRepository).findById(id);
            verify(commentRepository).saveAndFlush(any(Comment.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when comment not found")
        void shouldThrowResourceNotFoundExceptionWhenCommentDoesntExist() {
            UUID id = randomId();
            CommentUpdateRequest updateRequest =
                    createUpdateRequest("new content");

            when(commentRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> commentService.updateComment(id, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(commentRepository).findById(id);
            verify(commentRepository, never()).saveAndFlush(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("deleteComment()")
    class Delete {
        @Test
        @DisplayName("Should delete comment successfully")
        void shouldDeleteCommentWhenCommentExists(Comment comment) {
            UUID id = comment.getId();

            when(commentRepository.existsById(id))
                    .thenReturn(true);

            doNothing().when(commentRepository).deleteById(id);

            commentService.deleteComment(id);

            verify(commentRepository).existsById(id);
            verify(commentRepository).deleteById(id);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when comment not found")
        void shouldThrowResourceNotFoundExceptionWhenCommentDoesntExist() {
            UUID id = randomId();

            when(commentRepository.existsById(id))
                    .thenReturn(false);

            assertThatThrownBy(() -> commentService.deleteComment(id))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(commentRepository).existsById(id);
            verify(commentRepository, never()).deleteById(id);
        }
    }
}