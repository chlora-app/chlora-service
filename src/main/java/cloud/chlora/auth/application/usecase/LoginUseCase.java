package cloud.chlora.auth.application.usecase;

import cloud.chlora.auth.adapter.in.web.request.LoginRequest;
import cloud.chlora.auth.adapter.in.web.response.LoginResponse;
import cloud.chlora.auth.application.result.LoginResult;
import cloud.chlora.auth.application.port.out.UserRepository;
import cloud.chlora.auth.common.exception.AuthException;
import cloud.chlora.auth.common.response.BaseResponse;
import cloud.chlora.auth.domain.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private static final Logger log = LoggerFactory.getLogger(LoginUseCase.class);
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public LoginResult execute(LoginRequest request) {
        String rawIdentifier = request.userIdOrEmail().trim();
        boolean byEmail = EMAIL_PATTERN.matcher(rawIdentifier).matches();

        String identifier = byEmail ? rawIdentifier.toLowerCase() : rawIdentifier;
        String loginMethod = byEmail ? "email" : "userId";

        log.info("event=login_attempt login_method={} user_identifier={}", loginMethod, identifier);

        User user = (byEmail ? userRepository.findByEmail(identifier) : userRepository.findByUserId(identifier))
                .orElseThrow(() -> {
                    log.warn("event=login_failed reason=user_not_found login_method={} user_identifier={}", loginMethod, identifier);
                    return new AuthException.InvalidCredentialsException();
                });

        if (user.isDeleted()) {
            log.warn("event=login_failed reason=user_deleted login_method={} user_identifier={}", loginMethod, identifier);
            throw new AuthException.InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("event=login_failed reason=invalid_password login_method={} user_identifier={} user_id={}",
                    loginMethod, identifier, user.getUserId());
            throw new AuthException.InvalidCredentialsException();
        }

        String token = generateAccessToken(user);
        log.info("event=login_success login_method={} user_identifier={} user_id={}", loginMethod, identifier, user.getUserId());

        BaseResponse<LoginResponse> response = new BaseResponse<>(
                "User " + user.getUserId() + " logged in successfully.",
                Instant.now(),
                new LoginResponse(user.getName(), user.getEmail(), user.getRole())
        );

        return new LoginResult(response, token);
    }

    private String generateAccessToken(User user) {
        Instant now = Instant.now();
        JwsHeader headers = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .subject(user.getUserId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60 * 60 * 8))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(headers, claims)).getTokenValue();
    }
}