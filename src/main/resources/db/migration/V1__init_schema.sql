CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                       account_locked BOOLEAN NOT NULL DEFAULT FALSE,
                       failed_login_attempts INTEGER NOT NULL DEFAULT 0,
                       locked_until TIMESTAMP,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE regular_users (
                               id UUID PRIMARY KEY REFERENCES users(id),
                               username VARCHAR(255)
);


CREATE TABLE company_users (
                               id UUID PRIMARY KEY REFERENCES users(id),
                               company_name VARCHAR(255)
);

CREATE TABLE decisions (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           title VARCHAR(255),
                           why TEXT,
                           alternative TEXT,
                           regret_level VARCHAR(50),
                           vote_count INTEGER NOT NULL DEFAULT 0,
                           user_id UUID REFERENCES regular_users(id),
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comments (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          content TEXT,
                          user_id UUID REFERENCES regular_users(id),
                          decision_id UUID REFERENCES decisions(id),
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tags (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      name VARCHAR(100) NOT NULL UNIQUE,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE decision_tags (
                               decision_id UUID REFERENCES decisions(id) ON DELETE CASCADE,
                               tag_id UUID REFERENCES tags(id) ON DELETE CASCADE,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (decision_id, tag_id)
);

CREATE TABLE votes (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       user_id UUID NOT NULL REFERENCES regular_users(id),
                       decision_id UUID NOT NULL REFERENCES decisions(id),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT votes_user_decision_unique UNIQUE (user_id, decision_id)
);