package cloud.chlora.pipeline.notification.domain.model;

import cloud.chlora.pipeline.shared.NotificationSeverity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum BatteryNotificationThreshold {

    LOW(20f, NotificationSeverity.WARNING),
    VERY_LOW(10f, NotificationSeverity.WARNING),
    CRITICAL(5f, NotificationSeverity.CRITICAL);

    private final float percent;
    private final NotificationSeverity severity;

    /**
     * Menentukan threshold tertinggi yang cocok untuk battery level tertentu.
     * Contoh: batteryLevel 8.5 → VERY_LOW (karena < 10, tapi belum < 5).
     *         batteryLevel 3.0 → CRITICAL
     *         batteryLevel 25  → empty (tidak perlu notifikasi)
     */
    public static Optional<BatteryNotificationThreshold> resolve(float batteryLevel) {
        return Arrays.stream(values())
                .sorted(Comparator.comparing(BatteryNotificationThreshold::getPercent))
                .filter(t -> batteryLevel < t.percent)
                .findFirst();
    }

    /**
     * Cek apakah battery level sudah di atas semua threshold (sudah di-charge).
     */
    public static boolean isAboveAllThresholds(float batteryLevel) {
        return Arrays.stream(values())
                .noneMatch(t -> batteryLevel < t.percent);
    }
}