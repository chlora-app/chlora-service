package cloud.chlora.auth.adapter.in.web.response;

import cloud.chlora.auth.domain.UserRole;

import java.time.Instant;

public record RegisterResponse(
        String userId,
        String name,
        String email,
        UserRole role,
        Instant createdAt
) {}
