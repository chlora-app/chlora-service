package cloud.chlora.management.user.adapter.in.web.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "Old password can not be empty")
        String oldPassword,

        @NotBlank(message = "New password can not be empty")
        @Size(min = 8, message = "New password length must be longer than 7 characters")
        String newPassword,

        @NotBlank(message = "Confirm password can not be empty")
        String confirmNewPassword

) {
    @AssertTrue(message = "Confirm password does not match")
    public boolean isPasswordConfirmed() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }
}