package cloud.chlora.report.telemetry.application.service;

import cloud.chlora.report.shared.BaseReportResponse;
import cloud.chlora.report.telemetry.domain.model.TelemetryReport;
import cloud.chlora.report.telemetry.domain.model.TelemetryReportQuery;
import cloud.chlora.report.telemetry.domain.port.TelemetryReportRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// FIXME: fix the error
@ExtendWith(MockitoExtension.class)
class TelemetryReportServiceTest {

    @Mock
    private TelemetryReportRepository reportRepository;

    @InjectMocks
    private TelemetryReportService service;

    // Default test data constants
    private static final Instant DATE_FROM = Instant.parse("2026-05-06T17:00:00Z");
    private static final Instant DATE_TO = Instant.parse("2026-05-07T17:00:00Z");

    @Test
    @DisplayName("getReport should return paged response with correct total elements and pages")
    void getReport_shouldReturnPagedResponse_whenValidQuery() {
        // Arrange
        TelemetryReportQuery query = TelemetryReportQuery.builder()
                .page(1).size(5)
                .dateFrom(DATE_FROM).dateTo(DATE_TO)
                .potId(null)
                .build();

        List<TelemetryReport> mockReports = createMockReports(5);
        when(reportRepository.findAll(any())).thenReturn(mockReports);
        when(reportRepository.countAll(any())).thenReturn(25L);

        // Act
        BaseReportResponse<TelemetryReport> result = service.getReport(query);

        // Assert
        assertThat(result.contents()).hasSize(5);
        assertThat(result.totalElements()).isEqualTo(25);
        assertThat(result.totalPages()).isEqualTo(5);
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("getReport should return empty contents when no data matches")
    void getReport_shouldReturnEmptyContents_whenNoDataMatches() {
        // Arrange
        TelemetryReportQuery query = TelemetryReportQuery.builder()
                .page(1).size(10)
                .dateFrom(DATE_FROM).dateTo(DATE_TO)
                .potId(null)
                .build();

        when(reportRepository.findAll(any())).thenReturn(new ArrayList<>());
        when(reportRepository.countAll(any())).thenReturn(0L);

        // Act
        BaseReportResponse<TelemetryReport> result = service.getReport(query);

        // Assert
        assertThat(result.contents()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(0);
    }

    @Test
    @DisplayName("getReport should return partial results when on last page")
    void getReport_shouldReturnPartialResults_whenOnLastPage() {
        // Arrange
        TelemetryReportQuery query = TelemetryReportQuery.builder()
                .page(2).size(5)
                .dateFrom(DATE_FROM).dateTo(DATE_TO)
                .potId(null)
                .build();

        List<TelemetryReport> partialReports = createMockReports(3);
        when(reportRepository.findAll(any())).thenReturn(partialReports);
        when(reportRepository.countAll(any())).thenReturn(13L);

        // Act
        BaseReportResponse<TelemetryReport> result = service.getReport(query);

        // Assert
        assertThat(result.contents()).hasSize(3);
        assertThat(result.totalElements()).isEqualTo(13);
        assertThat(result.totalPages()).isEqualTo(3);
        assertThat(result.page()).isEqualTo(2);
    }

    @Test
    @DisplayName("getReport should calculate correct page count with different page sizes")
    void getReport_shouldCalculateCorrectPageCount_withDifferentPageSizes() {
        // Arrange
        when(reportRepository.findAll(any())).thenReturn(createMockReports(10));
        when(reportRepository.countAll(any())).thenReturn(100L);

        // Act & Assert - size=10
        TelemetryReportQuery query10 = TelemetryReportQuery.builder()
                .page(1).size(10)
                .dateFrom(DATE_FROM).dateTo(DATE_TO)
                .potId(null).build();
        BaseReportResponse<TelemetryReport> result10 = service.getReport(query10);
        assertThat(result10.totalPages()).isEqualTo(10); // Math.ceil(100 / 10) = 10

        // Act & Assert - size=20
        TelemetryReportQuery query20 = TelemetryReportQuery.builder()
                .page(1).size(20)
                .dateFrom(DATE_FROM).dateTo(DATE_TO)
                .potId(null).build();
        BaseReportResponse<TelemetryReport> result20 = service.getReport(query20);
        assertThat(result20.totalPages()).isEqualTo(5); // Math.ceil(100 / 20) = 5

        // Act & Assert - size=25
        TelemetryReportQuery query25 = TelemetryReportQuery.builder()
                .page(1).size(25)
                .dateFrom(DATE_FROM).dateTo(DATE_TO)
                .potId(null).build();
        BaseReportResponse<TelemetryReport> result25 = service.getReport(query25);
        assertThat(result25.totalPages()).isEqualTo(4); // Math.ceil(100 / 25) = 4
    }

    // ============ Helper Methods ============

    private List<TelemetryReport> createMockReports(int count) {
        List<TelemetryReport> reports = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            reports.add(TelemetryReport.builder()
                    .potName("Pot " + i)
                    .deviceName("Device " + i)
                    .soilMoisture(40.0f + i)
                    .temperature(25.0f + i)
                    .humidity(70.0f - i)
                    .batteryLevel(80 + i)
                    .timestamp(Instant.now().minusSeconds(i * 60))
                    .latency(100 + i * 10)
                    .build());
        }
        return reports;
    }
}