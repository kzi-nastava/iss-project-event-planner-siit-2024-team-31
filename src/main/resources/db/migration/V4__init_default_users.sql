-- DEVELOPMENT ONLY
INSERT INTO users (email, password_hash, first_name, role_id, is_active, is_deleted, version) VALUES
    (
         'admin',
         '$2a$10$bkYMiLynfP5DCVNKpnS.3eaJiY3.bVPRn0na43wKQPypu7kLiN.Ui',
         'Admin',
         (SELECT id FROM roles WHERE name = 'ADMIN'),
         TRUE,
        FALSE,
        0
    ),
    (
         'user',
         '$2a$10$bkYMiLynfP5DCVNKpnS.3eaJiY3.bVPRn0na43wKQPypu7kLiN.Ui',
         'User',
         (SELECT id FROM roles WHERE name = 'USER'),
         TRUE,
     FALSE,
        0
    ),
    (
         'pup',
         '$2a$10$bkYMiLynfP5DCVNKpnS.3eaJiY3.bVPRn0na43wKQPypu7kLiN.Ui',
         'Pup',
         (SELECT id FROM roles WHERE name = 'PUP'),
         TRUE,
     FALSE,
        0
    ),
    (
         'od',
         '$2a$10$bkYMiLynfP5DCVNKpnS.3eaJiY3.bVPRn0na43wKQPypu7kLiN.Ui',
         'Od',
         (SELECT id FROM roles WHERE name = 'OD'),
         TRUE,
     FALSE,
            0
    );