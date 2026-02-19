-- Add soft-delete support to riders and drivers
ALTER TABLE riders
    ADD COLUMN IF NOT EXISTS is_deleted  BOOLEAN   DEFAULT FALSE NOT NULL,
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP;

-- Add soft-delete and availability status to drivers
ALTER TABLE drivers
    ADD COLUMN IF NOT EXISTS is_deleted  BOOLEAN   DEFAULT FALSE NOT NULL,
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP,
    ADD COLUMN IF NOT EXISTS status      VARCHAR(10) DEFAULT 'OFFLINE' NOT NULL;
