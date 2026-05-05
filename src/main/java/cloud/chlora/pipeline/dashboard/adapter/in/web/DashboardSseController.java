package cloud.chlora.pipeline.dashboard.adapter.in.web;

import cloud.chlora.pipeline.dashboard.adapter.out.sse.DashboardSseRegistry;
import cloud.chlora.pipeline.dashboard.application.service.DashboardSseService;
import cloud.chlora.pipeline.dashboard.domain.model.DashboardSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardSseController {

    private final DashboardSseRegistry registry;
    private final DashboardSseService sseService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        SseEmitter emitter = registry.register(userId);

        DashboardSnapshot snapshot = sseService.getSnapshot();
        try {
            emitter.send(SseEmitter.event()
                    .name("snapshot")
                    .data(snapshot, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            log.warn("[SSE-Dashboard] Failed to send initial snapshot to user {}", userId, e);
        }

        return emitter;
    }
}