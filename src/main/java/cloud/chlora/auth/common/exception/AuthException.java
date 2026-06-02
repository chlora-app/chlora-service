package cloud.chlora.auth.common.exception;

public class AuthException extends RuntimeException {

    private AuthException(String message) {
        super(message);
    }

    public static class UserNotFoundException extends AuthException {
        public UserNotFoundException(String identifier) {
            super("User not found with identifier: " + identifier);
        }
    }

    public static class InvalidCredentialsException extends AuthException {
        public InvalidCredentialsException() {
            super("Invalid credentials");
        }
    }

    public static class EmailAlreadyRegisteredException extends AuthException {
        public EmailAlreadyRegisteredException(String email) {
            super("Email already registered: " + email);
        }
    }
}