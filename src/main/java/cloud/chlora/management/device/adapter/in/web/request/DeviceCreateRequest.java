package cloud.chlora.management.device.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceCreateRequest(

        @NotBlank
        String deviceName,

        @NotBlank
        String deviceType,

        @NotNull
        String clusterId
) {}