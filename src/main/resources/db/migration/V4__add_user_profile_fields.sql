ALTER TABLE regular_users
    ADD COLUMN photo_url VARCHAR(255),
    ADD COLUMN bio TEXT,
    ADD COLUMN location VARCHAR(255),
    ADD COLUMN job_title VARCHAR(255),
    ADD COLUMN experience TEXT,
    ADD COLUMN open_to TEXT,
    ADD COLUMN website VARCHAR(255),
    ADD COLUMN github_url VARCHAR(255),
    ADD COLUMN twitter_url VARCHAR(255);

CREATE TABLE regular_user_skills (
    user_id UUID NOT NULL,
    skill VARCHAR(255) NOT NULL,
    CONSTRAINT fk_regular_user_skills_user_id FOREIGN KEY (user_id) REFERENCES regular_users(id) ON DELETE CASCADE
);
