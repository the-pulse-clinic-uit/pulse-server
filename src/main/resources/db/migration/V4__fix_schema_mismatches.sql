-- add department_id to staff table (if not exists)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'staff' AND column_name = 'department_id') THEN
        ALTER TABLE staff ADD COLUMN department_id uuid;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'fk_staff_department_id') THEN
        ALTER TABLE staff ADD CONSTRAINT fk_staff_department_id FOREIGN KEY (department_id) REFERENCES departments(id);
    END IF;
END $$;

-- rename ticket_np to ticket_no in waitlist_entries table (if needed)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'waitlist_entries' AND column_name = 'ticket_np') THEN
        ALTER TABLE waitlist_entries RENAME COLUMN ticket_np TO ticket_no;
    END IF;
END $$;

-- fix prescriptions.status column type from smallint to varchar
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'prescriptions' AND column_name = 'status'
               AND data_type = 'smallint') THEN
        ALTER TABLE prescriptions ADD COLUMN status_temp varchar(255);

        UPDATE prescriptions SET status_temp =
            CASE
                WHEN status = 0 THEN 'DRAFT'
                WHEN status = 1 THEN 'DISPENSED'
                WHEN status = 2 THEN 'CANCELLED'
                ELSE 'DRAFT'
            END;

        ALTER TABLE prescriptions DROP CONSTRAINT prescriptions_status_check;
        ALTER TABLE prescriptions DROP COLUMN status;
        ALTER TABLE prescriptions RENAME COLUMN status_temp TO status;
        ALTER TABLE prescriptions ALTER COLUMN status SET NOT NULL;

        ALTER TABLE prescriptions ADD CONSTRAINT prescriptions_status_check
            CHECK (status IN ('DRAFT', 'DISPENSED', 'CANCELLED'));
    END IF;
END $$;
