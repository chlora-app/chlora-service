package cloud.chlora.pipeline.telemetry.application.usecase;

import cloud.chlora.pipeline.shared.ValidationResult;
import cloud.chlora.pipeline.shared.event.ProcessedTelemetryEvent;
import cloud.chlora.pipeline.shared.event.TelemetrySavedEvent;
import cloud.chlora.pipeline.telemetry.application.service.TelemetryService;
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
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveTelemetryUseCaseTest {

    @Mock
    private DeviceRegistrationPort deviceRegistrationPort;

    @Mock
    private TelemetryWriteRepository telemetryWriteRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TelemetryService useCase;

    private static final String DEVICE_ID = "dev-001";

    private ProcessedTelemetryEvent event;
    private Telemetry savedTelemetry;

    @BeforeEach
    void setUp() {
        event = new ProcessedTelemetryEvent(
                DEVICE_ID,
                Instant.parse("2025-01-01T00:00:00Z"),
                55.0f,
                27.5f,
                68.0f,
                85.4f,
                Instant.parse("2025-01-01T00:00:01Z")
        );

        savedTelemetry = new Telemetry(
                1L,
                DEVICE_ID,
                Instant.parse("2025-01-01T00:00:00Z"),
                55.0f,
                27.5f,
                68.0f,
                Math.round(85.4f),
                Instant.parse("2025-01-01T00:00:01Z"),
                true
        );

        when(telemetryWriteRepository.save(any(Telemetry.class))).thenReturn(savedTelemetry);
    }

    @Test
    @DisplayName("execute should save telemetry and publish TelemetrySavedEvent when device is registered")
    void should_saveTelemetryAndPublishEvent_when_deviceIsRegistered() {
        when(deviceRegistrationPort.isDeviceRegistered(DEVICE_ID)).thenReturn(true);
        ValidationResult validationResult = ValidationResult.ok();

        useCase.execute(event, validationResult);

        ArgumentCaptor<Telemetry> telemetryCaptor = ArgumentCaptor.forClass(Telemetry.class);
        verify(telemetryWriteRepository).save(telemetryCaptor.capture());
        Telemetry persisted = telemetryCaptor.getValue();
        assertThat(persisted.deviceId()).isEqualTo(DEVICE_ID);
        assertThat(persisted.isValid()).isTrue();
        assertThat(persisted.batteryLevel()).isEqualTo(Math.round(event.batteryLevel()));


        ArgumentCaptor<TelemetrySavedEvent> eventCaptor = ArgumentCaptor.forClass(TelemetrySavedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        TelemetrySavedEvent published = eventCaptor.getValue();
        assertThat(published.telemetryId()).isEqualTo(savedTelemetry.id());
        assertThat(published.deviceId()).isEqualTo(DEVICE_ID);
        assertThat(published.soilMoisture()).isEqualTo(savedTelemetry.soilMoisture());
        assertThat(published.temperature()).isEqualTo(savedTelemetry.temperature());
        assertThat(published.humidity()).isEqualTo(savedTelemetry.humidity());
    }

    @Test
    @DisplayName("execute should call setDeviceOnline after saving telemetry")
    void should_setDeviceOnline_when_saveSucceeds() {
        when(deviceRegistrationPort.isDeviceRegistered(DEVICE_ID)).thenReturn(true);

        useCase.execute(event, ValidationResult.ok());

        verify(deviceRegistrationPort).setDeviceOnline(DEVICE_ID);
    }

    @Test
    @DisplayName("execute should register device when it is not yet registered")
    void should_registerDevice_when_notRegistered() {
        when(deviceRegistrationPort.isDeviceRegistered(DEVICE_ID)).thenReturn(false);

        useCase.execute(event, ValidationResult.ok());

        verify(deviceRegistrationPort).registerDevice(DEVICE_ID);
        verify(telemetryWriteRepository).save(any(Telemetry.class));
    }

    @Test
    @DisplayName("execute should not register device when it is already registered")
    void should_notRegisterDevice_when_alreadyRegistered() {
        when(deviceRegistrationPort.isDeviceRegistered(DEVICE_ID)).thenReturn(true);

        useCase.execute(event, ValidationResult.ok());

        verify(deviceRegistrationPort, never()).registerDevice(any());
    }

    @Test
    @DisplayName("execute should save telemetry with isValid=false when validation failed")
    void should_saveWithInvalidFlag_when_validationFailed() {
        when(deviceRegistrationPort.isDeviceRegistered(DEVICE_ID)).thenReturn(true);
        ValidationResult failedValidation = ValidationResult.rejected("validation failed");

        useCase.execute(event, failedValidation);

        ArgumentCaptor<Telemetry> captor = ArgumentCaptor.forClass(Telemetry.class);
        verify(telemetryWriteRepository).save(captor.capture());
        assertThat(captor.getValue().isValid()).isFalse();
    }

    @Test
    @DisplayName("execute should not propagate exception when setDeviceOnline fails")
    void should_notFail_when_setDeviceOnlineThrowsException() {
        when(deviceRegistrationPort.isDeviceRegistered(DEVICE_ID)).thenReturn(true);
        doThrow(new RuntimeException("upstream timeout"))
                .when(deviceRegistrationPort).setDeviceOnline(DEVICE_ID);

        org.assertj.core.api.Assertions.assertThatNoException()
                .isThrownBy(() -> useCase.execute(event, ValidationResult.ok()));

        verify(telemetryWriteRepository).save(any(Telemetry.class));
        verify(eventPublisher).publishEvent(any(TelemetrySavedEvent.class));
    }
}