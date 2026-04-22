package cloud.chlora.management.user.adapter.in.web;

import cloud.chlora.management.user.adapter.in.web.request.UserCreateRequest;
import cloud.chlora.management.user.adapter.in.web.request.UserUpdateRequest;
import cloud.chlora.management.user.adapter.in.web.response.*;
import cloud.chlora.management.user.application.port.in.UserUseCase;
import cloud.chlora.management.user.domain.model.UserRole;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @GetMapping
    public ResponseEntity<@NonNull PagedUserResponse<UserGetResponse>> findAll(
            @RequestParam(defaultValue = "1")          int page,
            @RequestParam(defaultValue = "10")         int size,
            @RequestParam(required = false)            String search,
            @RequestParam(defaultValue = "created_at") String sort,
            @RequestParam(defaultValue = "asc")        String order,
            @RequestParam(required = false)            UserRole role
    ) {
        return ResponseEntity.ok(userUseCase.findAllActive(page, size, search, sort, order, role));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<@NonNull UserGetResponse> findOne(@PathVariable String userId) {
        return ResponseEntity.ok(userUseCase.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<@NonNull UserCreateResponse> create(
            @Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userUseCase.createUser(request));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<@NonNull UserUpdateResponse> update(
            @PathVariable String userId,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userUseCase.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<@NonNull Void> delete(@PathVariable String userId) {
        userUseCase.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    public ResponseEntity<@NonNull PagedUserResponse<UserDeletedResponse>> findDeleted(
            @RequestParam(defaultValue = "1")          int page,
            @RequestParam(defaultValue = "10")         int size,
            @RequestParam(required = false)            String search,
            @RequestParam(defaultValue = "created_at") String sort,
            @RequestParam(defaultValue = "asc")        String order,
            @RequestParam(required = false)            UserRole role
    ) {
        return ResponseEntity.ok(userUseCase.findAllDeleted(page, size, search, sort, order, role));
    }

    @PostMapping("/deleted/{userId}/restore")
    public ResponseEntity<@NonNull Void> restore(@PathVariable String userId) {
        userUseCase.restoreUser(userId);
        return ResponseEntity.noContent().build();
    }
}