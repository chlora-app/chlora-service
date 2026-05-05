package cloud.chlora.pipeline.dashboard.adapter.out.persistence;

import cloud.chlora.pipeline.dashboard.domain.model.DashboardSnapshot;
import cloud.chlora.pipeline.dashboard.domain.port.DashboardQueryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DashboardQueryAdapter implements DashboardQueryRepository {

    private final EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public DashboardSnapshot getSnapshot() {
        List<DashboardSnapshot.PotCard> potCards = fetchPotCards();
        DashboardSnapshot.PotStatus potStatus = buildPotStatus(potCards);
        DashboardSnapshot.AnomalySummary anomalySummary = fetchAnomalySummary();
        return new DashboardSnapshot(potStatus, anomalySummary, potCards);
    }

    @SuppressWarnings("unchecked")
    private List<DashboardSnapshot.PotCard> fetchPotCards() {
        String sql = """
                SELECT
                    p.pot_id,
                    p.pot_name,
                    (d_online.pot_id IS NOT NULL)   AS is_online,
                    COALESCE(lt.temperature, 0.0)   AS temperature,
                    COALESCE(lt.battery_level, 0.0) AS battery_level,
                    COALESCE(lt.soil_moisture, 0.0) AS soil_moisture,
                    COALESCE(lt.humidity, 0.0)      AS humidity,
                    COALESCE(ac.anomaly_count, 0)   AS anomaly_count,
                    lt.received_at                  AS last_updated
                FROM pots p
                LEFT JOIN LATERAL (
                    SELECT t.temperature, t.battery_level, t.soil_moisture, t.humidity, t.received_at
                    FROM telemetry t
                    INNER JOIN devices d ON d.device_id = t.device_id
                        AND d.pot_id = p.pot_id
                        AND d.deleted_at IS NULL
                    WHERE t.is_valid = true
                    ORDER BY t.received_at DESC
                    LIMIT 1
                ) lt ON TRUE
                LEFT JOIN (
                    SELECT d.pot_id
                    FROM devices d
                    WHERE d.status = 'ONLINE' AND d.deleted_at IS NULL
                    GROUP BY d.pot_id
                ) d_online ON d_online.pot_id = p.pot_id
                LEFT JOIN (
                    SELECT d.pot_id, COUNT(a.id) AS anomaly_count
                    FROM anomalies a
                    INNER JOIN telemetry t ON t.id = a.telemetry_id
                    INNER JOIN devices d   ON d.device_id = t.device_id AND d.deleted_at IS NULL
                    WHERE a.detected_at >= date_trunc('day', CURRENT_TIMESTAMP)
                    GROUP BY d.pot_id
                ) ac ON ac.pot_id = p.pot_id
                WHERE p.deleted_at IS NULL
                ORDER BY p.pot_name
                """;

        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        return rows.stream().map(row -> new DashboardSnapshot.PotCard(
                (String) row[0],
                (String) row[1],
                (Boolean) row[2],
                toFloat(row[3]),
                toFloat(row[4]),
                toFloat(row[5]),
                toFloat(row[6]),
                ((Number) row[7]).intValue(),
                toInstant(row[8])
        )).toList();
    }

    private DashboardSnapshot.PotStatus buildPotStatus(List<DashboardSnapshot.PotCard> potCards) {
        int online = (int) potCards.stream().filter(DashboardSnapshot.PotCard::isOnline).count();
        return new DashboardSnapshot.PotStatus(online, potCards.size() - online);
    }

    @SuppressWarnings("unchecked")
    private DashboardSnapshot.AnomalySummary fetchAnomalySummary() {
        String countSql = """
                SELECT
                    COUNT(*) FILTER (WHERE detected_at >= date_trunc('day', CURRENT_TIMESTAMP))
                        AS today_current,
                    COUNT(*) FILTER (WHERE detected_at >= date_trunc('day', CURRENT_TIMESTAMP) - INTERVAL '1 day'
                                      AND  detected_at <  date_trunc('day', CURRENT_TIMESTAMP))
                        AS today_previous,
                    COUNT(*) FILTER (WHERE detected_at >= date_trunc('week', CURRENT_TIMESTAMP))
                        AS week_current,
                    COUNT(*) FILTER (WHERE detected_at >= date_trunc('week', CURRENT_TIMESTAMP) - INTERVAL '7 days'
                                      AND  detected_at <  date_trunc('week', CURRENT_TIMESTAMP))
                        AS week_previous
                FROM anomalies
                """;

        Object[] counts = (Object[]) em.createNativeQuery(countSql).getSingleResult();

        String lastSql = """
                SELECT p.pot_id, p.pot_name, a.detected_at
                FROM anomalies a
                INNER JOIN telemetry t ON t.id        = a.telemetry_id
                INNER JOIN devices  d ON d.device_id  = t.device_id AND d.deleted_at IS NULL
                INNER JOIN pots     p ON p.pot_id     = d.pot_id    AND p.deleted_at IS NULL
                ORDER BY a.detected_at DESC
                LIMIT 1
                """;

        List<Object[]> lastRows = em.createNativeQuery(lastSql).getResultList();

        DashboardSnapshot.AnomalySummary.LastDetected lastDetected = lastRows.isEmpty() ? null
                : new DashboardSnapshot.AnomalySummary.LastDetected(
                        (String) lastRows.getFirst()[0],
                        (String) lastRows.getFirst()[1],
                        toInstant(lastRows.getFirst()[2]));

        return new DashboardSnapshot.AnomalySummary(
                new DashboardSnapshot.AnomalySummary.Today(
                        ((Number) counts[0]).intValue(),
                        ((Number) counts[1]).intValue()),
                new DashboardSnapshot.AnomalySummary.ThisWeek(
                        ((Number) counts[2]).intValue(),
                        ((Number) counts[3]).intValue()),
                lastDetected);
    }

    private float toFloat(Object val) {
        return val == null ? 0f : ((Number) val).floatValue();
    }

    private Instant toInstant(Object val) {
        if (val == null) return null;
        if (val instanceof java.sql.Timestamp ts) return ts.toInstant();
        if (val instanceof OffsetDateTime odt) return odt.toInstant();
        if (val instanceof Instant i) return i;
        return null;
    }
}
