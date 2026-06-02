package cloud.chlora.pipeline.notification.application.service;

import cloud.chlora.pipeline.notification.domain.model.BatteryNotificationThreshold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class NotificationRateLimiter {

    private final ConcurrentMap<String, Set<BatteryNotificationThreshold>> notifiedThresholds =
            new ConcurrentHashMap<>();

    public boolean tryAcquire(String deviceId, BatteryNotificationThreshold threshold) {
        Set<BatteryNotificationThreshold> sent = notifiedThresholds
                .computeIfAbsent(deviceId, k -> ConcurrentHashMap.newKeySet());

        boolean added = sent.add(threshold);
        if (!added) {
            log.debug("[RateLimiter] Suppressed {} notification for device={} (already sent)",
                    threshold, deviceId);
        }
        return added;
    }

    public void resetIfCharged(String deviceId, int batteryLevel) {
        if (BatteryNotificationThreshold.isAboveAllThresholds(batteryLevel)) {
            Set<BatteryNotificationThreshold> removed = notifiedThresholds.remove(deviceId);
            if (removed != null && !removed.isEmpty()) {
                log.info("[RateLimiter] Reset thresholds for device={} (battery at {}%, assumed charged)",
                        deviceId, batteryLevel);
            }
        }
    }
}