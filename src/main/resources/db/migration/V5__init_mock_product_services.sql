BEGIN;

-- 1) Insert 20 mock products with random rating
INSERT INTO product (
    version,
    pup_id,
    name,
    category_id,
    description,
    peculiarities,
    price,
    discount,
    is_visible,
    is_available,
    is_deleted,
    rating,
    status_id
)
SELECT
    1,
    (SELECT id FROM users WHERE email = 'pup'),
    'Mock Product ' || gs,
    (
        SELECT id
        FROM product_category
        ORDER BY id
        OFFSET ((gs - 1) % (SELECT COUNT(*) FROM product_category))
            LIMIT 1
    ),
    'Description for mock product ' || gs,
    'Standard features',
    10.0 * gs,
    0.0,
    TRUE,
    TRUE,
    FALSE,
    -- round(random() * 5 to 1 decimal
    round((random() * 5)::numeric, 1)::double precision,
    (SELECT id FROM status WHERE name = 'ACTIVE')
FROM generate_series(1,20) AS gs;

-- 2) Insert 20 mock services with random rating
INSERT INTO service (
    version,
    pup_id,
    category_id,
    name,
    description,
    peculiarities,
    price_per_hour,
    discount,
    is_visible,
    is_available,
    is_deleted,
    time_management,
    service_duration_min_minutes,
    service_duration_max_minutes,
    booking_confirmation,
    booking_decline_deadline_hours,
    rating,
    status_id
)
SELECT
    1,
    (SELECT id FROM users WHERE email = 'pup'),
    (
        SELECT id
        FROM service_category
        ORDER BY id
        OFFSET ((gs - 1) % (SELECT COUNT(*) FROM service_category))
            LIMIT 1
    ),
    'Mock Service ' || gs,
    'Description for mock service ' || gs,
    'Standard features',
    5.0 * gs,
    0.0,
    TRUE,
    TRUE,
    FALSE,
    FALSE,
    30,
    60,
    TRUE,
    24,
    -- round(random() * 5 to 1 decimal
    round((random() * 5)::numeric, 1)::double precision,
    (SELECT id FROM status WHERE name = 'ACTIVE')
FROM generate_series(1,20) AS gs;

COMMIT;