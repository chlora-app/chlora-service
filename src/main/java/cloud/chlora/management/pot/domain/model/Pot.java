package cloud.chlora.management.pot.domain.model;

import java.time.Instant;

public record Pot(
        Long id,
        String potId,
        String potName,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
    public boolean isDeleted() {
        return deletedAt != null;
    }
}