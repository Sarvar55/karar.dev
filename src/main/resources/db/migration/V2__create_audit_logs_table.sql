-- ==============================================
-- V2: Create audit_logs table
-- ==============================================
-- Stores a record of every CREATE, UPDATE, and DELETE
-- operation performed on auditable entities.

CREATE TABLE audit_logs  (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_name     VARCHAR(255)    NOT NULL,
    entity_id       VARCHAR(255)    NOT NULL,
    action          VARCHAR(50)     NOT NULL,
    performed_by    VARCHAR(255)    NOT NULL,
    details         TEXT,
    ip_address      VARCHAR(45),
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Performance indexes for common query patterns
CREATE INDEX idx_audit_entity_name   ON audit_logs (entity_name);
CREATE INDEX idx_audit_performed_by  ON audit_logs (performed_by);
CREATE INDEX idx_audit_action        ON audit_logs (action);
CREATE INDEX idx_audit_created_at    ON audit_logs (created_at);
