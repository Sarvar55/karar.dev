package org.karar.dev.domain.user;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.user.regular.RegularUser;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        @Tag("bank")
        @DisplayName("should return true when user exists")
        void shouldReturnTrue() {
            when(userRepository.existsById(id)).thenReturn(true);

            boolean result = userService.existsById(id);

            assertThat(result).isTrue();
            verify(userRepository).existsById(id);
        }

        @Test
        @DisplayName("should return false when user does not exist")
        void shouldReturnFalse() {
            when(userRepository.existsById(id)).thenReturn(false);

            boolean result = userService.existsById(id);

            assertThat(result).isFalse();
            verify(userRepository).existsById(id);
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        private RegularUser user;

        @BeforeEach
        public void setUp() {
            user = new RegularUser();
            user.setId(id);
            when(userRepository.findById(id))
                    .thenReturn(Optional.of(user));
        }

        @Test
        @DisplayName("should return user when found")
        void shouldReturnUser() {

            User result = userService.getById(id);

            assertThat(result).isNotNull();
            assertThat(id).isEqualTo(result.getId());

            verify(userRepository).findById(id);
        }

    }

    @Nested
    @DisplayName("getByIdException")
    class GetByIdException {

        @BeforeEach
        public void setUp() {
            when(userRepository.findById(id))
                    .thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            assertThatThrownBy(() -> userService.getById(id))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository).findById(id);
        }
    }
}