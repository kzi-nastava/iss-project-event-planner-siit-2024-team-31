-- V6__init_mock_events.sql

-- 1) Insert 3 event locations
INSERT INTO event_locations (version, lat, lng, address) VALUES
                                                             (0, 45.2517, 19.8369, 'Novi Sad, Serbia'),
                                                             (0, 44.7866, 20.4489, 'Belgrade, Serbia'),
                                                             (0, 43.3209, 21.8958, 'Niš, Serbia');

WITH series AS (
    SELECT
        gs,
        -- Генерим случайный момент в пределах 3 месяцев от начала текущего месяца
        date_trunc('month', now())
            + (floor(random()*3)::int * INTERVAL '1 month')
            + (floor(random()*25)::int * INTERVAL '1 day')
            + (floor(random()*23)::int * INTERVAL '1 hour')
            AS ts
    FROM generate_series(1,30) AS s(gs)
)
INSERT INTO event
    (version, name, description, start_time, end_time,
    max_num_guests, is_private, event_type_id,
    location_id, organizer_id, status_id,
    likes_count, rating)
SELECT
    0,
    'Mock Event ' || gs,
    'This is a mock description for event ' || gs || '.',
    ts,
    ts + INTERVAL '2 hours',
    10 + (floor(random()*90)::int),
    (random() < 0.5),
    (SELECT id FROM event_types ORDER BY random() LIMIT 1),
    1 + ((gs - 1) % 3),
    (SELECT id FROM users WHERE email = 'od'),
    (SELECT id FROM status ORDER BY random() LIMIT 1),
    floor(random() * 500),
    round((random() * 5)::numeric, 1)
FROM series;