package org.karar.dev.domain.user.regular;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.dto.PageResponse;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.base.BaseResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserResponse;
import org.karar.dev.domain.user.regular.dto.RegularUserUpdateRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegularUserServiceTest {
    @Mock
    private RegularUserRepository regularUserRepository;

    @InjectMocks
    private RegularUserService regularUserService;

    private UUID userId;
    private String email;
    private String username;
    private RegularUser regularUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        email = "user@karar.dev";
        username = "user";
        regularUser = new RegularUser();

        regularUser.setId(userId);
        regularUser.setEmail(email);
        regularUser.setUsername(username);
    }

    @Test
    @DisplayName("Should return paginated list of regular users successfully")
    void getAllShouldReturnAllRegularUsers() {
        PageRequest pageable = PageRequest.of(0, 1);
        when(regularUserRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(regularUser), pageable, 1));

        BaseResponse<PageResponse<RegularUserResponse>> response =
                regularUserService.getAll(pageable);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(1, response.getData().getSize());
        assertEquals(userId, response.getData().getContent().get(0).id());
        assertEquals(email, response.getData().getContent().get(0).email());
        assertEquals(username, response.getData().getContent().get(0).username());
        verify(regularUserRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should return regular user by ID successfully")
    void getUserByIdShouldReturnRegularUserWhenFound() {

        when(regularUserRepository.findById(userId))
                .thenReturn(Optional.of(regularUser));

        BaseResponse<RegularUserResponse> response =
                regularUserService.getUserById(userId);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(userId, response.getData().id());
        assertEquals(email, response.getData().email());
        assertEquals(username, response.getData().username());

        verify(regularUserRepository).findById(userId);
    }

    @Test
    @DisplayName("Should update regular user successfully")
    void updateShouldUpdateAndReturnRegularUserWhenFound() {
        final String emailUpdate = "update@karar.dev";
        final String usernameUpdate = "updated_username";
        RegularUserUpdateRequest updateRequest =
                new RegularUserUpdateRequest(emailUpdate, usernameUpdate);

        when(regularUserRepository.findById(userId))
                .thenReturn(Optional.of(regularUser));

        when(regularUserRepository.save(regularUser))
                .thenReturn(regularUser);

        BaseResponse<RegularUserResponse> response =
                regularUserService.update(userId, updateRequest);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(userId, response.getData().id());
        assertEquals(emailUpdate, response.getData().email());
        assertEquals(usernameUpdate, response.getData().username());

        verify(regularUserRepository).findById(userId);
        verify(regularUserRepository).save(regularUser);
    }

    @Test
    @DisplayName("Should throw exception when regular user is not found")
    void updateShouldThrowExceptionWhenUserNotFound() {
        final String emailUpdate = "update@karar.dev";
        final String usernameUpdate = "updated_username";
        RegularUserUpdateRequest updateRequest =
                new RegularUserUpdateRequest(emailUpdate, usernameUpdate);

        when(regularUserRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> regularUserService.update(userId, updateRequest));
        verify(regularUserRepository).findById(userId);
        verify(regularUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete regular user successfully")
    void deleteShouldDeleteRegularUserWhenFound() {
        when(regularUserRepository.existsById(userId))
                .thenReturn(true);

        BaseResponse<Void> response = regularUserService.delete(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        verify(regularUserRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw exception when regular user is not found")
    void deleteShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        when(regularUserRepository.existsById(userId))
                .thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> regularUserService.delete(userId));

        verify(regularUserRepository).existsById(userId);
        verify(regularUserRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return true if regular user exists")
    void existsByIdShouldReturnTrueWhenUserExists() {
        when(regularUserRepository.existsById(userId)).thenReturn(true);

        boolean exists = regularUserService.existsById(userId);

        assertTrue(exists);
        verify(regularUserRepository).existsById(userId);
    }

    @Test
    @DisplayName("Should return false if regular user does not exist")
    void getByIdShouldReturnRegularUserWhenFound() {
        when(regularUserRepository.findById(userId))
                .thenReturn(Optional.of(regularUser));

        RegularUser user = regularUserService.getById(userId);
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(userId, user.getId());
        assertEquals(username, user.getUsername());
        verify(regularUserRepository).findById(userId);
    }
    @Test
    @DisplayName("Should return null if regular user does not exist")
    void getByIdShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        when(regularUserRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> regularUserService.getById(userId));

        verify(regularUserRepository).findById(userId);
    }

}