-- =============================================
-- V7: Fix devices status constraint
--     Add notification_type to notifications
-- =============================================

-- 1. Restore correct device status values (ONLINE/OFFLINE per domain model)
ALTER TABLE devices DROP CONSTRAINT IF EXISTS devices_status_check;
ALTER TABLE devices ADD CONSTRAINT devices_status_check
    CHECK (status IN ('ONLINE', 'OFFLINE'));

-- 2. Add notification_type column to notifications table
ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS notification_type VARCHAR(50) NOT NULL DEFAULT 'ANOMALY';

-- Remove default after backfill so future inserts must be explicit
ALTER TABLE notifications
    ALTER COLUMN notification_type DROP DEFAULT;