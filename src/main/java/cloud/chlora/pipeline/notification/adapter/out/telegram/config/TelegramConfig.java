package cloud.chlora.pipeline.notification.adapter.out.telegram.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(TelegramConfigProperties.class)
public class TelegramConfig {

    public TelegramConfig(TelegramConfigProperties properties) {
        log.info("[Telegram] botToken={}, chatId={}",
                properties.botToken() != null ? "SET" : "NULL",
                properties.chatId());
    }
}