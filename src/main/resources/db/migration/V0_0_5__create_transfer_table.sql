CREATE TABLE transfer_requests
(
    id             BIGSERIAL PRIMARY KEY,
    player_id      BIGINT         NOT NULL
        CONSTRAINT fk_transfer_requests_player REFERENCES players (id),
    source_team_id BIGINT
        CONSTRAINT fk_transfer_requests_source_team REFERENCES teams (id),
    target_team_id BIGINT         NOT NULL
        CONSTRAINT fk_transfer_requests_target_team REFERENCES teams (id),
    amount         NUMERIC(15, 0) NOT NULL
        CONSTRAINT chk_transfer_requests_amount CHECK ( amount >= 0 ),
    status         VARCHAR(20)    NOT NULL
        CONSTRAINT chk_transfer_requests_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CLOSED') ),
    created_at     TIMESTAMP      NOT NULL,
    expires_at     TIMESTAMP      NOT NULL,
    decided_at     TIMESTAMP,
    CONSTRAINT chk_transfer_requests_source_and_target_team CHECK (source_team_id <> target_team_id),
    CONSTRAINT chk_transfer_requests_expires_date CHECK (created_at < expires_at )
);