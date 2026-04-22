-- =============================================================
-- V6 : user_notification_status
-- Owned by pipeline.notification module.
-- Join table tracking per-user read status for each
-- notification. Created separately because notifications is
-- append-only and user read state is mutable.
-- =============================================================

CREATE TABLE user_notification_status (
    id              BIGSERIAL    PRIMARY KEY,
    notification_id VARCHAR(16)  NOT NULL,
    user_id         VARCHAR(16)  NOT NULL,
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE,
    read_at         TIMESTAMPTZ,

    CONSTRAINT uq_user_notification UNIQUE (notification_id, user_id)
);

CREATE INDEX idx_uns_notification_id ON user_notification_status (notification_id);
CREATE INDEX idx_uns_user_id         ON user_notification_status (user_id);
CREATE INDEX idx_uns_is_read         ON user_notification_status (user_id, is_read);

COMMENT ON TABLE  user_notification_status                  IS 'Per-user read status for each notification.';
COMMENT ON COLUMN user_notification_status.notification_id  IS 'References notifications.notification_id; no DB-level FK (cross-module).';
COMMENT ON COLUMN user_notification_status.user_id          IS 'References users.user_id; no DB-level FK (cross-module).';
COMMENT ON COLUMN user_notification_status.read_at          IS 'NULL when is_read = FALSE.';