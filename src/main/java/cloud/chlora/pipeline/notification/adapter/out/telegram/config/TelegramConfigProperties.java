package cloud.chlora.pipeline.notification.adapter.out.telegram.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.config")
public record TelegramConfigProperties(

    String botToken,
    String chatId
) {}
