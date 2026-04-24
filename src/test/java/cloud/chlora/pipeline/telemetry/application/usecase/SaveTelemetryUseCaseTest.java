package cloud.chlora.pipeline.telemetry.application.usecase;

import cloud.chlora.pipeline.shared.ValidationResult;
import cloud.chlora.pipeline.shared.event.ProcessedTelemetryEvent;
import cloud.chlora.pipeline.telemetry.domain.model.Telemetry;
import cloud.chlora.pipeline.telemetry.domain.port.TelemetryWriteRepository;
import cloud.chlora.shared.port.DeviceRegistrationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveTelemetryUseCaseTest {

    @Mock
    private DeviceRegistrationPort deviceRegistrationPort;

    @Mock
    private TelemetryWriteRepository telemetryWriteRepository;

    @InjectMocks
    private SaveTelemetryUseCase useCase;

    private ProcessedTelemetryEvent event;
    private ValidationResult validationResult;

    @BeforeEach
    void setUp() {
        event = ProcessedTelemetryEvent.builder()
                .deviceId("dev-123")
                .deviceTimestamp(Instant.now())
                .soilMoisture(40.0f)
                .temperature(25.0f)
                .humidity(60.0f)
                .batteryLevel(90.0f)
                .receivedAt(Instant.now())
                .build();
        validationResult = ValidationResult.ok();
    }

    @Test
    @DisplayName("Should save telemetry and update device status when device is already registered")
    void should_saveTelemetryAndSetOnline_when_deviceIsRegistered() {
        // Arrange
        when(deviceRegistrationPort.isDeviceRegistered("dev-123")).thenReturn(true);

        // Act
        useCase.execute(event, validationResult);

        // Assert
        verify(deviceRegistrationPort, never()).registerDevice(anyString());
        
        ArgumentCaptor<Telemetry> telemetryCaptor = ArgumentCaptor.forClass(Telemetry.class);
        verify(telemetryWriteRepository).save(telemetryCaptor.capture());
        
        Telemetry saved = telemetryCaptor.getValue();
        assertThat(saved.deviceId()).isEqualTo("dev-123");
        assertThat(saved.isValid()).isTrue();

        verify(deviceRegistrationPort).setDeviceOnline("dev-123");
    }

    @Test
    @DisplayName("Should register device if not already registered before saving")
    void should_registerDevice_when_notRegistered() {
        // Arrange
        when(deviceRegistrationPort.isDeviceRegistered("dev-123")).thenReturn(false);

        // Act
        useCase.execute(event, validationResult);

        // Assert
        verify(deviceRegistrationPort).registerDevice("dev-123");
        verify(telemetryWriteRepository).save(any(Telemetry.class));
        verify(deviceRegistrationPort).setDeviceOnline("dev-123");
    }

    @Test
    @DisplayName("Should save telemetry with isValid=false when validation failed")
    void should_saveWithInvalidFlag_when_validationFailed() {
        // Arrange
        when(deviceRegistrationPort.isDeviceRegistered("dev-123")).thenReturn(true);
        ValidationResult failedResult = ValidationResult.rejected("Bad data");

        // Act
        useCase.execute(event, failedResult);

        // Assert
        ArgumentCaptor<Telemetry> telemetryCaptor = ArgumentCaptor.forClass(Telemetry.class);
        verify(telemetryWriteRepository).save(telemetryCaptor.capture());
        
        Telemetry saved = telemetryCaptor.getValue();
        assertThat(saved.isValid()).isFalse();
    }

    @Test
    @DisplayName("Should not fail if setting device online throws exception")
    void should_notFail_when_setOnlineThrowsException() {
        // Arrange
        when(deviceRegistrationPort.isDeviceRegistered("dev-123")).thenReturn(true);
        doThrow(new RuntimeException("API Error")).when(deviceRegistrationPort).setDeviceOnline(anyString());

        // Act & Assert
        // Should not throw exception
        useCase.execute(event, validationResult);
        
        verify(telemetryWriteRepository).save(any(Telemetry.class));
    }
}
