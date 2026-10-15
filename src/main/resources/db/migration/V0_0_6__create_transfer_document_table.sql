CREATE SEQUENCE transfer_document_number_seq START WITH 1;

CREATE TABLE transfer_documents
(
    id                  BIGSERIAL PRIMARY KEY,
    document_number     VARCHAR(12)    NOT NULL
        CONSTRAINT uq_transfer_documents_document_number UNIQUE,
    transfer_request_id BIGINT         NOT NULL
        CONSTRAINT uq_transfer_documents_transfer_request_id UNIQUE
        CONSTRAINT fk_transfer_documents_transfer_request REFERENCES transfer_requests (id),
    player_id           BIGINT         NOT NULL
        CONSTRAINT fk_transfer_documents_player REFERENCES players (id),
    source_team_id      BIGINT
        CONSTRAINT fk_transfer_documents_source_team REFERENCES teams (id),
    target_team_id      BIGINT         NOT NULL
        CONSTRAINT fk_transfer_documents_target_team REFERENCES teams (id),
    amount              NUMERIC(15, 0) NOT NULL
        CONSTRAINT chk_transfer_documents_amount CHECK ( amount >= 0 ),
    transferred_at      TIMESTAMP      NOT NULL
);