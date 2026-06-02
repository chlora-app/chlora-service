package cloud.chlora.report.anomaly.application.service;

import cloud.chlora.report.anomaly.domain.model.AnomalyReport;
import cloud.chlora.report.anomaly.domain.model.AnomalyReportQuery;
import cloud.chlora.report.anomaly.domain.port.AnomalyReportRepository;
import cloud.chlora.report.shared.BaseReportResponse;
import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.enums.AnomalyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnomalyReportServiceTest {

    @Mock
    private AnomalyReportRepository reportRepository;

    @InjectMocks
    private AnomalyReportService service;

    private AnomalyReportQuery query;
    private List<AnomalyReport> mockAnomalies;

    @BeforeEach
    void setUp() {
        query = AnomalyReportQuery.builder()
                .page(1)
                .size(10)
                .dateFrom(Instant.parse("2026-05-06T17:00:00Z"))
                .dateTo(Instant.parse("2026-05-07T17:00:00Z"))
                .potId(null)
                .anomalyType(null)
                .severity(null)
                .build();

        mockAnomalies = createMockAnomalies();
    }

    @Test
    @DisplayName("getReport should return anomalies with sensor data and correct severity")
    void getReport_shouldReturnAnomaliesWithSensorDataAndSeverity() {
        // Arrange
        when(reportRepository.findAll(query)).thenReturn(mockAnomalies);
        when(reportRepository.countAll(query)).thenReturn(10L);

        // Act
        BaseReportResponse<AnomalyReport> result = service.getReport(query);

        // Assert
        assertThat(result.contents()).hasSize(5);
        assertThat(result.totalElements()).isEqualTo(10);
        assertThat(result.totalPages()).isEqualTo(1);

        // Verify first anomaly contains all fields
        AnomalyReport firstAnomaly = result.contents().get(0);
        assertThat(firstAnomaly.potName()).isNotNull();
        assertThat(firstAnomaly.deviceName()).isNotNull();
        assertThat(firstAnomaly.soilMoisture()).isGreaterThanOrEqualTo(0);
        assertThat(firstAnomaly.temperature()).isGreaterThanOrEqualTo(0);
        assertThat(firstAnomaly.anomalyType()).isNotNull();
        assertThat(firstAnomaly.severity()).isNotNull();
        assertThat(firstAnomaly.anomalyScore()).isGreaterThan(0);
    }

    @Test
    @DisplayName("getReport should return empty response when no anomalies found")
    void getReport_shouldReturnEmpty_whenNoAnomaliesFound() {
        // Arrange
        when(reportRepository.findAll(query)).thenReturn(new ArrayList<>());
        when(reportRepository.countAll(query)).thenReturn(0L);

        // Act
        BaseReportResponse<AnomalyReport> result = service.getReport(query);

        // Assert
        assertThat(result.contents()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(0);
    }

    @Test
    @DisplayName("getReport should calculate pages consistently across different filters")
    void getReport_shouldCalculatePagesConsistentlyAcrossFilters() {
        // Arrange
        List<AnomalyReport> reports = createMockAnomalies(10);
        when(reportRepository.findAll(query)).thenReturn(reports);
        when(reportRepository.countAll(query)).thenReturn(50L);

        // Act
        BaseReportResponse<AnomalyReport> result = service.getReport(query);

        // Assert
        assertThat(result.totalPages()).isEqualTo(5); // Math.ceil(50 / 10)
        assertThat(result.totalElements()).isEqualTo(50);
    }

    // ============ Helper Methods ============

    private List<AnomalyReport> createMockAnomalies() {
        return createMockAnomalies(5);
    }

    private List<AnomalyReport> createMockAnomalies(int count) {
        List<AnomalyReport> anomalies = new ArrayList<>();
        AnomalyType[] types = {
                AnomalyType.SOIL_MOISTURE_OUT_OF_RANGE,
                AnomalyType.TEMPERATURE_OUT_OF_RANGE,
                AnomalyType.BATTERY_LOW,
                AnomalyType.HUMIDITY_OUT_OF_RANGE,
                AnomalyType.TIMESTAMP_DRIFT
        };
        AnomalySeverity[] severities = {
                AnomalySeverity.HIGH,
                AnomalySeverity.CRITICAL,
                AnomalySeverity.MEDIUM,
                AnomalySeverity.LOW,
                AnomalySeverity.HIGH
        };

        for (int i = 0; i < count; i++) {
            anomalies.add(AnomalyReport.builder()
                    .potName("Pot " + (i + 1))
                    .deviceName("Device " + (i + 1))
                    .soilMoisture(40.0f + i)
                    .temperature(25.0f + i)
                    .humidity(70.0f - i)
                    .batteryLevel(80 + i)
                    .timestamp(Instant.now().minusSeconds((i + 1) * 60))
                    .latency(100 + i * 10)
                    .anomalyType(types[i % types.length])
                    .severity(severities[i % severities.length])
                    .anomalyScore(0.6f + (i * 0.05f))
                    .build());
        }
        return anomalies;
    }
}