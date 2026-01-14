-- Add rating fields to encounters table
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'encounters' AND column_name = 'rating') THEN
        ALTER TABLE encounters ADD COLUMN rating integer;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'encounters' AND column_name = 'rating_comment') THEN
        ALTER TABLE encounters ADD COLUMN rating_comment text;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'encounters' AND column_name = 'rated_at') THEN
        ALTER TABLE encounters ADD COLUMN rated_at timestamp;
    END IF;
END $$;

-- Add check constraint for rating (1-5 stars)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'encounters_rating_check') THEN
        ALTER TABLE encounters ADD CONSTRAINT encounters_rating_check CHECK (rating >= 1 AND rating <= 5);
    END IF;
END $$;

-- Add rating cache fields to doctors table
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'doctors' AND column_name = 'average_rating') THEN
        ALTER TABLE doctors ADD COLUMN average_rating double precision;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'doctors' AND column_name = 'rating_count') THEN
        ALTER TABLE doctors ADD COLUMN rating_count integer NOT NULL DEFAULT 0;
    END IF;
END $$;

-- Add check constraint for rating_count (non-negative)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'doctors_rating_count_check') THEN
        ALTER TABLE doctors ADD CONSTRAINT doctors_rating_count_check CHECK (rating_count >= 0);
    END IF;
END $$;
