package cloud.chlora.auth.adapter.in.web.request;

import cloud.chlora.auth.adapter.in.web.validation.PasswordMatch;
import jakarta.validation.constraints.*;

@PasswordMatch
public record RegisterRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        String password,

        @NotBlank(message = "Confirm password is required")
        @Size(min = 8, max = 50, message = "Confirm password must be between 8 and 50 characters")
        String confirmPassword
) {}