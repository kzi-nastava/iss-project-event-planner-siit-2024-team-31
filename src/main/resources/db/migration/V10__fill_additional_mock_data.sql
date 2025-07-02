-- V__fill_additional_mock_data.sql

-- 1) Create a few more users
INSERT INTO users (email, password_hash, first_name, role_id, is_active, is_deleted, version)
VALUES
    ('user1', '$2a$10$bkYMiLynfP5DCVNKpnS.3eaJiY3.bVPRn0na43wKQPypu7kLiN.Ui', 'User One',   (SELECT id FROM roles WHERE name='USER'), TRUE, FALSE, 0),
    ('user2', '$2a$10$bkYMiLynfP5DCVNKpnS.3eaJiY3.bVPRn0na43wKQPypu7kLiN.Ui', 'User Two',   (SELECT id FROM roles WHERE name='USER'), TRUE, FALSE, 0),
    ('user3', '$2a$10$bkYMiLynfP5DCVNKpnS.3eaJiY3.bVPRn0na43wKQPypu7kLiN.Ui', 'User Three', (SELECT id FROM roles WHERE name='USER'), TRUE, FALSE, 0);

-- 2) Book a handful of pup’s budget items into reservation_list (status APPROVED)
WITH pup_bi AS (
    SELECT id, service_start_time, service_end_time
    FROM budget_items
    WHERE service_id IS NOT NULL
      AND status_id = (SELECT id FROM status WHERE name = 'PENDING')
    ORDER BY id
    LIMIT 3
)
INSERT INTO reservation_list (
    version, count, start_time, end_time, service_id, budget_item_id, status_id
)
SELECT
    0,
    1,
    bi.service_start_time,
    bi.service_end_time,
    NULL,
    bi.id,
    (SELECT id FROM status WHERE name = 'APPROVED')
FROM pup_bi bi;

-- 3) Register our new users for the first 5 events (status APPROVED)
WITH regs AS (
    SELECT id AS event_id, start_time
    FROM event
    ORDER BY id
    LIMIT 5
)
INSERT INTO event_registrations (
    version, event_id, user_id, registration_date, status_id, cancellation_reason, age_group, location
)
SELECT
    0,
    r.event_id,
    u.id,
    r.start_time - INTERVAL '2 days',
    (SELECT id FROM status WHERE name = 'APPROVED'),
    NULL,
    CASE u.email
        WHEN 'user1' THEN '18-25'
        WHEN 'user2' THEN '26-35'
        WHEN 'user3' THEN '36-45'
        END,
    CASE r.event_id
        WHEN 1 THEN 'Novi Sad, Serbia'
        WHEN 2 THEN 'Belgrade, Serbia'
        WHEN 3 THEN 'Niš, Serbia'
        ELSE 'Belgrade, Serbia'
        END
FROM regs r
         CROSS JOIN (
    SELECT id, email FROM users WHERE email IN ('user1','user2','user3')
) u;

-- 4) Provide ratings for those registrations
INSERT INTO event_ratings (
    version, event_id, user_id, rating, comment, rating_date, is_anonymous
)
VALUES
    (0, 1, (SELECT id FROM users WHERE email='user1'), 5, 'Exceptional event!', now() - INTERVAL '1 day', FALSE),
    (0, 2, (SELECT id FROM users WHERE email='user2'), 4, 'Really enjoyed it.',  now() - INTERVAL '1 day', FALSE),
    (0, 3, (SELECT id FROM users WHERE email='user3'), 3, 'It was OK.',          now() - INTERVAL '1 day', TRUE),
    (0, 4, (SELECT id FROM users WHERE email='user1'), 4, 'Great speakers!',     now() - INTERVAL '2 days', FALSE),
    (0, 5, (SELECT id FROM users WHERE email='user2'), 5, 'Loved every minute.', now() - INTERVAL '2 days', FALSE);

-- 5) Record attendances for those same users/events (status COMPLETED)
INSERT INTO event_attendances (
    version, event_id, user_id, check_in_time, check_out_time, status_id, attendance_duration_minutes
)
VALUES
    (0, 1, (SELECT id FROM users WHERE email='user1'),
     (SELECT start_time + INTERVAL '10 minutes'  FROM event WHERE id=1),
     (SELECT end_time   - INTERVAL '15 minutes'  FROM event WHERE id=1),
     (SELECT id         FROM status WHERE name='COMPLETED'),
     EXTRACT(EPOCH FROM ((SELECT end_time - INTERVAL '15 minutes' FROM event WHERE id=1)
         - (SELECT start_time + INTERVAL '10 minutes' FROM event WHERE id=1))) / 60),
    (0, 2, (SELECT id FROM users WHERE email='user2'),
     (SELECT start_time + INTERVAL '5 minutes'   FROM event WHERE id=2),
     (SELECT end_time   - INTERVAL '10 minutes'  FROM event WHERE id=2),
     (SELECT id         FROM status WHERE name='COMPLETED'),
     EXTRACT(EPOCH FROM ((SELECT end_time - INTERVAL '10 minutes' FROM event WHERE id=2)
         - (SELECT start_time + INTERVAL '5 minutes'  FROM event WHERE id=2))) / 60),
    (0, 3, (SELECT id FROM users WHERE email='user3'),
     (SELECT start_time + INTERVAL '20 minutes'  FROM event WHERE id=3),
     (SELECT end_time   - INTERVAL '5 minutes'   FROM event WHERE id=3),
     (SELECT id         FROM status WHERE name='COMPLETED'),
     EXTRACT(EPOCH FROM ((SELECT end_time - INTERVAL '5 minutes'  FROM event WHERE id=3)
         - (SELECT start_time + INTERVAL '20 minutes' FROM event WHERE id=3))) / 60);

COMMIT;