package cloud.chlora.management.pot.adapter.in.web.response;

import java.time.Instant;

public record PotCreateResponse(
        String potId,
        String potName,
        Instant createdAt
) {}