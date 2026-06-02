package cloud.chlora.auth.application.usecase;

import cloud.chlora.auth.adapter.in.web.request.RegisterRequest;
import cloud.chlora.auth.adapter.in.web.response.RegisterResponse;
import cloud.chlora.auth.application.port.out.UserRepository;
import cloud.chlora.auth.common.exception.AuthException;
import cloud.chlora.auth.common.response.BaseResponse;
import cloud.chlora.auth.domain.User;
import cloud.chlora.auth.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterUserUseCase.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BaseResponse<RegisterResponse> execute(RegisterRequest request) {
        String cleanEmail = request.email().trim().toLowerCase();
        String maskedEmail = maskEmail(cleanEmail);

        log.info("event=register_attempt email={}", maskedEmail);

        if (userRepository.findByEmail(cleanEmail).isPresent()) {
            log.warn("event=register_failed reason=email_exists email={}", maskedEmail);
            throw new AuthException.EmailAlreadyRegisteredException(cleanEmail);
        }

        User user = User.builder()
                .name(request.name())
                .email(cleanEmail)
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.USER)
                .build();

        User saved = userRepository.save(user);
        log.info("event=register_success userId={} email={}", saved.getUserId(), maskedEmail);

        return new BaseResponse<>(
                "User " + saved.getUserId() + " registered successfully.",
                Instant.now(),
                new RegisterResponse(
                        saved.getUserId(),
                        saved.getName(),
                        saved.getEmail(),
                        saved.getRole(),
                        saved.getCreatedAt()
                )
        );
    }

    private String maskEmail(String email) {
        return email.replaceAll("(^.).*(@.*$)", "$1***$2");
    }
}