package cloud.chlora.management.device.application.usecase;

import cloud.chlora.management.device.domain.port.DeviceWriteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceStatusScheduler {

    private static final int OFFLINE_THRESHOLD_MINUTES = 15;

    private final DeviceWriteRepository writeRepository;

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void markStaleDevicesOffline() {
        Instant threshold = Instant.now().minus(OFFLINE_THRESHOLD_MINUTES, ChronoUnit.MINUTES);
        int affected = writeRepository.setOfflineIfStale(threshold);
        if (affected > 0) {
            log.info("[Scheduler] Marked {} device(s) as OFFLINE (no data for {} minutes)", affected, OFFLINE_THRESHOLD_MINUTES);
        }
    }
}