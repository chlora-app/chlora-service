package cloud.chlora.pipeline.ingestion.adapter.in.mqtt.config;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MqttConfig {

    private final MqttConfigProperties properties;

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(properties.getUsername());
        options.setPassword(properties.getPassword().toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        return options;
    }

    @Bean
    public MqttClient mqttClient() throws MqttException {
        return new MqttClient(
                properties.getBrokerUrl(),
                properties.getClientId(),
                new MemoryPersistence()
        );
    }
}