-- Add stock management fields to drugs table
DO $$
BEGIN
    -- Add quantity column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'drugs' AND column_name = 'quantity') THEN
        ALTER TABLE drugs ADD COLUMN quantity integer NOT NULL DEFAULT 0;
    END IF;

    -- Add expiry_date column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'drugs' AND column_name = 'expiry_date') THEN
        ALTER TABLE drugs ADD COLUMN expiry_date date;
    END IF;

    -- Add min_stock_level column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'drugs' AND column_name = 'min_stock_level') THEN
        ALTER TABLE drugs ADD COLUMN min_stock_level integer NOT NULL DEFAULT 10;
    END IF;

    -- Add batch_number column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'drugs' AND column_name = 'batch_number') THEN
        ALTER TABLE drugs ADD COLUMN batch_number varchar(255);
    END IF;
END $$;

-- Add check constraint to ensure quantity is non-negative
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'drugs_quantity_check') THEN
        ALTER TABLE drugs ADD CONSTRAINT drugs_quantity_check CHECK (quantity >= 0);
    END IF;
END $$;

-- Add check constraint to ensure min_stock_level is non-negative
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'drugs_min_stock_level_check') THEN
        ALTER TABLE drugs ADD CONSTRAINT drugs_min_stock_level_check CHECK (min_stock_level >= 0);
    END IF;
END $$;
