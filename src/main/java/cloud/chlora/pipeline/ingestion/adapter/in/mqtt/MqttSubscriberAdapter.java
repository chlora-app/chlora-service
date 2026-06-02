package cloud.chlora.pipeline.ingestion.adapter.in.mqtt;

import cloud.chlora.pipeline.ingestion.application.usecase.TelemetryIngestUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttSubscriberAdapter implements MqttCallback, SmartLifecycle {

    private final MqttClient client;
    private final TelemetryIngestUseCase useCase;
    private final MqttSubscriberProperties properties;
    private final MqttConnectOptions connectOptions;

    private volatile boolean running = false;

    @Override
    public void start() {
        try {
            client.connect(connectOptions);
            client.setCallback(this);
            client.subscribe(properties.getTopic(), properties.getQos());

            running = true;
        } catch (MqttException e) {
            throw new RuntimeException("Failed to start MQTT subscriber", e);
        }
    }

    @Override
    public void stop() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }

            running = false;
            log.info("MQTT subscriber stopped");
        } catch (MqttException e) {
            log.error("Error during MQTT disconnect", e);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());

        try {
            useCase.handle(topic, payload, Instant.now());
        } catch (Exception e) {
            log.error("Failed to process message. topic={}, payload={}", topic, payload, e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.error("MQTT connection lost", cause);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
}
