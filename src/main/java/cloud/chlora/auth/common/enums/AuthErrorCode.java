package cloud.chlora.auth.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode {
    VALIDATION_ERROR("AUTH-400"),
    INVALID_CREDENTIALS("AUTH-401"),
    USER_NOT_FOUND("AUTH-404"),
    EMAIL_EXISTS("AUTH-409");

    private final String code;
}