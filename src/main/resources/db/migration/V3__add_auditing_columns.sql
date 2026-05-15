-- ==============================================
-- V3: Add created_by and updated_by columns
--     to all existing tables for JPA Auditing
-- ==============================================

-- Users
ALTER TABLE users ADD COLUMN created_by VARCHAR(255);
ALTER TABLE users ADD COLUMN updated_by VARCHAR(255);

-- Decisions
ALTER TABLE decisions ADD COLUMN created_by VARCHAR(255);
ALTER TABLE decisions ADD COLUMN updated_by VARCHAR(255);

-- Comments
ALTER TABLE comments ADD COLUMN created_by VARCHAR(255);
ALTER TABLE comments ADD COLUMN updated_by VARCHAR(255);

-- Tags
ALTER TABLE tags ADD COLUMN created_by VARCHAR(255);
ALTER TABLE tags ADD COLUMN updated_by VARCHAR(255);

-- Decision Tags
ALTER TABLE decision_tags ADD COLUMN created_by VARCHAR(255);
ALTER TABLE decision_tags ADD COLUMN updated_by VARCHAR(255);

-- Votes
ALTER TABLE votes ADD COLUMN created_by VARCHAR(255);
ALTER TABLE votes ADD COLUMN updated_by VARCHAR(255);

-- Backfill existing rows with 'SYSTEM' as the creator
UPDATE users SET created_by = 'SYSTEM', updated_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE decisions SET created_by = 'SYSTEM', updated_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE comments SET created_by = 'SYSTEM', updated_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE tags SET created_by = 'SYSTEM', updated_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE decision_tags SET created_by = 'SYSTEM', updated_by = 'SYSTEM' WHERE created_by IS NULL;
UPDATE votes SET created_by = 'SYSTEM', updated_by = 'SYSTEM' WHERE created_by IS NULL;
