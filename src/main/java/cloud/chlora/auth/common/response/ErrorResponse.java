package cloud.chlora.auth.common.response;

public record ErrorResponse(
        String code,
        String message
) {}
