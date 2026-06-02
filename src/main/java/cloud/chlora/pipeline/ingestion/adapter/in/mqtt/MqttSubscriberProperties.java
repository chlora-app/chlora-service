package cloud.chlora.pipeline.ingestion.adapter.in.mqtt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mqtt.subscriber")
public class MqttSubscriberProperties {

    private String topic;
    private int qos;
}
