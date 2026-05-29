package org.karar.dev.domain.user.regular;
import org.karar.dev.domain.user.regular.repository.RegularUserRepository;
import org.karar.dev.domain.user.regular.service.RegularUserService;
import org.karar.dev.domain.user.regular.entity.RegularUser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.karar.dev.common.dto.PageResponse;
import org.karar.dev.common.exception.notfound.ResourceNotFoundException;
import org.karar.dev.domain.annotation.UnitTest;
import org.karar.dev.common.dto.BaseResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserUpdateRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@UnitTest
class RegularUserServiceTest {
    @Mock
    private RegularUserRepository regularUserRepository;

    @InjectMocks
    private RegularUserService regularUserService;


    @Test
    @DisplayName("Should return paginated list of regular users successfully")
    void shouldReturnPaginatedUsers() {
        UUID id = UUID.randomUUID();
        RegularUser user = RegularUserBuilder
                .user()
                .withId(id)
                .build();

        PageRequest pageable = PageRequest.of(0, 1);

        when(regularUserRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(user), pageable, 1));


        BaseResponse<PageResponse<RegularUserResponse>> response =
                regularUserService.getAll(pageable);

        assertThat(response)
                .isNotNull()
                .extracting(BaseResponse::getStatus)
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getData().getContent())
                .hasSize(1)
                .first()
                .extracting(
                        RegularUserResponse::id,
                        RegularUserResponse::email,
                        RegularUserResponse::username
                ).containsExactly(id, user.getEmail(), user.getUsername());

        verify(regularUserRepository)
                .findAll(pageable);

    }

    @Test
    @DisplayName("Should return regular user by ID successfully")
    void shouldReturnUserWhenFound() {
        UUID id = UUID.randomUUID();
        RegularUser user = RegularUserBuilder
                .user()
                .withId(id)
                .build();

        when(regularUserRepository.findById(id))
                .thenReturn(Optional.of(user));

        BaseResponse<RegularUserResponse> response =
                regularUserService.getUserById(id);

        assertThat(response)
                .isNotNull()
                .extracting(BaseResponse::getStatus)
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getData())
                .extracting(
                        RegularUserResponse::id,
                        RegularUserResponse::email,
                        RegularUserResponse::username
                )
                .containsExactly(id, user.getEmail(), user.getUsername());

        verify(regularUserRepository).findById(id);
    }

    @Test
    @DisplayName("Should update regular user successfully")
    void updateShouldUpdateAndReturnRegularUserWhenFound() {
        final String emailUpdate = "update@karar.dev";
        final String usernameUpdate = "updated_username";

        UUID id = UUID.randomUUID();
        RegularUser user = RegularUserBuilder.user().withId(id).build();

        RegularUserUpdateRequest updateRequest =
                new RegularUserUpdateRequest(emailUpdate, usernameUpdate, null, null, null, null, null, null, null, null, null, null);


        when(regularUserRepository.findById(id))
                .thenReturn(Optional.of(user));

        when(regularUserRepository.save(any()))
                .thenReturn(user);

        BaseResponse<RegularUserResponse> response =
                regularUserService.update(id, updateRequest);

        assertThat(response.getData().email()).isEqualTo(emailUpdate);
        assertThat(response.getData().username()).isEqualTo(usernameUpdate);

        verify(regularUserRepository).findById(id);
        verify(regularUserRepository).save(user);
    }

    @Test
    @DisplayName("Should throw exception when regular user is not found")
    void shouldThrowWhenUpdatingNonExistingUser() {
        UUID id = UUID.randomUUID();

        when(regularUserRepository.findById(id))
                .thenReturn(Optional.empty());

        // assertThrows(ResourceNotFoundException.class, () -> regularUserService.update(userId, updateRequest));
        assertThatThrownBy(() -> regularUserService.update(id, any()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(regularUserRepository).findById(id);
        verify(regularUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete regular user successfully")
    void shouldDeleteUser() {
        UUID id = UUID.randomUUID();
        when(regularUserRepository.existsById(id))
                .thenReturn(true);

        BaseResponse<Void> response = regularUserService.delete(id);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(regularUserRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when regular user is not found")
    void shouldThrowWhenDeletingNonExistingUser() {
        UUID id = UUID.randomUUID();
        when(regularUserRepository.existsById(id))
                .thenReturn(false);

        assertThatThrownBy(() -> regularUserService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(regularUserRepository).existsById(id);
        verify(regularUserRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should return true if regular user exists")
    void shouldReturnTrueWhenExists() {
        UUID id = UUID.randomUUID();
        when(regularUserRepository.existsById(id))
                .thenReturn(true);

        boolean exists = regularUserService.existsById(id);

        assertThat(exists).isTrue();

        verify(regularUserRepository).existsById(id);
    }

    @Test
    @DisplayName("Should return false if regular user does not exist")
    void shouldReturnUserWhenFound2() {
        UUID id = UUID.randomUUID();
        RegularUser user = RegularUserBuilder.user().withId(id).build();

        when(regularUserRepository.findById(id))
                .thenReturn(Optional.of(user));

        RegularUser response = regularUserService.getById(id);

        assertThat(response)
                .isNotNull()
                .extracting(RegularUser::getId)
                .isEqualTo(id);

        verify(regularUserRepository).findById(id);
    }

    @Test
    @DisplayName("Should return null if regular user does not exist")
    void getByIdShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(regularUserRepository.findById(id))
                .thenReturn(Optional.empty());

        //assertThrows(ResourceNotFoundException.class, () -> regularUserService.getById(userId));
        assertThatThrownBy(() -> regularUserService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(regularUserRepository).findById(id);
    }

}