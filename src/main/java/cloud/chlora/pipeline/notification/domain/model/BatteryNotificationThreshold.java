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

    LOW(20, NotificationSeverity.WARNING),
    VERY_LOW(10, NotificationSeverity.WARNING),
    CRITICAL(5, NotificationSeverity.CRITICAL);

    private final int percent;
    private final NotificationSeverity severity;

    /**
     * Menentukan threshold tertinggi yang cocok untuk battery level tertentu.
     * Contoh: batteryLevel 8 → VERY_LOW (karena < 10, tapi belum < 5).
     *         batteryLevel 3 → CRITICAL
     *         batteryLevel 25 → empty (tidak perlu notifikasi)
     */
    public static Optional<BatteryNotificationThreshold> resolve(int batteryLevel) {
        return Arrays.stream(values())
                .sorted(Comparator.comparing(BatteryNotificationThreshold::getPercent))
                .filter(t -> batteryLevel < t.percent)
                .findFirst();
    }

    /**
     * Cek apakah battery level sudah di atas semua threshold (sudah di-charge).
     */
    public static boolean isAboveAllThresholds(int batteryLevel) {
        return Arrays.stream(values())
                .noneMatch(t -> batteryLevel < t.percent);
    }
}