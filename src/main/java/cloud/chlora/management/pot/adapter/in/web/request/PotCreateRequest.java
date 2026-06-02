package cloud.chlora.management.pot.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;

public record PotCreateRequest(

        @NotBlank
        String potName
) {}