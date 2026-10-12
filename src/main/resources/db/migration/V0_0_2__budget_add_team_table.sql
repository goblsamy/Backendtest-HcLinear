ALTER TABLE teams
    ADD COLUMN budget NUMERIC(15, 0) NOT NULL DEFAULT 0
        CONSTRAINT chk_teams_budget CHECK (budget >= 0);