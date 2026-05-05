package cloud.chlora.pipeline.anomaly.adapter.out.ml;

import cloud.chlora.pipeline.anomaly.domain.model.DetectionRequest;
import cloud.chlora.pipeline.anomaly.domain.model.DetectionResponse;
import cloud.chlora.pipeline.anomaly.domain.port.AnomalyDetectionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnomalyDetectionClient implements AnomalyDetectionPort {

    private final RestClient restClient;

    @Override
    public DetectionResponse detect(DetectionRequest request) {
        log.info("[ML] Sending detection request deviceId={}", request.deviceId());

        DetectionResponse response = restClient.post()
                .uri("/api/ml/detect")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(DetectionResponse.class);

        log.info(
                "[ML] Received response deviceId={} isAnomaly={} score={}",
                request.deviceId(), response.isAnomaly(), response.anomalyScore()
        );

        return response;
    }
}