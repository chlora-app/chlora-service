package cloud.chlora.management.pot.adapter.out.persistence.repository;

public interface PotSummaryProjection {
    String getPotId();
    String getPotName();
    boolean getIsMonitored();
}