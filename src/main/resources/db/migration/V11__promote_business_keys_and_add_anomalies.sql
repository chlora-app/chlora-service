-- =============================================================
-- V11 : Promote business keys to primary key
--       Drop surrogate BIGSERIAL id columns from:
--         users, pots, devices, notifications
--       Add anomalies table
-- =============================================================

-- ---------------------------------------------------------------
-- USERS
-- ---------------------------------------------------------------

-- 1. Drop old PK constraint (bound to id)
ALTER TABLE users DROP CONSTRAINT users_pkey;

-- 2. Drop surrogate column
ALTER TABLE users DROP COLUMN id;

-- 3. Promote user_id to PK
ALTER TABLE users ADD PRIMARY KEY (user_id);

-- ---------------------------------------------------------------
-- POTS
-- ---------------------------------------------------------------

ALTER TABLE pots DROP CONSTRAINT pots_pkey;
ALTER TABLE pots DROP COLUMN id;
ALTER TABLE pots ADD PRIMARY KEY (pot_id);

-- ---------------------------------------------------------------
-- DEVICES
-- ---------------------------------------------------------------

ALTER TABLE devices DROP CONSTRAINT devices_pkey;
ALTER TABLE devices DROP COLUMN id;
ALTER TABLE devices ADD PRIMARY KEY (device_id);

-- ---------------------------------------------------------------
-- NOTIFICATIONS
-- ---------------------------------------------------------------

ALTER TABLE notifications DROP CONSTRAINT notifications_pkey;
ALTER TABLE notifications DROP COLUMN id;
ALTER TABLE notifications ADD PRIMARY KEY (notification_id);

-- ---------------------------------------------------------------
-- ANOMALIES (new table)
-- Owned by pipeline.anomaly module.
-- References telemetry and notifications via business keys
-- (no DB-level FK across module boundaries).
-- ---------------------------------------------------------------

CREATE TABLE anomalies (
    id               BIGSERIAL     PRIMARY KEY,
    anomaly_type     VARCHAR(64)   NOT NULL,
    severity         VARCHAR(16)   NOT NULL CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    anomaly_score    REAL          NOT NULL,
    detected_by      VARCHAR(64)   NOT NULL,
    model_version    VARCHAR(32)   NOT NULL,
    detected_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    telemetry_id     BIGINT        NOT NULL,
    notification_id  VARCHAR(16)
);

CREATE INDEX idx_anomalies_telemetry_id     ON anomalies (telemetry_id);
CREATE INDEX idx_anomalies_notification_id  ON anomalies (notification_id);
CREATE INDEX idx_anomalies_detected_at      ON anomalies (detected_at DESC);
CREATE INDEX idx_anomalies_severity         ON anomalies (severity);

COMMENT ON TABLE  anomalies                   IS 'ML-detected sensor anomalies from the detection pipeline.';
COMMENT ON COLUMN anomalies.anomaly_type      IS 'e.g. SOIL_MOISTURE_OUT_OF_RANGE, TEMPERATURE_OUT_OF_RANGE, etc.';
COMMENT ON COLUMN anomalies.severity          IS 'LOW | MEDIUM | HIGH | CRITICAL';
COMMENT ON COLUMN anomalies.anomaly_score     IS 'Isolation Forest / LSTM anomaly score (higher = more anomalous).';
COMMENT ON COLUMN anomalies.detected_by       IS 'Model identifier, e.g. ISOLATION_FOREST, LSTM_AUTOENCODER.';
COMMENT ON COLUMN anomalies.model_version     IS 'Deployed model version string, e.g. 1.0.0.';
COMMENT ON COLUMN anomalies.telemetry_id      IS 'References telemetry.id; no DB-level FK (cross-module).';
COMMENT ON COLUMN anomalies.notification_id   IS 'References notifications.notification_id; nullable — anomaly may not always trigger a notification.';