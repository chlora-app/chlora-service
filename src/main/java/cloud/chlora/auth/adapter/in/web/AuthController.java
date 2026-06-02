package cloud.chlora.auth.adapter.in.web;

import cloud.chlora.auth.application.result.LoginResult;
import cloud.chlora.auth.application.usecase.LoginUseCase;
import cloud.chlora.auth.application.usecase.RegisterUserUseCase;
import cloud.chlora.auth.adapter.in.web.request.LoginRequest;
import cloud.chlora.auth.adapter.in.web.request.RegisterRequest;
import cloud.chlora.auth.adapter.in.web.response.LoginResponse;
import cloud.chlora.auth.adapter.in.web.response.RegisterResponse;
import cloud.chlora.auth.common.response.BaseResponse;
import cloud.chlora.shared.util.CookieAuth;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("/login")
    public ResponseEntity<@NonNull BaseResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        LoginResult result = loginUseCase.execute(request);
        response.addHeader(HttpHeaders.SET_COOKIE, CookieAuth.accessTokenCookie(result.token()));

        return ResponseEntity.ok(result.response());
    }

    @PostMapping("/register")
    public ResponseEntity<@NonNull BaseResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {

        BaseResponse<RegisterResponse> response = registerUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<@NonNull Void> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, CookieAuth.clearAccessTokenCookie());
        return ResponseEntity.noContent().build();
    }
}