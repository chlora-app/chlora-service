package cloud.chlora.management.user.adapter.out.persistence.repository;

import cloud.chlora.management.shared.error.UserErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.user.adapter.in.web.request.UserCreateRequest;
import cloud.chlora.management.user.adapter.in.web.request.UserUpdateRequest;
import cloud.chlora.management.user.adapter.out.persistence.entity.UserWriteEntity;
import cloud.chlora.management.user.domain.model.User;
import cloud.chlora.management.user.domain.port.UserWriteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class UserWriteRepositoryAdapter implements UserWriteRepository {

    private final UserWriteJpaRepository repository;
    private final PasswordEncoder        passwordEncoder;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User create(UserCreateRequest request) {
        String rawPassword = "chlora_" + request.name().split(" ")[0];

        int attempt = 1;
        while (attempt <= 3) {
            UserWriteEntity entity = UserWriteEntity.builder()
                    .email(request.email())
                    .password(passwordEncoder.encode(rawPassword))
                    .name(request.name())
                    .role(request.role())
                    .userId("U-" + RandomStringUtils.secure().nextAlphanumeric(8).toLowerCase())
                    .build();

            try {
                return toDomain(repository.saveAndFlush(entity));
            } catch (DataIntegrityViolationException e) {
                if (isEmailConflict(e)) {
                    throw AppException.of(UserErrorCode.EMAIL_ALREADY_EXISTS);
                }

                if (attempt == 3) {
                    throw AppException.of(UserErrorCode.USER_ID_GENERATION_FAILED);
                }

                attempt++;
            }
        }

        throw AppException.of(UserErrorCode.USER_ID_GENERATION_FAILED);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User update(String userId, UserUpdateRequest request) {
        UserWriteEntity entity = repository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        if (request.email() != null) entity.setEmail(request.email());
        if (request.name()  != null) entity.setName(request.name());
        if (request.role()  != null) entity.setRole(request.role());

        try {
            return toDomain(repository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException e) {
            throw AppException.of(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void softDelete(String userId) {
        repository.softDelete(userId, Instant.now());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void restore(String userId) {
        repository.restore(userId, Instant.now());
    }

    // ── helper ────────────────────────────────────────────────────────────────
    private User toDomain(UserWriteEntity e) {
        return new User(
                e.getId(), e.getUserId(), e.getEmail(), e.getPassword(),
                e.getName(), e.getRole(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt()
        );
    }

    private boolean isEmailConflict(DataIntegrityViolationException e) {
        String message = e.getMessage();
        return message != null && message.contains("email");
    }
}