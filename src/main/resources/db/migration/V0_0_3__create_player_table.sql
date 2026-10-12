CREATE TABLE players
(
    id           BIGSERIAL PRIMARY KEY,
    first_name   VARCHAR(75)    NOT NULL,
    last_name    VARCHAR(75)    NOT NULL,
    position     VARCHAR(10)    NOT NULL
        CONSTRAINT chk_players_position CHECK (position IN ('GK', 'DF', 'MF', 'FW')),
    shirt_number INTEGER
        CONSTRAINT chk_players_shirt_number CHECK (shirt_number BETWEEN 1 AND 99),
    birth_date   DATE           NOT NULL,
    market_value NUMERIC(15, 0) NOT NULL
        CONSTRAINT chk_players_market_value CHECK (market_value >= 0),
    team_id      BIGINT
        CONSTRAINT fk_players_team REFERENCES teams (id),
    CONSTRAINT uq_players_team_id_shirt_number UNIQUE (team_id, shirt_number)
);
