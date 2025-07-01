WITH
    u AS (
        SELECT id AS user_id
        FROM users
        WHERE email = 'user'
    ),
    st AS (
        SELECT id AS status_id
        FROM status
        WHERE name = 'PENDING'
    ),
    new_events AS (
        SELECT
            id,
            row_number() OVER (ORDER BY id) AS rn
        FROM (
                 SELECT id
                 FROM event
                 ORDER BY id DESC
                 LIMIT 30
             ) sub
    )
INSERT INTO invite_list (version, user_id, event_id, status_id)
SELECT
    0,
    u.user_id,
    ne.id,
    st.status_id
FROM new_events ne
         CROSS JOIN u
         CROSS JOIN st
WHERE ne.rn % 4 != 0;