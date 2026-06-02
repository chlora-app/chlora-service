package cloud.chlora.auth.common.response;

import java.time.Instant;

public record BaseResponse<T>(
        String message,
        Instant timestamp,
        T data
) {}