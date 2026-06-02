package cloud.chlora.auth.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "Username or email is required")
        @Size(min = 5, max = 50, message = "Username or email must be between 5 and 50 characters")
        String userIdOrEmail,

        @NotBlank(message = "Password is required")
        String password
) {}