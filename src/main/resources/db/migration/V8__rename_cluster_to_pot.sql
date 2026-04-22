-- V7__rename_cluster_to_pot.sql
-- Rename clusters table → pots
ALTER TABLE clusters RENAME TO pots;

-- Rename columns inside pots table
ALTER TABLE pots RENAME COLUMN cluster_id   TO pot_id;
ALTER TABLE pots RENAME COLUMN cluster_name TO pot_name;

-- Rename FK column in devices table
ALTER TABLE devices RENAME COLUMN cluster_id TO pot_id;

-- Rename indexes / constraints if they exist (adjust names to match your actual schema)
ALTER INDEX IF EXISTS clusters_pkey               RENAME TO pots_pkey;
ALTER INDEX IF EXISTS clusters_cluster_id_key     RENAME TO pots_pot_id_key;
ALTER INDEX IF EXISTS clusters_cluster_name_key   RENAME TO pots_pot_name_key;