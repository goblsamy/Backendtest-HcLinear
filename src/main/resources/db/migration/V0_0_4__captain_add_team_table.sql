ALTER TABLE teams
    ADD COLUMN captain_id BIGINT
        CONSTRAINT fk_teams_captain REFERENCES players (id)
        CONSTRAINT uq_teams_captain UNIQUE;