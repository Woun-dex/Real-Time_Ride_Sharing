ALTER TABLE trips ADD COLUMN rating INTEGER CHECK (rating >= 1 AND rating <= 5);
