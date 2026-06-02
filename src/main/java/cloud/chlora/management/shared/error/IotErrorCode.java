package cloud.chlora.management.shared.error;

import org.springframework.http.HttpStatus;

public enum IotErrorCode implements AppErrorCode {

    DEVICE_STATUS_INVALID(        "DVC-400-001", HttpStatus.BAD_REQUEST,  "device",     "Invalid device status"),
    DEVICE_UPDATE_EMPTY(          "DVC-400-002", HttpStatus.BAD_REQUEST,  "device",     "Device update request is empty"),
    DEVICE_REQUEST_INVALID(       "DVC-400-003", HttpStatus.BAD_REQUEST,  "device",     "Invalid device request"),
    DEVICE_NOT_FOUND(             "DVC-404-001", HttpStatus.NOT_FOUND,    "device",     "Device not found"),
    DEVICE_NOT_FOUND_AFTER_UPDATE("DVC-404-002", HttpStatus.NOT_FOUND,    "device",     "Device not found after update"),
    DEVICE_UPDATE_FAILED(         "DVC-409-001", HttpStatus.CONFLICT,     "device",     "Device update failed"),
    DEVICE_ALREADY_DELETED(       "DVC-409-002", HttpStatus.CONFLICT,     "device",     "Device already deleted"),

    POT_UPDATE_EMPTY(             "POT-400-001", HttpStatus.BAD_REQUEST,  "pot",        "Pot update request is empty"),
    POT_REQUEST_EMPTY(            "POT-400-002", HttpStatus.BAD_REQUEST,  "pot",        "Pot request is empty"),
    POT_NOT_FOUND(                "POT-404-001", HttpStatus.NOT_FOUND,    "pot",        "Pot not found"),
    POT_NAME_ALREADY_EXISTS(      "POT-409-001", HttpStatus.CONFLICT,     "pot",        "Pot name already exists"),
    POT_ALREADY_DELETED(          "POT-409-002", HttpStatus.CONFLICT,     "pot",        "Pot already deleted"),
    POT_ALREADY_ASSIGNED(         "POT-409-003", HttpStatus.CONFLICT,     "pot",        "Pot already assigned to another device"),

    PAGE_LOWER_THAN_ONE(          "GEN-400-001", HttpStatus.BAD_REQUEST,  "pagination", "Page must be greater than 0"),
    SIZE_LOWER_THAN_ONE(          "GEN-400-002", HttpStatus.BAD_REQUEST,  "pagination", "Size must be greater than 0");

    private final String code;
    private final HttpStatus status;
    private final String domain;
    private final String message;

    IotErrorCode(String code, HttpStatus status, String domain, String message) {
        this.code    = code;
        this.status  = status;
        this.domain  = domain;
        this.message = message;
    }

    @Override public String     code()    { return code; }
    @Override public String     message() { return message; }
    @Override public HttpStatus status()  { return status; }
    @Override public String     domain()  { return domain; }
}