package cloud.chlora.management.pot.adapter.in.web.response;

import java.time.Instant;

public record PotUpdateResponse(
        String potId,
        String potName,
        Instant updatedAt
) {}