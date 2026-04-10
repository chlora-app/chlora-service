package cloud.chlora.auth.common.exception;

import cloud.chlora.auth.common.enums.AuthErrorCode;
import cloud.chlora.auth.common.response.ErrorResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleRuntime(RuntimeException ex) {
        log.error("Internal server error: ", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(AuthException.UserNotFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleUserNotFound(AuthException.UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(AuthErrorCode.USER_NOT_FOUND.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(AuthException.InvalidCredentialsException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleInvalidCredentials(AuthException.InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(AuthErrorCode.INVALID_CREDENTIALS.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(AuthException.EmailAlreadyRegisteredException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleEmailExists(AuthException.EmailAlreadyRegisteredException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(AuthErrorCode.EMAIL_EXISTS.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(AuthErrorCode.VALIDATION_ERROR.getCode(), message));
    }
}