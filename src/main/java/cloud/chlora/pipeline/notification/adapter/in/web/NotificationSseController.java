package cloud.chlora.pipeline.notification.adapter.in.web;

import cloud.chlora.pipeline.notification.adapter.in.web.response.NotificationListResponse;
import cloud.chlora.pipeline.notification.adapter.out.sse.SseEmitterRegistry;
import cloud.chlora.pipeline.notification.application.usecase.GetNotificationListUseCase;
import cloud.chlora.pipeline.notification.application.usecase.MarkAllNotificationsReadUseCase;
import cloud.chlora.pipeline.notification.application.usecase.MarkNotificationReadUseCase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationSseController {

    private final SseEmitterRegistry registry;
    private final GetNotificationListUseCase getNotificationListUseCase;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;
    private final MarkAllNotificationsReadUseCase markAllNotificationsReadUseCase;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return registry.register(userId);
    }

    @GetMapping
    public ResponseEntity<@NonNull NotificationListResponse> getAll(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(getNotificationListUseCase.execute(userId));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<@NonNull Void> markAsRead(
            @PathVariable String notificationId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        markNotificationReadUseCase.execute(notificationId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<@NonNull Void> markAllAsRead(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        markAllNotificationsReadUseCase.execute(userId);
        return ResponseEntity.noContent().build();
    }
}