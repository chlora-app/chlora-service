-- V14: Change battery_level from FLOAT to INTEGER in the telemetry table
-- ESP still sends float, but we store rounded integer (Math.round) at the application layer

ALTER TABLE telemetry
    ALTER COLUMN battery_level TYPE INTEGER
        USING ROUND(battery_level)::INTEGER;