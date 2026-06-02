package cloud.chlora.management.user.adapter.in.web.response;

import cloud.chlora.management.user.domain.model.UserRole;

import java.time.Instant;

public record UserCreateResponse(
        String email,
        String name,
        UserRole role,
        Instant createdAt
) {}