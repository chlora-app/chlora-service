package cloud.chlora.pipeline.dashboard.adapter.out.sse;

import cloud.chlora.pipeline.dashboard.domain.port.DashboardSsePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardSseRegistry implements DashboardSsePort {

    private final ObjectMapper objectMapper;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(String clientId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.put(clientId, emitter);
        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onError(throwable -> emitters.remove(clientId));
        emitter.onTimeout(() -> emitters.remove(clientId));

        log.info("[SSE-Dashboard] Client {} registered", clientId);
        return emitter;
    }

    public void broadcast(String eventName, Object data) {
        List<String> dead = new ArrayList<>();
        String json = toJson(data);

        emitters.forEach((clientId, emitter) -> {
            try {
                log.info("[SSE-Dashboard] Sending event {} to client {}", eventName, clientId);
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(json, MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                log.warn("[SSE-Dashboard] Client {} emitter is dead, removing from registry", clientId, e);
                dead.add(clientId);
            }
        });

        dead.forEach(emitters::remove);
    }

    private String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("[SSE-Dashboard] Failed to serialize payload to JSON", e);
            throw new IllegalStateException("SSE payload serialization failed", e);
        }
    }
}
