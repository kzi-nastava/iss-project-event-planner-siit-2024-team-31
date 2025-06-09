INSERT INTO event_types (name, description, status_id) VALUES
    (
      'All',
      'Apply this event type to events that do not fit into any specific category or combine elements from multiple categories. A general-purpose option for diverse or hybrid events.',
      (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
      'Conference',
      'Formal gatherings focused on knowledge-sharing, expert discussions, and presentations within a specific industry or field. Ideal for professional networking and collaborative learning.',
      (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
      'Exhibition',
      'Events showcasing products, services, art, or innovations to a targeted audience. Designed for demonstrations, promotions, and direct engagement with attendees.',
      (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
      'Festival',
      'Large-scale celebrations featuring cultural, artistic, or entertainment activities. Includes live performances, food, games, and community-driven experiences.',
      (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
      'Networking',
      'Events centered around building professional or social connections. Facilitates interactions between participants through structured activities or informal mingling.',
      (SELECT id FROM status WHERE name = 'ACTIVE')
    ),
    (
      'Workshop',
      'Interactive sessions aimed at skill development, hands-on training, or collaborative problem-solving. Led by experts to encourage active participation and learning.',
      (SELECT id FROM status WHERE name = 'INACTIVE')
    );