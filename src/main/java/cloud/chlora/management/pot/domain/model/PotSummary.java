package cloud.chlora.management.pot.domain.model;

public record PotSummary(
        String potId,
        String potName,
        boolean isMonitored
) {}