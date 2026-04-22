package cloud.chlora.management.user.adapter.in.web.request;

import cloud.chlora.management.user.domain.model.UserRole;
import jakarta.validation.constraints.Email;

public record UserUpdateRequest(

        @Email
        String email,

        String name,

        UserRole role
) {}