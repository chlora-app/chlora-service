-- =============================================================
-- V1 : users
-- Owned by the auth module; read by the management module.
-- =============================================================

CREATE TABLE users (
    id         BIGSERIAL     PRIMARY KEY,
    user_id    VARCHAR(16)   NOT NULL UNIQUE,
    email      VARCHAR(255)  NOT NULL UNIQUE,
    password   VARCHAR(255)  NOT NULL,
    name       VARCHAR(255)  NOT NULL,
    role       VARCHAR(16)   NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_users_user_id    ON users (user_id);
CREATE INDEX idx_users_email      ON users (email);
CREATE INDEX idx_users_deleted_at ON users (deleted_at);

COMMENT ON TABLE  users             IS 'Application users — owned by the auth module.';
COMMENT ON COLUMN users.user_id     IS 'Business-facing ID, format: U-<6 alphanum>.';
COMMENT ON COLUMN users.role        IS 'USER | ADMIN';
COMMENT ON COLUMN users.deleted_at  IS 'NULL means active; soft-delete pattern.';