package cloud.chlora.auth.application.usecase;

import cloud.chlora.auth.adapter.in.web.request.LoginRequest;
import cloud.chlora.auth.application.port.out.UserRepository;
import cloud.chlora.auth.application.result.LoginResult;
import cloud.chlora.auth.common.exception.AuthException;
import cloud.chlora.auth.domain.User;
import cloud.chlora.auth.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = User.builder()
                .userId("u-123456")
                .email("test@example.com")
                .password("encoded-password")
                .name("Test User")
                .role(UserRole.USER)
                .build();
    }

    @Test
    @DisplayName("execute should return LoginResult when login with email is successful")
    void execute_shouldReturnResult_whenEmailLoginSuccessful() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mock-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        LoginResult result = loginUseCase.execute(request);

        // Assert
        assertThat(result.token()).isEqualTo("mock-token");
        assertThat(result.response().data().email()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("execute should return LoginResult when login with userId is successful")
    void execute_shouldReturnResult_whenUserIdLoginSuccessful() {
        // Arrange
        LoginRequest request = new LoginRequest("u-123456", "password123");
        when(userRepository.findByUserId("u-123456")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mock-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        LoginResult result = loginUseCase.execute(request);

        // Assert
        assertThat(result.token()).isEqualTo("mock-token");
    }

    @Test
    @DisplayName("execute should throw InvalidCredentialsException when password matches failed")
    void execute_shouldThrowInvalidCredentials_whenPasswordFailed() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "wrong-password");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> loginUseCase.execute(request))
                .isInstanceOf(AuthException.InvalidCredentialsException.class);
    }
}
