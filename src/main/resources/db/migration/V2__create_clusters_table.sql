-- =============================================================
-- V2 : clusters
-- Owned by management.cluster module.
-- =============================================================

CREATE TABLE clusters (
    id           BIGSERIAL    PRIMARY KEY,
    cluster_id   VARCHAR(16)  NOT NULL UNIQUE,
    cluster_name VARCHAR(255) NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    deleted_at   TIMESTAMPTZ
);

CREATE INDEX idx_clusters_cluster_id  ON clusters (cluster_id);
CREATE INDEX idx_clusters_deleted_at  ON clusters (deleted_at);

COMMENT ON TABLE  clusters               IS 'Device grouping units (grow clusters / racks).';
COMMENT ON COLUMN clusters.cluster_id   IS 'Business-facing ID, format: CL-<6 alphanum>.';
COMMENT ON COLUMN clusters.deleted_at   IS 'NULL means active; soft-delete pattern.';