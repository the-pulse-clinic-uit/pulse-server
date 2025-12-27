-- Make ended_at column nullable in encounters table
-- An encounter is created when it starts but hasn't ended yet

ALTER TABLE encounters ALTER COLUMN ended_at DROP NOT NULL;
