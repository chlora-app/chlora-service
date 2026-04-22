-- =============================================================
-- V5 : notifications
-- Owned by pipeline.notification module.
-- Append-only; records are never updated or deleted.
-- =============================================================

CREATE TABLE notifications (
    id                  BIGSERIAL       PRIMARY KEY,
    notification_id     VARCHAR(16)     NOT NULL UNIQUE,
    device_id           VARCHAR(24)     NOT NULL,
    message             TEXT            NOT NULL,
    severity            VARCHAR(16)     NOT NULL CHECK (severity IN ('INFO', 'WARNING', 'CRITICAL')),
    notification_type   VARCHAR(50)     NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_notification_id ON notifications (notification_id);
CREATE INDEX idx_notifications_device_id       ON notifications (device_id);
CREATE INDEX idx_notifications_severity        ON notifications (severity);
CREATE INDEX idx_notifications_created_at      ON notifications (created_at DESC);

COMMENT ON TABLE  notifications                    IS 'Anomaly / alert notifications emitted by the detection pipeline.';
COMMENT ON COLUMN notifications.notification_id   IS 'Business-facing ID, format: N-<8 alphanum>.';
COMMENT ON COLUMN notifications.device_id         IS 'References devices.device_id; no DB-level FK (cross-module).';
COMMENT ON COLUMN notifications.severity          IS 'INFO | WARNING | CRITICAL';