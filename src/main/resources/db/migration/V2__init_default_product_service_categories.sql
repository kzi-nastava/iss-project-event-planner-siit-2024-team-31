INSERT INTO service_category (name, description, status_id, version) VALUES
    (
        'Catering',
        'Food and beverage services for events',
        (SELECT id FROM status WHERE name = 'ACTIVE'),
        1
    ),
    (
        'Photography',
        'Professional event photography services',
        (SELECT id FROM status WHERE name = 'ACTIVE'),
        1
    ),
    (
        'Music',
        'Music and DJ services for all occasions',
        (SELECT id FROM status WHERE name = 'ACTIVE'),
        1
    ),
    (
        'Decorations',
        'Event decorations and theme designs',
        (SELECT id FROM status WHERE name = 'ACTIVE'),
        1
    ),
    (
        'Transportation',
        'Guest and equipment transportation services',
        (SELECT id FROM status WHERE name = 'ACTIVE'),
        1
    ),
    (
        'Venue Rental',
        'Rental of halls, conference rooms and outdoor spaces',
        (SELECT id FROM status WHERE name = 'ACTIVE'),
        1
    ),
    (
        'Event Planning',
        'Full-service event planning and coordination',
        (SELECT id FROM status WHERE name = 'ACTIVE'),
        1
    ),
    (
        'Lighting',
        'Design and setup of event lighting',
        (SELECT id FROM status WHERE name = 'ACTIVE'),
        1
    ),
    (
        'AV Equipment',
        'Audio-visual equipment rental and on-site support',
        (SELECT id FROM status WHERE name = 'ACTIVE'),
        1
    ),
    (
        'Security',
        'Professional security and crowd management',
        (SELECT id FROM status WHERE name = 'ACTIVE'),
        1
)
ON CONFLICT (name) DO NOTHING;

INSERT INTO product_category (name, description, status_id, version) VALUES
     (
         'Furniture',
         'Chairs, tables and seating arrangements',
         (SELECT id FROM status WHERE name = 'ACTIVE'),
         1
     ),
     (
         'Decor Items',
         'Physical decorative items like centerpieces, drapes, props',
         (SELECT id FROM status WHERE name = 'ACTIVE'),
         1
     ),
     (
         'Audio Equipment',
         'Speakers, microphones and mixing consoles',
         (SELECT id FROM status WHERE name = 'ACTIVE'),
         1
     ),
     (
         'Lighting Equipment',
         'Portable lighting fixtures and accessories',
         (SELECT id FROM status WHERE name = 'ACTIVE'),
         1
     ),
     (
         'Printed Materials',
         'Banners, invitations, signage and printed programs',
         (SELECT id FROM status WHERE name = 'ACTIVE'),
         1
     ),
     (
         'Merchandise',
         'Branded giveaways, souvenirs and promotional items',
         (SELECT id FROM status WHERE name = 'ACTIVE'),
         1
     ),
     (
         'Tableware',
         'Plates, glasses, cutlery and serving dishes',
         (SELECT id FROM status WHERE name = 'ACTIVE'),
         1
     ),
     (
         'Flowers',
         'Floral arrangements, bouquets and decorative greenery',
         (SELECT id FROM status WHERE name = 'ACTIVE'),
         1
     ),
     (
         'Party Favors',
         'Small gift packages and tokens for guests',
         (SELECT id FROM status WHERE name = 'ACTIVE'),
         1
     ),
     (
         'Tents & Canopies',
         'Outdoor tents, marquees and canopies',
         (SELECT id FROM status WHERE name = 'ACTIVE'),
         1
     )
ON CONFLICT (name) DO NOTHING;