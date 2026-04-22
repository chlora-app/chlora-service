package cloud.chlora.pipeline.notification.adapter.out.sse;

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
public class SseEmitterRegistry {

    private final ObjectMapper objectMapper;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onError(throwable -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        log.info("[SSE] User {} registered", userId);
        return emitter;
    }

    public void broadcast(String eventName, Object data) {
        List<String> dead = new ArrayList<>();
        String json = toJson(data);

        emitters.forEach((userId, emitter) -> {
            try {
                log.info("[SSE] Sending event {} to user {}", eventName, userId);
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(json, MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                log.warn("[SSE] User {} emitter is dead, removing from registry", userId, e);
                dead.add(userId);
            }
        });

        dead.forEach(emitters::remove);
    }

    public void sendToUser(String userId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            log.debug("[SSE] No active emitter for user {}, skipping", userId);
            return;
        }
        try {
            String json = toJson(data);
            log.info("[SSE] Sending event {} to user {}", eventName, userId);
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(json, MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            log.warn("[SSE] User {} emitter is dead, removing from registry", userId, e);
            emitters.remove(userId);
        }
    }

    private String toJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("[SSE] Failed to serialize payload to JSON", e);
            throw new IllegalStateException("SSE payload serialization failed", e);
        }
    }
}