package cloud.chlora.pipeline.notification.application.service;

import cloud.chlora.pipeline.notification.domain.port.NotificationWriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCleanupService {

    private final NotificationWriteRepository notificationWriteRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanOldNotification() {
        Instant cutoff = Instant.now().minus(14, ChronoUnit.DAYS);
        int deleted = notificationWriteRepository.deleteOlderThan(cutoff);
        log.info("[Clean Up] Deleted {} old notifications", deleted);
    }
}
