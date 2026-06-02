package cloud.chlora.pipeline.dashboard.adapter.out.persistence;

import cloud.chlora.pipeline.dashboard.domain.model.SensorHistory;
import cloud.chlora.pipeline.dashboard.domain.port.SensorHistoryQueryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SensorHistoryQueryAdapter implements SensorHistoryQueryRepository {

    private final EntityManager entityManager;

    private static final ZoneId JAKARTA = ZoneId.of("Asia/Jakarta");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

    @Override
    public SensorHistory getSensorHistory(String potId, String range) {
        int rangeMinutes = RANGE_MINUTES_MAP.get(range);

        Instant to = Instant.now();
        Instant from = to.minus(rangeMinutes, ChronoUnit.MINUTES);

        String interval = RANGE_INTERVAL_MAP.get(range);
        int intervalMinutes = INTERVAL_MINUTES_MAP.get(interval);
        boolean isMinuteBased = intervalMinutes < 60;

        Map<String, String> graphMetadata = fetchGraphMetadata(potId);
        SensorAggregatePointParams params = new SensorAggregatePointParams(
                potId, from, to,
                isMinuteBased ? intervalMinutes : intervalMinutes / 60,
                range
        );

        List<SensorHistory.SensorAggregatePoint> aggregateData = isMinuteBased
                ? fetchAggregateByMinute(params)
                : fetchAggregateByHour(params);

        return SensorHistory.builder()
                .potId(potId)
                .potName(graphMetadata.get("potName"))
                .deviceName(graphMetadata.get("deviceName"))
                .range(range)
                .interval(interval)
                .data(aggregateData)
                .build();
    }

    private Map<String, String> fetchGraphMetadata(String potId) {
        String sql = """
                SELECT p.pot_name, d.device_name
                FROM devices d
                JOIN pots p ON p.pot_id = d.pot_id
                WHERE d.pot_id = :potId AND d.deleted_at IS NULL
                LIMIT 1
                """;

        Object[] row = (Object[]) entityManager.createNativeQuery(sql)
                .setParameter("potId", potId)
                .getSingleResult();

        return Map.of(
                "potName",    (String) row[0],
                "deviceName", (String) row[1]
        );
    }

    @SuppressWarnings("unchecked")
    private List<SensorHistory.SensorAggregatePoint> fetchAggregateByMinute(SensorAggregatePointParams params) {
        String sql = """
                SELECT
                    time_bucket,
                    ROUND(AVG(temperature)::numeric, 1)   AS temperature,
                    ROUND(AVG(humidity)::numeric, 1)      AS humidity,
                    ROUND(AVG(soil_moisture)::numeric, 1) AS soil_moisture
                FROM (
                    SELECT
                        DATE_TRUNC('minute', device_timestamp AT TIME ZONE 'Asia/Jakarta') -
                            (EXTRACT(MINUTE FROM device_timestamp AT TIME ZONE 'Asia/Jakarta')::int % :interval)
                            * INTERVAL '1 minute' AS time_bucket,
                        temperature,
                        humidity,
                        soil_moisture
                    FROM telemetry t
                    INNER JOIN devices d ON d.device_id = t.device_id AND d.deleted_at IS NULL
                    WHERE d.pot_id = :potId
                      AND t.device_timestamp BETWEEN CAST(:from AS timestamptz) AND CAST(:to AS timestamptz)
                      AND t.is_valid = true
                ) sub
                GROUP BY time_bucket
                ORDER BY time_bucket DESC
                """;

        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("potId", params.potId())
                .setParameter("from",     params.from())
                .setParameter("to",       params.to())
                .setParameter("interval", params.interval())
                .getResultList();

        return mapToAggregatePoints(rows, params.range());
    }

    @SuppressWarnings("unchecked")
    private List<SensorHistory.SensorAggregatePoint> fetchAggregateByHour(SensorAggregatePointParams params) {
        String sql = """
                SELECT
                    time_bucket,
                    ROUND(AVG(temperature)::numeric, 1)   AS temperature,
                    ROUND(AVG(humidity)::numeric, 1)      AS humidity,
                    ROUND(AVG(soil_moisture)::numeric, 1) AS soil_moisture
                FROM (
                    SELECT
                        DATE_TRUNC('hour', device_timestamp AT TIME ZONE 'Asia/Jakarta') -
                            (EXTRACT(MINUTE FROM device_timestamp AT TIME ZONE 'Asia/Jakarta')::int % :interval)
                            * INTERVAL '1 hour' AS time_bucket,
                        temperature,
                        humidity,
                        soil_moisture
                    FROM telemetry t
                    INNER JOIN devices d ON d.device_id = t.device_id AND d.deleted_at IS NULL
                    WHERE d.pot_id = :potId
                        AND t.device_timestamp BETWEEN CAST(:from AS timestamptz) AND CAST(:to AS timestamptz)
                        AND t.is_valid = true
                ) sub
                GROUP BY time_bucket
                ORDER BY time_bucket DESC
                """;

        List<Object[]> rows = entityManager.createNativeQuery(sql)
                .setParameter("potId", params.potId())
                .setParameter("from",     params.from())
                .setParameter("to",       params.to())
                .setParameter("interval", params.interval())
                .getResultList();

        return mapToAggregatePoints(rows, params.range());
    }

    private List<SensorHistory.SensorAggregatePoint> mapToAggregatePoints(List<Object[]> rows, String range) {
        DateTimeFormatter formatter = "7d".equals(range) ? DATETIME_FORMATTER : TIME_FORMATTER;
        return rows.stream()
                .map(row -> {
                    LocalDateTime time = (LocalDateTime) row[0];
                    return SensorHistory.SensorAggregatePoint.builder()
                            .time(time.format(formatter))
                            .temperature(((BigDecimal) row[1]).floatValue())
                            .humidity(((BigDecimal) row[2]).floatValue())
                            .soilMoisture(((BigDecimal) row[3]).floatValue())
                            .build();
                })
                .toList();
    }

    private record SensorAggregatePointParams(String potId, Instant from, Instant to, int interval, String range) {}

    private static final Map<String, String> RANGE_INTERVAL_MAP = Map.of(
            "5m",  "1m",
            "1h",  "5m",
            "6h",  "15m",
            "24h", "1h",
            "7d",  "6h"
    );

    private static final Map<String, Integer> RANGE_MINUTES_MAP = Map.of(
            "5m",  5,
            "1h",  60,
            "6h",  360,
            "24h", 1440,
            "7d",  10080
    );

    private static final Map<String, Integer> INTERVAL_MINUTES_MAP = Map.of(
            "1m",  1,
            "5m",  5,
            "15m", 15,
            "1h",  60,
            "6h",  360
    );
}