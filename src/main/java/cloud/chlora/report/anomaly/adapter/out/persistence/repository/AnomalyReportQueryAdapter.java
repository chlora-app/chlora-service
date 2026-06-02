package cloud.chlora.report.anomaly.adapter.out.persistence.repository;

import cloud.chlora.report.anomaly.domain.model.AnomalyReport;
import cloud.chlora.report.anomaly.domain.model.AnomalyReportQuery;
import cloud.chlora.report.anomaly.domain.port.AnomalyReportRepository;
import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.enums.AnomalyType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AnomalyReportQueryAdapter implements AnomalyReportRepository {

    private final EntityManager em;

    private static final String BASE_FROM = """
            FROM anomalies a
            JOIN telemetry t ON t.id         = a.telemetry_id
            JOIN devices   d ON d.device_id  = t.device_id
                             AND d.deleted_at IS NULL
            JOIN pots      p ON p.pot_id     = d.pot_id
                             AND p.deleted_at IS NULL
            WHERE a.detected_at >= :dateFrom
              AND a.detected_at <  :dateTo
              AND (CAST(:potId       AS text) IS NULL OR p.pot_id             = :potId)
              AND (CAST(:anomalyType AS text) IS NULL OR a.anomaly_type::text = :anomalyType)
              AND (CAST(:severity    AS text) IS NULL OR a.severity::text     = :severity)
            """;

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<AnomalyReport> findAll(AnomalyReportQuery query) {
        String sql = """
                SELECT
                    p.pot_name,
                    d.device_name,
                    t.soil_moisture,
                    t.temperature,
                    t.humidity,
                    t.battery_level,
                    t.received_at,
                    ROUND(EXTRACT(EPOCH FROM (t.received_at - t.device_timestamp)) * 1000) AS latency,
                    a.anomaly_type,
                    a.severity,
                    a.anomaly_score
                """ + BASE_FROM +
                "ORDER BY t.received_at " + query.order().toSql() + "\n" +
                "LIMIT :size OFFSET :offset";

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("dateFrom",    query.dateFrom())
                .setParameter("dateTo",      query.dateTo())
                .setParameter("potId",       query.potId())
                .setParameter("anomalyType", query.anomalyType() != null ? query.anomalyType().name() : null)
                .setParameter("severity",    query.severity()    != null ? query.severity().name()    : null)
                .setParameter("size",        query.size())
                .setParameter("offset",      (query.page() - 1) * query.size())
                .getResultList();

        return rows.stream().map(this::toReport).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAll(AnomalyReportQuery query) {
        String sql = "SELECT COUNT(*) " + BASE_FROM;

        Number result = (Number) em.createNativeQuery(sql)
                .setParameter("dateFrom",    query.dateFrom())
                .setParameter("dateTo",      query.dateTo())
                .setParameter("potId",       query.potId())
                .setParameter("anomalyType", query.anomalyType() != null ? query.anomalyType().name() : null)
                .setParameter("severity",    query.severity()    != null ? query.severity().name()    : null)
                .getSingleResult();

        return result.longValue();
    }

    private AnomalyReport toReport(Object[] row) {
        return AnomalyReport.builder()
                .potName(     (String)  row[0])
                .deviceName(  (String)  row[1])
                .soilMoisture(toFloat(  row[2]))
                .temperature( toFloat(  row[3]))
                .humidity(    toFloat(  row[4]))
                .batteryLevel(toInt(    row[5]))
                .timestamp(   toInstant(row[6]))
                .latency(     toLong(   row[7]))
                .anomalyType( AnomalyType.valueOf((String) row[8]))
                .severity(    AnomalySeverity.valueOf((String) row[9]))
                .anomalyScore(toFloat(  row[10]))
                .build();
    }

    private float toFloat(Object val) {
        return val == null ? 0f : ((Number) val).floatValue();
    }

    private int toInt(Object val) {
        return val == null ? 0 : ((Number) val).intValue();
    }

    private long toLong(Object val) {
        return val == null ? 0L : ((Number) val).longValue();
    }

    private Instant toInstant(Object val) {
        if (val == null)                          return null;
        if (val instanceof java.sql.Timestamp ts) return ts.toInstant();
        if (val instanceof OffsetDateTime odt)    return odt.toInstant();
        if (val instanceof Instant i)             return i;
        return null;
    }
}