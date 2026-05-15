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

INSERT INTO regular_users (id, username)
VALUES
    ('b76701b9-0db8-48e8-b2e7-057b7712df1c', 'user1'),
    ('c11101b9-1db8-48e8-b2e7-057b7712df1c', 'user2')
    ON CONFLICT (id) DO NOTHING;

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