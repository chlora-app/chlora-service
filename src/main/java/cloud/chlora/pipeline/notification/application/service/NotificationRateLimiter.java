package cloud.chlora.pipeline.notification.application.service;

import cloud.chlora.pipeline.notification.domain.model.BatteryNotificationThreshold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory rate limiter untuk battery notification.
 * Per device, track threshold mana saja yang sudah dikirim.
 * - Kalau threshold sudah pernah dikirim → suppress (return false).
 * - Kalau battery naik kembali di atas semua threshold → reset semua,
 *   artinya device sudah di-charge dan cycle notifikasi dimulai ulang.
 */
@Slf4j
@Component
public class NotificationRateLimiter {

    // deviceId → set of thresholds yang sudah di-notify
    private final ConcurrentMap<String, Set<BatteryNotificationThreshold>> notifiedThresholds =
            new ConcurrentHashMap<>();

    /**
     * Cek apakah notifikasi untuk threshold ini boleh dikirim.
     * Kalau boleh, langsung record supaya tidak dikirim ulang.
     *
     * @return true jika notifikasi boleh dikirim
     */
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

    /**
     * Dipanggil setiap kali telemetry masuk. Kalau battery sudah di atas
     * semua threshold, reset state supaya cycle notifikasi bisa dimulai ulang.
     */
    public void resetIfCharged(String deviceId, float batteryLevel) {
        if (BatteryNotificationThreshold.isAboveAllThresholds(batteryLevel)) {
            Set<BatteryNotificationThreshold> removed = notifiedThresholds.remove(deviceId);
            if (removed != null && !removed.isEmpty()) {
                log.info("[RateLimiter] Reset thresholds for device={} (battery at {}%, assumed charged)",
                        deviceId, batteryLevel);
            }
        }
    }
}