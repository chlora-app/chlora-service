package cloud.chlora.management.user.domain.model;

import java.time.Instant;

/**
 * Domain model — pure Java record.
 * Owned by auth service; management treats this as read-only view.
 */
public record User(
        String userId,
        String email,
        String password,
        String name,
        UserRole role,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public boolean isDeleted() { return deletedAt != null; }
}