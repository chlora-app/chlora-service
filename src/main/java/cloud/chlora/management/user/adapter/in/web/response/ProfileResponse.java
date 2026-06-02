package cloud.chlora.management.user.adapter.in.web.response;

import cloud.chlora.management.user.domain.model.UserRole;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ProfileResponse(
        String userId,
        String email,
        String name,
        UserRole role,
        Instant createdAt,
        Instant updatedAt
) {}