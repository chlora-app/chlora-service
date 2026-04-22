package cloud.chlora.management.user.adapter.in.web.request;

import cloud.chlora.management.user.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(

        @Email
        @NotBlank
        String email,

        @NotBlank
        String name,

        @NotNull
        UserRole role
) {}