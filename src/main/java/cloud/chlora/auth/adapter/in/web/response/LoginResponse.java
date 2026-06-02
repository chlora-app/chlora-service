package cloud.chlora.auth.adapter.in.web.response;

import cloud.chlora.auth.domain.UserRole;

public record LoginResponse(
        String name,
        String email,
        UserRole role
) {}
