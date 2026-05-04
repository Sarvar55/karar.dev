package org.karar.dev.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.karar.dev.common.exception.notFound.ResourceNotFoundException;
import org.karar.dev.domain.annotation.UnitTest;
import org.karar.dev.domain.extensions.GlobalTestExtension;
import org.karar.dev.domain.user.regular.RegularUser;
import org.karar.dev.domain.extensions.UserTestDataExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ UserTestDataExtension.class})
@UnitTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("existsById")
    class ExistsById {

        @Test
        @DisplayName("should return true when user exists")
        void shouldReturnTrue(UUID id) {
            when(userRepository.existsById(id)).thenReturn(true);

            boolean result = userService.existsById(id);

            assertThat(result).isTrue();
            verify(userRepository).existsById(id);
        }

        @Test
        @DisplayName("should return false when user does not exist")
        void shouldReturnFalse(UUID id) {
            when(userRepository.existsById(id)).thenReturn(false);

            boolean result = userService.existsById(id);
            assertThat(result).isFalse();
            verify(userRepository).existsById(id);
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("should return user when found")
        void shouldReturnUser(UUID id) {
            User user = new RegularUser();
            user.setId(id);

            when(userRepository.findById(id))
                    .thenReturn(Optional.of(user));

            User result = userService.getById(id);

            assertThat(result)
                    .isNotNull()
                    .extracting(User::getId)
                    .isEqualTo(id);

            verify(userRepository).findById(id);
        }

        @Test
        @DisplayName("should throw exception when user not found")
        void shouldThrowException(UUID id) {
            when(userRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getById(id))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository).findById(id);
        }
    }
}