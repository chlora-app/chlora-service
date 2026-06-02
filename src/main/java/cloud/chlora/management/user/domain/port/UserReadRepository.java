package cloud.chlora.management.user.domain.port;

import cloud.chlora.management.user.domain.model.User;
import cloud.chlora.management.user.domain.model.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port — read-only access to the shared `users` table.
 * Management never writes directly; writes go through UserAuthClient.
 */
public interface UserReadRepository {

    List<User> findAllActive(
            String search, UserRole role,
            String sortColumn, String sortDirection,
            int limit, int offset
    );

    long countActive(String search, UserRole role);

    List<User> findAllDeleted(
            String search, UserRole role,
            String sortColumn, String sortDirection,
            int limit, int offset
    );

    long countDeleted(String search, UserRole role);

    Optional<User> findByUserId(String userId);
}