package cloud.chlora.auth.adapter.out.persistence;

import cloud.chlora.auth.application.port.out.UserRepository;
import cloud.chlora.auth.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }
}