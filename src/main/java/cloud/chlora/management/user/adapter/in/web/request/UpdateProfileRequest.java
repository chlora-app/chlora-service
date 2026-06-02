package cloud.chlora.management.user.adapter.in.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @Email(message = "Email format is not valid")
        String email,

        @Size(min = 2, max = 50, message = "Name length must be between 2 and 50")
        String name
) {}