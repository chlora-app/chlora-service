package cloud.chlora.management.user.adapter.out.persistence.repository;

import cloud.chlora.management.user.adapter.out.persistence.mapper.UserPersistenceMapper;
import cloud.chlora.management.user.domain.exception.UserNotFoundException;
import cloud.chlora.management.user.domain.model.User;
import cloud.chlora.management.user.domain.port.UserProfileReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileReadRepositoryAdapter implements UserProfileReadRepository {

    private final ProfileReadJpaRepository repository;

    @Override
    public User getProfile(String userId) {
        return repository.findByUserId(userId)
                .map(UserPersistenceMapper::toDomain)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}