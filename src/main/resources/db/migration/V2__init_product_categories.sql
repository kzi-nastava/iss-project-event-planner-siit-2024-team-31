INSERT INTO product_category (name, description, status_id) VALUES
    (
          'Catering',
          'Food and beverage services for events',
          (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
          'Photography',
          'Professional event photography services',
          (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
          'Music',
          'Music and DJ services for all occasions',
          (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
          'Decorations',
          'Event decorations and theme designs',
          (SELECT id FROM status WHERE name = 'ACTIVE')
        ),
    (
          'Transportation',
          'Transportation services for guests or equipment',
          (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
          'Venue',
          'Event venues and spaces for hire',
          (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
          'Event Planning',
          'Professional event planning and coordination services',
          (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
          'Lighting',
          'Lighting equipment and setup for events',
          (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
          'AV Equipment',
          'Audio-visual equipment rental and support',
          (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
          'Security',
          'Security services to ensure event safety',
          (SELECT id FROM status WHERE name = 'ACTIVE')
    )
ON CONFLICT (name) DO NOTHING;