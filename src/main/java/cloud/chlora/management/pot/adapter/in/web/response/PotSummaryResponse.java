package cloud.chlora.management.pot.adapter.in.web.response;

public record PotSummaryResponse(
        String potId,
        String potName,
        boolean isMonitored
) {}