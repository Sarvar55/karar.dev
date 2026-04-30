package org.karar.dev.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.user.regular.RegularUser;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
    }

    @Nested
    @DisplayName("existsById")
    class ExistsById {

        @Test
        @DisplayName("should return true when user exists")
        void shouldReturnTrue() {
            when(userRepository.existsById(id)).thenReturn(true);

            boolean result = userService.existsById(id);

            assertTrue(result);
            verify(userRepository).existsById(id);
        }

        @Test
        @DisplayName("should return false when user does not exist")
        void shouldReturnFalse() {
            when(userRepository.existsById(id)).thenReturn(false);

            boolean result = userService.existsById(id);

            assertFalse(result);
            verify(userRepository).existsById(id);
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("should return user when found")
        void shouldReturnUser() {
            User user = new RegularUser();
            user.setId(id);

            when(userRepository.findById(id))
                    .thenReturn(Optional.of(user));

            User result = userService.getById(id);

            assertNotNull(result);
            assertEquals(id, result.getId());

            verify(userRepository).findById(id);
        }

        @Test
        @DisplayName("should throw exception when user not found")
        void shouldThrowException() {
            when(userRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> userService.getById(id));

            verify(userRepository).findById(id);
        }
    }
}