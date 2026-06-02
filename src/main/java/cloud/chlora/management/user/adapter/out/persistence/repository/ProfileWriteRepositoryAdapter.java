package cloud.chlora.management.user.adapter.out.persistence.repository;

import cloud.chlora.management.shared.error.UserErrorCode;
import cloud.chlora.management.shared.exception.AppException;
import cloud.chlora.management.user.adapter.in.web.request.UpdateProfileRequest;
import cloud.chlora.management.user.adapter.out.persistence.entity.UserWriteEntity;
import cloud.chlora.management.user.adapter.out.persistence.mapper.UserPersistenceMapper;
import cloud.chlora.management.user.domain.exception.UserNotFoundException;
import cloud.chlora.management.user.domain.model.User;
import cloud.chlora.management.user.domain.port.UserProfileWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ProfileWriteRepositoryAdapter implements UserProfileWriteRepository {

    private final ProfileWriteJpaRepository writeRepository;
    private final ProfileReadJpaRepository readRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User updateProfile(String userId, UpdateProfileRequest request) {
        UserWriteEntity entity = writeRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (request.name()  != null) entity.setName(request.name());
        if (request.email() != null) entity.setEmail(request.email());

        try {
            writeRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException ex) {
            throw AppException.of(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        return readRepository.findByUserId(userId)
                .map(UserPersistenceMapper::toDomain)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public void changePassword(String userId, String hashedPassword) {
        writeRepository.changePassword(userId, hashedPassword);
    }
}