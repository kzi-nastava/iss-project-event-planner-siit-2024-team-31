-- V6__init_mock_events.sql

-- 1) Insert 3 event locations
INSERT INTO event_locations (version, lat, lng, address) VALUES
                                                             (0, 45.2517, 19.8369, 'Novi Sad, Serbia'),
                                                             (0, 44.7866, 20.4489, 'Belgrade, Serbia'),
                                                             (0, 43.3209, 21.8958, 'Niš, Serbia');

-- 2) Insert 25 mock events
INSERT INTO event
(version,
 name,
 description,
 start_time,
 end_time,
 max_num_guests,
 is_private,
 event_type_id,
 location_id,
 organizer_id,
 status_id,
 likes_count,
 rating)
SELECT
    0,
    'Mock Event ' || gs,
    'This is a mock description for event ' || gs || '.',
    now() + (gs * INTERVAL '1 day'),
    now() + (gs * INTERVAL '1 day') + INTERVAL '2 hours',
    20 + ((gs * 3) % 100),
    (gs % 2 = 0),
    (SELECT id FROM event_types ORDER BY random() LIMIT 1),
    1 + ((gs - 1) % 3),
    (SELECT id FROM users       WHERE email = 'od'),
    (SELECT id FROM status      ORDER BY random() LIMIT 1),
    floor(random() * 500),
    round((random() * 5)::numeric, 1)
FROM generate_series(1, 25) AS s(gs);

-- 3) Insert mock photos for each of the 25 new events
--    три фото на событие, используя сервис Picsum
WITH latest_events AS (
    SELECT id
    FROM event
    ORDER BY id DESC
    LIMIT 25
)
INSERT INTO event_photos (version, event_id, url)
SELECT
    0,
    id,
    'https://picsum.photos/seed/event_' || id || '/600/400'
FROM latest_events
UNION ALL
SELECT
    0,
    id,
    'https://picsum.photos/seed/event_' || id || '_2/600/400'
FROM latest_events
UNION ALL
SELECT
    0,
    id,
    'https://picsum.photos/seed/event_' || id || '_3/600/400'
FROM latest_events;