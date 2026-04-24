package cloud.chlora.pipeline.validation.adapter.in;

import cloud.chlora.pipeline.ingestion.internal.MqttPayload;
import cloud.chlora.pipeline.shared.ValidationResult;
import cloud.chlora.pipeline.shared.event.SensorAnomalyDetectedEvent;
import cloud.chlora.pipeline.shared.event.TelemetryProcessedEvent;
import cloud.chlora.pipeline.validation.application.validator.TelemetryValidator;
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
class ValidationPortAdapterTest {

    @Mock
    private TelemetryValidator validator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ValidationPortAdapter adapter;

    private MqttPayload payload;

    @BeforeEach
    void setUp() {
        MqttPayload.MqttMetadata metadata = new MqttPayload.MqttMetadata("msg-1", "topic", Instant.now());
        MqttPayload.MqttData data = new MqttPayload.MqttData("dev-1", System.currentTimeMillis(), 50.0f, 25.0f, 60.0f, 95.0f);
        payload = new MqttPayload(metadata, data);
    }

    @Test
    @DisplayName("Should publish TelemetryProcessedEvent when data is valid")
    void should_publishTelemetryProcessedEvent_when_valid() {
        // Arrange
        when(validator.validate(payload)).thenReturn(ValidationResult.ok());

        // Act
        ValidationResult result = adapter.validate(payload);

        // Assert
        assertThat(result.valid()).isTrue();
        
        ArgumentCaptor<TelemetryProcessedEvent> captor = ArgumentCaptor.forClass(TelemetryProcessedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        
        TelemetryProcessedEvent event = captor.getValue();
        assertThat(event.telemetry().deviceId()).isEqualTo("dev-1");
        assertThat(event.validationResult().valid()).isTrue();
        
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }

    @Test
    @DisplayName("Should publish Anomaly events when data is out of range")
    void should_publishAnomalyEvents_when_invalid() {
        // Arrange
        // Mocking invalid data: Temperature 100 is out of range [-40, 85]
        MqttPayload.MqttData invalidData = new MqttPayload.MqttData("dev-1", System.currentTimeMillis(), 50.0f, 100.0f, 60.0f, 95.0f);
        MqttPayload invalidPayload = new MqttPayload(payload.metadata(), invalidData);
        
        when(validator.validate(invalidPayload)).thenReturn(ValidationResult.rejected("Temperature out of range"));

        // Act
        adapter.validate(invalidPayload);

        // Assert
        // Should publish 2 events: TelemetryProcessedEvent AND SensorAnomalyDetectedEvent
        verify(eventPublisher, times(2)).publishEvent(any(Object.class));
        
        verify(eventPublisher).publishEvent(any(TelemetryProcessedEvent.class));
        verify(eventPublisher).publishEvent(any(SensorAnomalyDetectedEvent.class));
    }

    @Test
    @DisplayName("Should publish multiple anomalies if multiple values are out of range")
    void should_publishMultipleAnomalies_when_multipleInvalid() {
        // Arrange
        // Temperature 100 (>85), Humidity 110 (>100)
        MqttPayload.MqttData multiInvalidData = new MqttPayload.MqttData("dev-1", System.currentTimeMillis(), 50.0f, 100.0f, 110.0f, 95.0f);
        MqttPayload multiInvalidPayload = new MqttPayload(payload.metadata(), multiInvalidData);
        
        when(validator.validate(multiInvalidPayload)).thenReturn(ValidationResult.rejected("Multiple errors"));

        // Act
        adapter.validate(multiInvalidPayload);

        // Assert
        // 1 TelemetryProcessedEvent + 1 Temperature Anomaly + 1 Humidity Anomaly = 3 events
        verify(eventPublisher, times(3)).publishEvent(any(Object.class));
        verify(eventPublisher, times(2)).publishEvent(any(SensorAnomalyDetectedEvent.class));
    }
}
