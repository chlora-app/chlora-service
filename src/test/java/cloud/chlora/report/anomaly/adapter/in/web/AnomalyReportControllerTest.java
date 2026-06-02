package cloud.chlora.report.anomaly.adapter.in.web;

import cloud.chlora.report.anomaly.adapter.in.web.request.AnomalyReportFilterRequest;
import cloud.chlora.report.anomaly.adapter.in.web.response.AnomalyReportResponse;
import cloud.chlora.report.anomaly.application.usecase.GetAnomalyReportUseCase;
import cloud.chlora.report.anomaly.domain.model.AnomalyReport;
import cloud.chlora.report.anomaly.domain.model.AnomalyReportQuery;
import cloud.chlora.report.shared.BaseReportResponse;
import cloud.chlora.shared.enums.AnomalySeverity;
import cloud.chlora.shared.enums.AnomalyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnomalyReportControllerTest {

    @Mock
    private GetAnomalyReportUseCase getReportUseCase;

    @Mock
    private AnomalyReportMapper mapper;

    @InjectMocks
    private AnomalyReportController controller;

    private AnomalyReportFilterRequest filterRequest;
    private BaseReportResponse<AnomalyReport> mockDomainResponse;

    @BeforeEach
    void setUp() {
        filterRequest = new AnomalyReportFilterRequest(
                1, 10, "2026-05-07", "2026-05-07",
                "pot-1", "battery_low", "high", "desc"
        );

        List<AnomalyReport> reports = List.of(
                AnomalyReport.builder()
                        .potName("Pot A")
                        .deviceName("Device 1")
                        .soilMoisture(45.0f)
                        .temperature(28.0f)
                        .humidity(70.0f)
                        .batteryLevel(15)
                        .timestamp(Instant.now())
                        .latency(100)
                        .anomalyType(AnomalyType.BATTERY_LOW)
                        .severity(AnomalySeverity.HIGH)
                        .anomalyScore(0.87f)
                        .build()
        );

        mockDomainResponse = new BaseReportResponse<>(1, 1, 10, 1, reports);
    }

    @Test
    @DisplayName("getAnomalyReport should return OK with valid query")
    void getAnomalyReport_shouldReturnOk_withValidQuery() {
        // Arrange
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any()))
                .thenReturn(new AnomalyReportResponse(
                        "Pot A", "Device 1", 45.0f, 28.0f, 70.0f, 15,
                        Instant.now(), 100, AnomalyType.BATTERY_LOW,
                        AnomalySeverity.HIGH, 0.87f
                ));

        // Act
        ResponseEntity<BaseReportResponse<AnomalyReportResponse>> result =
                controller.getAnomalyReport(filterRequest);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().contents()).hasSize(1);
    }

    @Test
    @DisplayName("getAnomalyReport should normalize severity to uppercase")
    void getAnomalyReport_shouldNormalizeSeverityToUppercase() {
        // Arrange
        AnomalyReportFilterRequest lowercaseRequest =
                new AnomalyReportFilterRequest(1, 10, "2026-05-07", "2026-05-07",
                        "pot-1", "battery_low", "high", "desc");
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any()))
                .thenReturn(new AnomalyReportResponse(
                        "Pot A", "Device 1", 45.0f, 28.0f, 70.0f, 15,
                        Instant.now(), 100, AnomalyType.BATTERY_LOW,
                        AnomalySeverity.HIGH, 0.87f
                ));

        // Act
        controller.getAnomalyReport(lowercaseRequest);

        // Assert
        ArgumentCaptor<AnomalyReportQuery> captor = ArgumentCaptor.forClass(AnomalyReportQuery.class);
        verify(getReportUseCase).getReport(captor.capture());

        AnomalyReportQuery capturedQuery = captor.getValue();
        assertThat(capturedQuery.severity()).isEqualTo(AnomalySeverity.HIGH);
    }

    @Test
    @DisplayName("getAnomalyReport should normalize anomaly type to uppercase")
    void getAnomalyReport_shouldNormalizeAnomalyTypeToUppercase() {
        // Arrange
        AnomalyReportFilterRequest lowercaseRequest =
                new AnomalyReportFilterRequest(1, 10, "2026-05-07", "2026-05-07",
                        "pot-1", "battery_low", "high", "desc");
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any()))
                .thenReturn(new AnomalyReportResponse(
                        "Pot A", "Device 1", 45.0f, 28.0f, 70.0f, 15,
                        Instant.now(), 100, AnomalyType.BATTERY_LOW,
                        AnomalySeverity.HIGH, 0.87f
                ));

        // Act
        controller.getAnomalyReport(lowercaseRequest);

        // Assert
        ArgumentCaptor<AnomalyReportQuery> captor = ArgumentCaptor.forClass(AnomalyReportQuery.class);
        verify(getReportUseCase).getReport(captor.capture());

        AnomalyReportQuery capturedQuery = captor.getValue();
        assertThat(capturedQuery.anomalyType()).isEqualTo(AnomalyType.BATTERY_LOW);
    }

    @Test
    @DisplayName("getAnomalyReport should trim whitespace from enum parameters")
    void getAnomalyReport_shouldTrimWhitespace_fromEnumParams() {
        // Arrange
        AnomalyReportFilterRequest whitespacedRequest =
                new AnomalyReportFilterRequest(1, 10, "2026-05-07", "2026-05-07",
                        "pot-1", "  BATTERY_LOW  ", "  CRITICAL  ", "desc");
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any()))
                .thenReturn(new AnomalyReportResponse(
                        "Pot A", "Device 1", 45.0f, 28.0f, 70.0f, 15,
                        Instant.now(), 100, AnomalyType.BATTERY_LOW,
                        AnomalySeverity.CRITICAL, 0.87f
                ));

        // Act
        controller.getAnomalyReport(whitespacedRequest);

        // Assert
        ArgumentCaptor<AnomalyReportQuery> captor = ArgumentCaptor.forClass(AnomalyReportQuery.class);
        verify(getReportUseCase).getReport(captor.capture());

        AnomalyReportQuery capturedQuery = captor.getValue();
        assertThat(capturedQuery.anomalyType()).isEqualTo(AnomalyType.BATTERY_LOW);
        assertThat(capturedQuery.severity()).isEqualTo(AnomalySeverity.CRITICAL);
    }

    @Test
    @DisplayName("getAnomalyReport should pass null enum when parameter is blank")
    void getAnomalyReport_shouldPassNullEnum_whenParamBlank() {
        // Arrange
        AnomalyReportFilterRequest blankRequest =
                new AnomalyReportFilterRequest(1, 10, "2026-05-07", "2026-05-07",
                        "pot-1", "", "", "desc");
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any()))
                .thenReturn(new AnomalyReportResponse(
                        "Pot A", "Device 1", 45.0f, 28.0f, 70.0f, 15,
                        Instant.now(), 100, AnomalyType.BATTERY_LOW,
                        AnomalySeverity.HIGH, 0.87f
                ));

        // Act
        controller.getAnomalyReport(blankRequest);

        // Assert
        ArgumentCaptor<AnomalyReportQuery> captor = ArgumentCaptor.forClass(AnomalyReportQuery.class);
        verify(getReportUseCase).getReport(captor.capture());

        AnomalyReportQuery capturedQuery = captor.getValue();
        assertThat(capturedQuery.anomalyType()).isNull();
        assertThat(capturedQuery.severity()).isNull();
    }

    @Test
    @DisplayName("getAnomalyReport should convert blank potId to null")
    void getAnomalyReport_shouldConvertBlankPotIdToNull() {
        // Arrange
        AnomalyReportFilterRequest blankPotRequest =
                new AnomalyReportFilterRequest(1, 10, "2026-05-07", "2026-05-07",
                        "   ", "battery_low", "high", "desc");
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any()))
                .thenReturn(new AnomalyReportResponse(
                        "Pot A", "Device 1", 45.0f, 28.0f, 70.0f, 15,
                        Instant.now(), 100, AnomalyType.BATTERY_LOW,
                        AnomalySeverity.HIGH, 0.87f
                ));

        // Act
        controller.getAnomalyReport(blankPotRequest);

        // Assert
        ArgumentCaptor<AnomalyReportQuery> captor = ArgumentCaptor.forClass(AnomalyReportQuery.class);
        verify(getReportUseCase).getReport(captor.capture());

        AnomalyReportQuery capturedQuery = captor.getValue();
        assertThat(capturedQuery.potId()).isNull();
    }

    @Test
    @DisplayName("getAnomalyReport should convert date range to UTC")
    void getAnomalyReport_shouldConvertDateRange_toUTC() {
        // Arrange
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any()))
                .thenReturn(new AnomalyReportResponse(
                        "Pot A", "Device 1", 45.0f, 28.0f, 70.0f, 15,
                        Instant.now(), 100, AnomalyType.BATTERY_LOW,
                        AnomalySeverity.HIGH, 0.87f
                ));

        // Act
        controller.getAnomalyReport(filterRequest);

        // Assert
        ArgumentCaptor<AnomalyReportQuery> captor = ArgumentCaptor.forClass(AnomalyReportQuery.class);
        verify(getReportUseCase).getReport(captor.capture());

        AnomalyReportQuery capturedQuery = captor.getValue();
        assertThat(capturedQuery.dateFrom()).isEqualTo(Instant.parse("2026-05-06T17:00:00Z"));
        assertThat(capturedQuery.dateTo()).isEqualTo(Instant.parse("2026-05-07T17:00:00Z"));
    }

    @Test
    @DisplayName("getAnomalyReport should throw IllegalArgumentException when enum value is invalid")
    void getAnomalyReport_shouldThrowIllegalArgumentException_whenEnumInvalid() {
        // Arrange
        AnomalyReportFilterRequest invalidRequest =
                new AnomalyReportFilterRequest(1, 10, "2026-05-07", "2026-05-07",
                        "pot-1", "INVALID_TYPE", "INVALID_SEVERITY", "desc");

        // Act & Assert
        assertThatThrownBy(() -> controller.getAnomalyReport(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}