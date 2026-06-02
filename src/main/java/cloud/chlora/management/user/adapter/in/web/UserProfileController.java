package cloud.chlora.management.user.adapter.in.web;

import cloud.chlora.management.user.adapter.in.web.request.ChangePasswordRequest;
import cloud.chlora.management.user.adapter.in.web.request.UpdateProfileRequest;
import cloud.chlora.management.user.adapter.in.web.response.ProfileResponse;
import cloud.chlora.management.user.application.usecase.ChangePasswordUseCase;
import cloud.chlora.management.user.application.usecase.GetMyProfileUseCase;
import cloud.chlora.management.user.application.usecase.UpdateProfileUseCase;
import cloud.chlora.management.user.domain.model.User;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserProfileController {

    private final GetMyProfileUseCase getMyProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @GetMapping("/profile")
    public ResponseEntity<@NonNull ProfileResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        User user = getMyProfileUseCase.execute(userId);

        return ResponseEntity.ok(toResponse(user));
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<@NonNull ProfileResponse> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        String userId = jwt.getSubject();
        User user = updateProfileUseCase.execute(userId, request);

        return ResponseEntity.ok(toResponse(user));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<@NonNull Void> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String userId = jwt.getSubject();
        changePasswordUseCase.execute(userId, request);

        return ResponseEntity.noContent().build();
    }

    private ProfileResponse toResponse(User user) {
        return ProfileResponse.builder()
                .userId(user.userId())
                .email(user.email())
                .name(user.name())
                .role(user.role())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }
}