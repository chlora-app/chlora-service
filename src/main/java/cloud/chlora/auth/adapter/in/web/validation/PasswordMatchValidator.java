package cloud.chlora.auth.adapter.in.web.validation;

import cloud.chlora.auth.adapter.in.web.request.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return value.password().equals(value.confirmPassword());
    }
}