package cloud.chlora.management.user.application.usecase;

import cloud.chlora.management.shared.error.UserErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.user.adapter.in.web.request.UserCreateRequest;
import cloud.chlora.management.user.adapter.in.web.request.UserUpdateRequest;
import cloud.chlora.management.user.adapter.in.web.response.*;
import cloud.chlora.management.user.application.port.in.UserUseCase;
import cloud.chlora.management.user.domain.model.User;
import cloud.chlora.management.user.domain.model.UserRole;
import cloud.chlora.management.user.domain.port.UserWriteRepository;
import cloud.chlora.management.user.domain.port.UserReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserReadRepository readRepository;
    private final UserWriteRepository writeRepository;

    // ── Queries (read directly from shared DB) ────────────────────────────────
    @Override
    public PagedUserResponse<UserGetResponse> findAllActive(
            int page, int size, String search, String sort, String order, UserRole role
    ) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int offset   = (safePage - 1) * safeSize;

        List<UserGetResponse> content = readRepository
                .findAllActive(search, role, resolveColumn(sort), resolveDir(order), safeSize, offset)
                .stream().map(this::toGetResponse).toList();

        long total      = readRepository.countActive(search, role);
        int  totalPages = (int) Math.ceil((double) total / safeSize);

        return new PagedUserResponse<>(total, safePage, safeSize, totalPages, content);
    }

    @Override
    public PagedUserResponse<UserDeletedResponse> findAllDeleted(
            int page, int size, String search, String sort, String order, UserRole role
    ) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int offset   = (safePage - 1) * safeSize;

        List<UserDeletedResponse> content = readRepository
                .findAllDeleted(search, role, resolveColumn(sort), resolveDir(order), safeSize, offset)
                .stream()
                .map(u -> new UserDeletedResponse(u.userId(), u.email(), u.name(), u.role(), u.deletedAt()))
                .toList();

        long total      = readRepository.countDeleted(search, role);
        int  totalPages = (int) Math.ceil((double) total / safeSize);

        return new PagedUserResponse<>(total, safePage, safeSize, totalPages, content);
    }

    @Override
    public UserGetResponse findByUserId(String userId) {
        User user = requireUser(userId);
        if (user.isDeleted()) throw AppException.of(UserErrorCode.USER_ALREADY_DELETED);
        return toGetResponse(user);
    }

    // ── Commands (delegated to auth service over HTTP) ────────────────────────

    @Override
    public UserCreateResponse createUser(UserCreateRequest request) {
        User created = writeRepository.create(request);
        log.info("[UserUseCase] created user email={}", created.email());
        return new UserCreateResponse(created.email(), created.name(), created.role(), created.createdAt());
    }

    @Override
    public UserUpdateResponse updateUser(String userId, UserUpdateRequest request) {
        if (request.email() == null && request.name() == null && request.role() == null) {
            throw AppException.of(UserErrorCode.USER_PATCH_EMPTY);
        }
        User updated = writeRepository.update(userId, request);
        log.info("[UserUseCase] updated userId={}", userId);
        return new UserUpdateResponse(updated.userId(), updated.email(), updated.name(), updated.role(), updated.updatedAt());
    }

    @Override
    public void deleteUser(String userId) {
        User user = requireUser(userId);
        if (user.isDeleted()) throw AppException.of(UserErrorCode.USER_ALREADY_DELETED);
        writeRepository.softDelete(userId);
        log.info("[UserUseCase] soft-deleted userId={}", userId);
    }

    @Override
    public void restoreUser(String userId) {
        User user = requireUser(userId);
        if (!user.isDeleted()) throw AppException.of(UserErrorCode.USER_IS_ACTIVE);
        writeRepository.restore(userId);
        log.info("[UserUseCase] restored userId={}", userId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private User requireUser(String userId) {
        return readRepository.findByUserId(userId)
                .orElseThrow(() -> AppException.of(UserErrorCode.USER_NOT_FOUND));
    }

    private UserGetResponse toGetResponse(User u) {
        return new UserGetResponse(u.userId(), u.email(), u.name(), u.role(), u.createdAt(), u.updatedAt());
    }

    private String resolveColumn(String sort) {
        return switch (sort == null ? "" : sort) {
            case "name" -> "name";
            case "email" -> "email";
            case "role" -> "role";
            default -> "createdAt";
        };
    }

    private String resolveDir(String order) {
        return "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
    }
}