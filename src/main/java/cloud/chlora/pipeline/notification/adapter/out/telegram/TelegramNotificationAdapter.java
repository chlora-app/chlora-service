package cloud.chlora.pipeline.notification.adapter.out.telegram;

import cloud.chlora.pipeline.notification.adapter.out.telegram.config.TelegramConfigProperties;
import cloud.chlora.pipeline.notification.application.port.out.NotificationSenderPort;
import cloud.chlora.pipeline.shared.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationAdapter implements NotificationSenderPort {

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot{token}/sendMessage";

    private final RestTemplate restTemplate;
    private final TelegramConfigProperties properties;

    @Override
    public void send(NotificationMessage message) {
        String text = formatMessage(message);

        try {
            Map<String, Object> body = Map.of(
                    "chat_id", properties.chatId(),
                    "text", text,
                    "parse_mode", "HTML"
            );

            restTemplate.postForObject(
                    TELEGRAM_API_URL,
                    body,
                    String.class,
                    Map.of("token", properties.botToken())
            );
        } catch (Exception e) {
            log.error("[Telegram] Failed to send notification: {}", e.getMessage(), e);
        }
    }

    private String formatMessage(NotificationMessage message) {
        String icon = switch (message.severity()) {
            case CRITICAL -> "🔴";
            case WARNING  -> "🟡";
            case INFO     -> "🔵";
        };

        String typeLabel = switch (message.notificationType()) {
            case BATTERY ->  "Battery Alert";
            case ANOMALY ->  "Anomaly Detected";
        };

        return """
                %s <b>[%s] %s</b>
                
                %s
                
                <i>Severity: %s</i>
                """.formatted(icon, typeLabel, message.title(), message.body(), message.severity());
    }
}