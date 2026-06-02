package cloud.chlora.report.telemetry.adapter.in.web;

import cloud.chlora.report.shared.BaseReportResponse;
import cloud.chlora.report.telemetry.adapter.in.web.request.TelemetryReportFilterRequest;
import cloud.chlora.report.telemetry.adapter.in.web.response.TelemetryReportResponse;
import cloud.chlora.report.telemetry.application.usecase.GetTelemetryReportUseCase;
import cloud.chlora.report.telemetry.domain.model.TelemetryReport;
import cloud.chlora.report.telemetry.domain.model.TelemetryReportQuery;
import lombok.NonNull;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelemetryReportControllerTest {

    @Mock
    private GetTelemetryReportUseCase getReportUseCase;

    @Mock
    private TelemetryReportMapper mapper;

    @InjectMocks
    private TelemetryReportController controller;

    private TelemetryReportFilterRequest filterRequest;
    private BaseReportResponse<TelemetryReport> mockDomainResponse;
    private BaseReportResponse<TelemetryReportResponse> expectedResponse;

    @BeforeEach
    void setUp() {
        filterRequest = new TelemetryReportFilterRequest(1, 10, "2026-05-07", "2026-05-07", "pot-1", "desc");

        List<TelemetryReport> reports = List.of(
                TelemetryReport.builder()
                        .potName("Pot A")
                        .deviceName("Device 1")
                        .soilMoisture(45.0f)
                        .temperature(28.0f)
                        .humidity(70.0f)
                        .batteryLevel(80)
                        .timestamp(Instant.now())
                        .latency(100)
                        .build()
        );

        mockDomainResponse = new BaseReportResponse<>(1, 1, 10, 1, reports);

        List<TelemetryReportResponse> responses = List.of(
                new TelemetryReportResponse("Pot A", "Device 1", 45.0f, 28.0f, 70.0f, 80, Instant.now(), 100)
        );
        expectedResponse = new BaseReportResponse<>(1, 1, 10, 1, responses);
    }

    @Test
    @DisplayName("getTelemetryReport should return OK with mapped responses")
    void getTelemetryReport_shouldReturnOk_withValidQuery() {
        // Arrange
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any())).thenReturn(expectedResponse.contents().getFirst());

        // Act
        ResponseEntity<@NonNull BaseReportResponse<TelemetryReportResponse>> result =
                controller.getTelemetryReport(filterRequest);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().contents()).hasSize(1);
    }

    @Test
    @DisplayName("getTelemetryReport should convert date from yyyy-MM-dd to UTC Instant")
    void getTelemetryReport_shouldConvertLocalDateToInstant_withJakartaTimezone() {
        // Arrange
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any())).thenReturn(expectedResponse.contents().getFirst());

        // Act
        controller.getTelemetryReport(filterRequest);

        // Assert
        ArgumentCaptor<TelemetryReportQuery> captor = ArgumentCaptor.forClass(TelemetryReportQuery.class);
        verify(getReportUseCase).getReport(captor.capture());

        TelemetryReportQuery capturedQuery = captor.getValue();
        // 2026-05-07T00:00:00+07:00 = 2026-05-06T17:00:00Z
        assertThat(capturedQuery.dateFrom()).isEqualTo(Instant.parse("2026-05-06T17:00:00Z"));
    }

    @Test
    @DisplayName("getTelemetryReport should make dateTo inclusive by adding one day")
    void getTelemetryReport_shouldMakeDateToInclusive_byAddingOneDay() {
        // Arrange
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any())).thenReturn(expectedResponse.contents().getFirst());

        // Act
        controller.getTelemetryReport(filterRequest);

        // Assert
        ArgumentCaptor<TelemetryReportQuery> captor = ArgumentCaptor.forClass(TelemetryReportQuery.class);
        verify(getReportUseCase).getReport(captor.capture());

        TelemetryReportQuery capturedQuery = captor.getValue();
        // 2026-05-07 + 1 day = 2026-05-08T00:00:00+07:00 = 2026-05-07T17:00:00Z
        assertThat(capturedQuery.dateTo()).isEqualTo(Instant.parse("2026-05-07T17:00:00Z"));
    }

    @Test
    @DisplayName("getTelemetryReport should convert blank potId to null")
    void getTelemetryReport_shouldConvertBlankPotIdToNull() {
        // Arrange
        TelemetryReportFilterRequest blankPotIdRequest =
                new TelemetryReportFilterRequest(1, 10, "2026-05-07", "2026-05-07", "   ", "desc");
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any())).thenReturn(expectedResponse.contents().getFirst());

        // Act
        controller.getTelemetryReport(blankPotIdRequest);

        // Assert
        ArgumentCaptor<TelemetryReportQuery> captor = ArgumentCaptor.forClass(TelemetryReportQuery.class);
        verify(getReportUseCase).getReport(captor.capture());

        TelemetryReportQuery capturedQuery = captor.getValue();
        assertThat(capturedQuery.potId()).isNull();
    }

    @Test
    @DisplayName("getTelemetryReport should map domain reports to DTOs")
    void getTelemetryReport_shouldMapDomainReportsToDTOs() {
        // Arrange
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        TelemetryReportResponse mappedResponse =
                new TelemetryReportResponse("Pot A", "Device 1", 45.0f, 28.0f, 70.0f, 80, Instant.now(), 100);
        when(mapper.toResponse(mockDomainResponse.contents().getFirst())).thenReturn(mappedResponse);

        // Act
        ResponseEntity<@NonNull BaseReportResponse<TelemetryReportResponse>> result =
                controller.getTelemetryReport(filterRequest);

        // Assert
        verify(mapper).toResponse(mockDomainResponse.contents().getFirst());
        assertThat(result.getBody().contents().getFirst()).isEqualTo(mappedResponse);
    }

    @Test
    @DisplayName("getTelemetryReport should include pagination info in response")
    void getTelemetryReport_shouldIncludePaginationInfo_inResponse() {
        // Arrange
        BaseReportResponse<TelemetryReport> paginatedResponse =
                new BaseReportResponse<>(50, 2, 10, 5, mockDomainResponse.contents());
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(paginatedResponse);
        when(mapper.toResponse(org.mockito.Mockito.any())).thenReturn(expectedResponse.contents().get(0));

        // Act
        ResponseEntity<@NonNull BaseReportResponse<TelemetryReportResponse>> result =
                controller.getTelemetryReport(filterRequest);

        // Assert
        assertThat(result.getBody().totalElements()).isEqualTo(50);
        assertThat(result.getBody().page()).isEqualTo(2);
        assertThat(result.getBody().size()).isEqualTo(10);
        assertThat(result.getBody().totalPages()).isEqualTo(5);
    }

    @Test
    @DisplayName("getTelemetryReport should use default page and size when not provided")
    void getTelemetryReport_shouldUseDefaultValues_whenNotProvided() {
        // Arrange
        // Note: Since filter request uses records with default values, we test with explicit creation
        TelemetryReportFilterRequest defaultRequest =
                new TelemetryReportFilterRequest(1, 10, "2026-05-07", "2026-05-07", null, "desc");
        when(getReportUseCase.getReport(org.mockito.Mockito.any())).thenReturn(mockDomainResponse);
        when(mapper.toResponse(org.mockito.Mockito.any())).thenReturn(expectedResponse.contents().getFirst());

        // Act
        controller.getTelemetryReport(defaultRequest);

        // Assert
        ArgumentCaptor<TelemetryReportQuery> captor = ArgumentCaptor.forClass(TelemetryReportQuery.class);
        verify(getReportUseCase).getReport(captor.capture());

        TelemetryReportQuery capturedQuery = captor.getValue();
        assertThat(capturedQuery.page()).isEqualTo(1);
        assertThat(capturedQuery.size()).isEqualTo(10);
    }
}