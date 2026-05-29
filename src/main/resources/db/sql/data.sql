-- =========================================
-- USERS
-- =========================================

INSERT INTO users (id, email, password, role, email_verified, account_locked, failed_login_attempts)
VALUES
    ('b76701b9-0db8-48e8-b2e7-057b7712df1c', 'user1@karar.dev', '{noop}pass', 'USER', true, false, 0),
    ('c11101b9-1db8-48e8-b2e7-057b7712df1c', 'user2@karar.dev', '{noop}pass', 'USER', true, false, 0),
    ('d22201b9-2db8-48e8-b2e7-057b7712df1c', 'company@karar.dev', '{noop}pass', 'COMPANY', true, false, 0)
    ON CONFLICT (id) DO NOTHING;

-- =========================================
-- REGULAR USERS & COMPANIES
-- =========================================

INSERT INTO regular_users (id, username, photo_url, bio, location, job_title, experience, open_to, website, github_url, twitter_url)
VALUES
    ('b76701b9-0db8-48e8-b2e7-057b7712df1c', 'user1', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user1', 'Backend developer. Bugüne kadar birçok teknik karar aldım.', 'Istanbul, Turkey', 'SENIOR BACKEND · 8Y', '8 years backend engineering. Currently at TechCorp working on distributed systems and data infrastructure.', 'Backend consulting, technical writing, and interesting architecture challenges.', 'aysekaya.dev', 'github.com/aysekaya', '@aysekaya'),
    ('c11101b9-1db8-48e8-b2e7-057b7712df1c', 'user2', 'https://api.dicebear.com/7.x/avataaars/svg?seed=user2', 'Fullstack engineer focusing on React and Spring Boot.', 'Ankara, Turkey', 'FULLSTACK DEVELOPER', '5 years of experience in web development.', 'Open source contributions and freelance projects.', 'user2.dev', 'github.com/user2', '@user2')
    ON CONFLICT (id) DO UPDATE SET 
        photo_url = EXCLUDED.photo_url,
        bio = EXCLUDED.bio,
        location = EXCLUDED.location,
        job_title = EXCLUDED.job_title,
        experience = EXCLUDED.experience,
        open_to = EXCLUDED.open_to,
        website = EXCLUDED.website,
        github_url = EXCLUDED.github_url,
        twitter_url = EXCLUDED.twitter_url;

INSERT INTO regular_user_skills (user_id, skill)
SELECT user_id, skill FROM (
    VALUES
        ('b76701b9-0db8-48e8-b2e7-057b7712df1c'::uuid, 'PostgreSQL'),
        ('b76701b9-0db8-48e8-b2e7-057b7712df1c'::uuid, 'Redis'),
        ('b76701b9-0db8-48e8-b2e7-057b7712df1c'::uuid, 'Spring Boot'),
        ('b76701b9-0db8-48e8-b2e7-057b7712df1c'::uuid, 'Kubernetes'),
        ('b76701b9-0db8-48e8-b2e7-057b7712df1c'::uuid, 'System Design'),
        ('b76701b9-0db8-48e8-b2e7-057b7712df1c'::uuid, 'Java'),
        ('b76701b9-0db8-48e8-b2e7-057b7712df1c'::uuid, 'Docker'),
        ('c11101b9-1db8-48e8-b2e7-057b7712df1c'::uuid, 'React'),
        ('c11101b9-1db8-48e8-b2e7-057b7712df1c'::uuid, 'TypeScript'),
        ('c11101b9-1db8-48e8-b2e7-057b7712df1c'::uuid, 'Node.js')
) AS data(user_id, skill)
WHERE NOT EXISTS (
    SELECT 1 FROM regular_user_skills t 
    WHERE t.user_id = data.user_id AND t.skill = data.skill
);

INSERT INTO company_users (id, company_name)
VALUES
    ('d22201b9-2db8-48e8-b2e7-057b7712df1c', 'Karar Dev Ltd')
    ON CONFLICT (id) DO NOTHING;

-- =========================================
-- DECISIONS
-- =========================================

INSERT INTO decisions (id, title, why, alternative, regret_level, vote_count, user_id)
VALUES
    ('d0000000-0000-0000-0000-000000000001', 'Migrated monolith to microservices',  'Scalability issues',            'Keep modular monolith',     'LOW',    0, 'b76701b9-0db8-48e8-b2e7-057b7712df1c'),
    ('d0000000-0000-0000-0000-000000000002', 'Switched from REST to GraphQL',        'Overfetching problem',          'Optimize REST endpoints',   'MEDIUM', 0, 'b76701b9-0db8-48e8-b2e7-057b7712df1c'),
    ('d0000000-0000-0000-0000-000000000003', 'Adopted PostgreSQL over MySQL',         'Better JSON support',           'Stick with MySQL',          'LOW',    0, 'b76701b9-0db8-48e8-b2e7-057b7712df1c'),
    ('d0000000-0000-0000-0000-000000000004', 'Moved caching to Redis',               'Performance bottlenecks',       'In-memory caching',         'LOW',    0, 'c11101b9-1db8-48e8-b2e7-057b7712df1c'),
    ('d0000000-0000-0000-0000-000000000005', 'Introduced CQRS pattern',              'Read/write scaling separation', 'Single model architecture', 'MEDIUM', 0, 'c11101b9-1db8-48e8-b2e7-057b7712df1c'),
    ('d0000000-0000-0000-0000-000000000006', 'Used JWT instead of session auth',     'Stateless architecture',        'Server sessions',           'LOW',    0, 'c11101b9-1db8-48e8-b2e7-057b7712df1c'),
    ('d0000000-0000-0000-0000-000000000007', 'Adopted Docker for deployment',        'Environment consistency',       'Manual deployment',         'LOW',    0, 'b76701b9-0db8-48e8-b2e7-057b7712df1c'),
    ('d0000000-0000-0000-0000-000000000008', 'Introduced Kafka messaging',           'Async processing needs',        'REST sync calls',           'LOW',    0, 'c11101b9-1db8-48e8-b2e7-057b7712df1c'),
    ('d0000000-0000-0000-0000-000000000009', 'Moved to Spring Boot 3',               'Modern Java features',          'Spring Boot 2',             'LOW',    0, 'b76701b9-0db8-48e8-b2e7-057b7712df1c'),
    ('d0000000-0000-0000-0000-000000000010', 'Introduced Redis rate limiting',       'Prevent brute force attacks',   'In-memory counters',        'LOW',    0, 'c11101b9-1db8-48e8-b2e7-057b7712df1c')
    ON CONFLICT (id) DO NOTHING;

-- =========================================
-- COMMENTS
-- =========================================

INSERT INTO comments (id, content, decision_id, user_id)
VALUES
    ('c0000000-0000-0000-0000-000000000001',
     'Great decision, improved scalability a lot',
     'd0000000-0000-0000-0000-000000000001',   -- Migrated monolith to microservices
     'c11101b9-1db8-48e8-b2e7-057b7712df1c'),  -- user2

    ('c0000000-0000-0000-0000-000000000002',
     'We had similar issue in our company',
     'd0000000-0000-0000-0000-000000000002',   -- Switched from REST to GraphQL
     'c11101b9-1db8-48e8-b2e7-057b7712df1c'),  -- user2

    ('c0000000-0000-0000-0000-000000000003',
     'Redis was a game changer for us',
     'd0000000-0000-0000-0000-000000000004',   -- Moved caching to Redis
     'b76701b9-0db8-48e8-b2e7-057b7712df1c')   -- user1
    ON CONFLICT (id) DO NOTHING;

-- =========================================
-- VOTES
-- =========================================

INSERT INTO votes (id, user_id, decision_id)
VALUES
    ('a0000000-0000-0000-0000-000000000001',
     'c11101b9-1db8-48e8-b2e7-057b7712df1c',   -- user2
     'd0000000-0000-0000-0000-000000000001'),   -- Migrated monolith to microservices

    ('a0000000-0000-0000-0000-000000000002',
     'b76701b9-0db8-48e8-b2e7-057b7712df1c',   -- user1
     'd0000000-0000-0000-0000-000000000004'),   -- Moved caching to Redis

    ('a0000000-0000-0000-0000-000000000003',
     'b76701b9-0db8-48e8-b2e7-057b7712df1c',   -- user1
     'd0000000-0000-0000-0000-000000000010')    -- Introduced Redis rate limiting
    ON CONFLICT (id) DO NOTHING;