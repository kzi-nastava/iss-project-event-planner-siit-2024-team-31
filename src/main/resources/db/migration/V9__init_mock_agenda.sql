WITH ev AS (
    SELECT
        id,
        start_time,
        end_time,
        extract(epoch FROM end_time - start_time) / 60 AS duration_minutes
    FROM event
    ORDER BY id DESC
    LIMIT 30
)

INSERT INTO agenda_items (
    version,
    title,
    description,
    location,
    start_time,
    end_time,
    event_id
)
SELECT
    0 AS version,
    'Agenda Item ' || seq || ' for Event ' || ev.id AS title,
    'This is mock agenda item ' || seq || ' for event ' || ev.id || '.' AS description,
    'Room ' || (floor(random()*10)::int + 1) AS location,
    ev.start_time
        + (floor(random() * (ev.duration_minutes - item_dur))::int || ' minutes')::interval AS start_time,
    (ev.start_time
        + (floor(random() * (ev.duration_minutes - item_dur))::int || ' minutes')::interval)
        + (item_dur::text || ' minutes')::interval AS end_time,
    ev.id AS event_id
FROM ev
         CROSS JOIN LATERAL generate_series(1, floor(random()*4)::int + 2) AS s(seq)
         CROSS JOIN LATERAL (
    SELECT (floor(random()*30)::int + 10) AS item_dur
    ) AS d;
