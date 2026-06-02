package cloud.chlora.pipeline.ingestion.adapter.in.mqtt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mqtt.config")
public class MqttConfigProperties {

    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;
}
