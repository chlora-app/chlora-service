-- V13__recreate_notifications_table.sql
DROP TABLE IF EXISTS notifications CASCADE;

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