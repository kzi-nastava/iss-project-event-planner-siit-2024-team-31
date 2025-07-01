-- V9__init_mock_budget_items_realistic.sql

-- 1) Вставляем бюджетные сервисы: для каждого события 1–3 записи
WITH
    pup_user AS (
        SELECT id AS pup_id
        FROM users
        WHERE email = 'pup'
    ),
    pending_status AS (
        SELECT id AS status_id
        FROM status
        WHERE name = 'PENDING'
    ),
    ev AS (
        SELECT id AS event_id, start_time
        FROM event
        ORDER BY random()
        LIMIT 15
    ),
    pup_services AS (
        SELECT id AS service_id,
               price_per_hour,
               service_duration_min_minutes
        FROM service
        WHERE pup_id = (SELECT pup_id FROM pup_user)
    )
INSERT INTO budget_items (
    version,
    event_id,
    status_id,
    service_id,
    product_id,
    product_count,
    service_start_time,
    service_end_time,
    total_price
)
SELECT
    0,
    e.event_id,
    ps.status_id,
    svc.service_id,
    NULL,
    0,
    e.start_time,
    e.start_time + (svc.service_duration_min_minutes || ' minutes')::interval,
    svc.price_per_hour * svc.service_duration_min_minutes / 60.0
FROM ev e
         CROSS JOIN pending_status ps
         CROSS JOIN LATERAL generate_series(1, floor(random()*3)::int + 1) AS rnd_svc(n)
         CROSS JOIN LATERAL (
    SELECT service_id, price_per_hour, service_duration_min_minutes
    FROM pup_services
    ORDER BY random()
    LIMIT 1
    ) AS svc
;

-- 2) Вставляем бюджетные продукты: для каждого события 1–3 записи
WITH
    pup_user AS (
        SELECT id AS pup_id
        FROM users
        WHERE email = 'pup'
    ),
    pending_status AS (
        SELECT id AS status_id
        FROM status
        WHERE name = 'PENDING'
    ),
    ev AS (
        SELECT id AS event_id
        FROM event
        ORDER BY random()
        LIMIT 15
    ),
    pup_products AS (
        SELECT id AS product_id,
               price
        FROM product
        WHERE pup_id = (SELECT pup_id FROM pup_user)
    )
INSERT INTO budget_items (
    version,
    event_id,
    status_id,
    service_id,
    product_id,
    product_count,
    service_start_time,
    service_end_time,
    total_price
)
SELECT
    0,
    e.event_id,
    ps.status_id,
    NULL,
    prod.product_id,
    (floor(random()*3)::int + 1),
    NULL,
    NULL,
    prod.price * (floor(random()*3)::int + 1)
FROM ev e
         CROSS JOIN pending_status ps
         CROSS JOIN LATERAL generate_series(1, floor(random()*3)::int + 1) AS rnd_prod(n)
         CROSS JOIN LATERAL (
    SELECT product_id, price
    FROM pup_products
    ORDER BY random()
    LIMIT 1
    ) AS prod
;