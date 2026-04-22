-- =============================================================
-- V4 : telemetry
-- Owned by pipeline.telemetry module.
-- Append-only time-series; never updated or soft-deleted.
-- device_id references devices.device_id (business key, no FK
-- across module boundaries).
-- =============================================================

CREATE TABLE telemetry (
    id               BIGSERIAL   PRIMARY KEY,
    device_id        VARCHAR(24) NOT NULL,
    device_timestamp TIMESTAMPTZ NOT NULL,
    soil_moisture    REAL        NOT NULL,
    temperature      REAL        NOT NULL,
    humidity         REAL        NOT NULL,
    battery_level    REAL        NOT NULL,
    received_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_valid         BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_telemetry_device_id        ON telemetry (device_id);
CREATE INDEX idx_telemetry_device_timestamp ON telemetry (device_timestamp DESC);
CREATE INDEX idx_telemetry_received_at      ON telemetry (received_at DESC);
CREATE INDEX idx_telemetry_device_time      ON telemetry (device_id, device_timestamp DESC);

COMMENT ON TABLE  telemetry                  IS 'Raw sensor readings ingested via MQTT pipeline.';
COMMENT ON COLUMN telemetry.device_id        IS 'References devices.device_id; no DB-level FK (cross-module).';
COMMENT ON COLUMN telemetry.device_timestamp IS 'Timestamp reported by the ESP32 node (epoch-derived).';
COMMENT ON COLUMN telemetry.received_at      IS 'Timestamp when the Spring Boot backend received the MQTT message.';
COMMENT ON COLUMN telemetry.is_valid         IS 'FALSE when the telemetry validator rejects the reading.';