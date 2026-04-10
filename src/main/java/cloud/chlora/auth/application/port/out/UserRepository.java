package cloud.chlora.auth.application.port.out;

import cloud.chlora.auth.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    User save(User user);
}