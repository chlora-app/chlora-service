CREATE TABLE event_publication (
    id UUID PRIMARY KEY,
    listener_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    serialized_event TEXT NOT NULL,
    publication_date TIMESTAMP NOT NULL,
    completion_date TIMESTAMP,
    completion_attempts INT,
    last_resubmission_date TIMESTAMP,
    status VARCHAR(50)
);