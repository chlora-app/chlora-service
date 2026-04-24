package cloud.chlora.auth.application.usecase;

import cloud.chlora.auth.adapter.in.web.request.RegisterRequest;
import cloud.chlora.auth.adapter.in.web.response.RegisterResponse;
import cloud.chlora.auth.application.port.out.UserRepository;
import cloud.chlora.auth.common.exception.AuthException;
import cloud.chlora.auth.common.response.BaseResponse;
import cloud.chlora.auth.domain.User;
import cloud.chlora.auth.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    @DisplayName("execute should successfully register a new user")
    void execute_shouldRegisterUser() {
        // Arrange
        RegisterRequest request = new RegisterRequest("New User", "new@example.com", "password123", "password123");
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        User savedUser = User.builder()
                .userId("u-abcdef")
                .name("New User")
                .email("new@example.com")
                .role(UserRole.USER)
                .createdAt(Instant.now())
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        BaseResponse<RegisterResponse> response = registerUserUseCase.execute(request);

        // Assert
        assertThat(response.data().email()).isEqualTo("new@example.com");
        assertThat(response.data().role()).isEqualTo(UserRole.USER);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("execute should throw EmailAlreadyRegisteredException when email exists")
    void execute_shouldThrowException_whenEmailExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("New User", "existing@example.com", "password123", "password123");
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(User.builder().build()));

        // Act & Assert
        assertThatThrownBy(() -> registerUserUseCase.execute(request))
                .isInstanceOf(AuthException.EmailAlreadyRegisteredException.class);
    }
}
