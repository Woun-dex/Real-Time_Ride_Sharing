-- Widen phone column to accommodate international formats (e.g. +1 (555) 555-5555)
ALTER TABLE riders  ALTER COLUMN phone TYPE VARCHAR(30);
ALTER TABLE drivers ALTER COLUMN phone TYPE VARCHAR(30);
