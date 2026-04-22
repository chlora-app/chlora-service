package cloud.chlora.management.user.adapter.out.persistence.repository;

import cloud.chlora.management.user.adapter.out.persistence.mapper.UserPersistenceMapper;
import cloud.chlora.management.user.domain.model.User;
import cloud.chlora.management.user.domain.model.UserRole;
import cloud.chlora.management.user.domain.port.UserReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserReadRepositoryAdapter implements UserReadRepository {

    private final UserReadJpaRepository repository;

    @Override
    public List<User> findAllActive(
            String search, UserRole role, String sortColumn, String sortDirection, int limit, int offset
    ) {
        var pageable = buildPageable(limit, offset, sortColumn, sortDirection);
        return repository.findAllActive(normalize(search), toRoleString(role), pageable)
                .map(UserPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public long countActive(String search, UserRole role) {
        return repository.findAllActive(normalize(search), toRoleString(role), PageRequest.of(0, 1))
                .getTotalElements();
    }

    @Override
    public List<User> findAllDeleted(
            String search, UserRole role,
            String sortColumn, String sortDirection,
            int limit, int offset
    ) {
        var pageable = buildPageable(limit, offset, sortColumn, sortDirection);
        return repository.findAllDeleted(normalize(search), toRoleString(role), pageable)
                .map(UserPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public long countDeleted(String search, UserRole role) {
        return repository.findAllDeleted(normalize(search), toRoleString(role), PageRequest.of(0, 1))
                .getTotalElements();
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        return repository.findByUserId(userId).map(UserPersistenceMapper::toDomain);
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private PageRequest buildPageable(int limit, int offset, String col, String dir) {
        var direction = "DESC".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(offset / limit, limit, Sort.by(direction, toColumnName(col)));
    }

    private String toColumnName(String col) {
        return switch (col) {
            case "userId"    -> "user_id";
            case "createdAt" -> "created_at";
            case "updatedAt" -> "updated_at";
            default          -> col;
        };
    }

    private String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private String toRoleString(UserRole role) {
        return role == null ? null : role.name();
    }
}