CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       version INTEGER,
                       name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE status (
                        id BIGSERIAL PRIMARY KEY,
                        version INTEGER,
                        name VARCHAR(255),
                        description VARCHAR(255)
);

CREATE TABLE photos (
                        id BIGSERIAL PRIMARY KEY,
                        version INTEGER
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       version INTEGER,
                       is_active BOOLEAN default FALSE,
                       is_deleted BOOLEAN,
                       registration_date TIMESTAMP(6),
                       role_id BIGINT NOT NULL,
                       address VARCHAR(255),
                       city VARCHAR(255),
                       company_description VARCHAR(255),
                       country VARCHAR(255),
                       email VARCHAR(255),
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       password_hash VARCHAR(255),
                       phone_number VARCHAR(255),
                       zip_code VARCHAR(255)
);

CREATE TABLE user_photo (
                            id BIGSERIAL PRIMARY KEY,
                            version INTEGER,
                            user_id BIGINT NOT NULL,
                            url VARCHAR(255)
);

CREATE TABLE password_reset_tokens (
                                       id BIGSERIAL PRIMARY KEY,
                                       version INTEGER,
                                       used BOOLEAN,
                                       expiry_at TIMESTAMP(6) WITH TIME ZONE,
                                       user_id BIGINT,
                                       token_hash VARCHAR(255)
);

CREATE TABLE event_types (
                             id BIGSERIAL PRIMARY KEY,
                             version INTEGER,
                             status_id BIGINT,
                             name VARCHAR(255),
                             description VARCHAR(255)
);

CREATE TABLE event_locations (
                                 id BIGSERIAL PRIMARY KEY,
                                 version INTEGER
);

CREATE TABLE event (
                       id BIGSERIAL PRIMARY KEY,
                       version INTEGER,
                       name VARCHAR(255),
                       description VARCHAR(255),
                       start_time TIMESTAMP(6),
                       end_time TIMESTAMP(6),
                       max_num_guests INTEGER,
                       is_private BOOLEAN,
                       event_type_id BIGINT NOT NULL,
                       location_id BIGINT UNIQUE
);

CREATE TABLE event_type_product_category_link (
                                                  event_type_id BIGINT NOT NULL,
                                                  category_id BIGINT NOT NULL
);

CREATE TABLE product_category (
                                  id BIGSERIAL PRIMARY KEY,
                                  version INTEGER,
                                  status_id BIGINT NOT NULL,
                                  name VARCHAR(255) NOT NULL UNIQUE,
                                  description VARCHAR(255)
);

CREATE TABLE product (
                         id BIGSERIAL PRIMARY KEY,
                         version INTEGER,
                         name VARCHAR(255),
                         description VARCHAR(255),
                         peculiarities VARCHAR(255),
                         likes BIGINT,
                         price DOUBLE PRECISION,
                         discount DOUBLE PRECISION,
                         is_available BOOLEAN,
                         is_deleted BOOLEAN,
                         is_visible BOOLEAN,
                         service_duration_min_minutes INTEGER,
                         service_duration_max_minutes INTEGER,
                         time_management BOOLEAN,
                         booking_confirmation BOOLEAN,
                         booking_decline_deadline_hours INTEGER,
                         category_id BIGINT NOT NULL,
                         pup_id BIGINT NOT NULL
);

CREATE TABLE product_event_type_link (
                                         product_id BIGINT NOT NULL,
                                         event_type_id BIGINT NOT NULL
);

CREATE TABLE product_photo (
                               id BIGSERIAL PRIMARY KEY,
                               version INTEGER,
                               product_id BIGINT,
                               photo_url VARCHAR(255)
);

CREATE TABLE reservation_list (
                                  id BIGSERIAL PRIMARY KEY,
                                  version INTEGER,
                                  count INTEGER,
                                  start_time TIMESTAMP(6),
                                  end_time TIMESTAMP(6),
                                  product_id BIGINT,
                                  budget_item_id BIGINT UNIQUE,
                                  status_id BIGINT UNIQUE
);

CREATE TABLE product_reservation (
                                     product_id BIGINT NOT NULL,
                                     reservation_id BIGINT NOT NULL
);

CREATE TABLE budget_items (
                              id BIGSERIAL PRIMARY KEY,
                              version INTEGER,
                              price DOUBLE PRECISION,
                              event_id BIGINT,
                              reservation_id BIGINT UNIQUE
);

CREATE TABLE agenda_items (
                              id BIGSERIAL PRIMARY KEY,
                              version INTEGER,
                              title VARCHAR(255),
                              description VARCHAR(255),
                              location VARCHAR(255),
                              start_time TIMESTAMP(6),
                              end_time TIMESTAMP(6),
                              event_id BIGINT
);

CREATE TABLE comments (
                          id BIGSERIAL PRIMARY KEY,
                          version INTEGER,
                          description VARCHAR(255),
                          date TIMESTAMP(6),
                          likes BIGINT,
                          user_id BIGINT
);

CREATE TABLE company (
                         id BIGSERIAL PRIMARY KEY,
                         version INTEGER,
                         company_name VARCHAR(255),
                         company_description VARCHAR(255),
                         company_email VARCHAR(255),
                         company_phone_number VARCHAR(255),
                         company_address VARCHAR(255),
                         company_city VARCHAR(255),
                         company_password VARCHAR(255) NOT NULL,
                         registration_date TIMESTAMP(6),
                         photo_id BIGINT UNIQUE,
                         is_active BOOLEAN
);

ALTER TABLE users
    ADD CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES roles(id);

ALTER TABLE user_photo
    ADD CONSTRAINT fk_user_photo_user
        FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE password_reset_tokens
    ADD CONSTRAINT fk_prt_user
        FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE event
    ADD CONSTRAINT fk_event_type
        FOREIGN KEY (event_type_id) REFERENCES event_types(id),
    ADD CONSTRAINT fk_event_location
        FOREIGN KEY (location_id) REFERENCES event_locations(id);

ALTER TABLE event_type_product_category_link
    ADD CONSTRAINT fk_etpcl_event_type
        FOREIGN KEY (event_type_id) REFERENCES event_types(id),
    ADD CONSTRAINT fk_etpcl_category
        FOREIGN KEY (category_id) REFERENCES product_category(id);

ALTER TABLE product
    ADD CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES product_category(id),
    ADD CONSTRAINT fk_product_user
        FOREIGN KEY (pup_id) REFERENCES users(id);

ALTER TABLE product_event_type_link
    ADD CONSTRAINT fk_petl_product
        FOREIGN KEY (product_id) REFERENCES product(id),
    ADD CONSTRAINT fk_petl_event_type
        FOREIGN KEY (event_type_id) REFERENCES event_types(id);

ALTER TABLE product_photo
    ADD CONSTRAINT fk_pphoto_product
        FOREIGN KEY (product_id) REFERENCES product(id);

ALTER TABLE reservation_list
    ADD CONSTRAINT fk_rl_product
        FOREIGN KEY (product_id) REFERENCES product(id),
    ADD CONSTRAINT fk_rl_budget
        FOREIGN KEY (budget_item_id) REFERENCES budget_items(id),
    ADD CONSTRAINT fk_rl_status
        FOREIGN KEY (status_id) REFERENCES status(id);

ALTER TABLE product_reservation
    ADD CONSTRAINT fk_pr_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservation_list(id),
    ADD CONSTRAINT fk_pr_product
        FOREIGN KEY (product_id) REFERENCES product(id);

ALTER TABLE budget_items
    ADD CONSTRAINT fk_bi_event
        FOREIGN KEY (event_id) REFERENCES event(id),
    ADD CONSTRAINT fk_bi_reservation
        FOREIGN KEY (reservation_id) REFERENCES reservation_list(id);

ALTER TABLE agenda_items
    ADD CONSTRAINT fk_ai_event
        FOREIGN KEY (event_id) REFERENCES event(id);

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_user
        FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE company
    ADD CONSTRAINT fk_company_photo
        FOREIGN KEY (photo_id) REFERENCES photos(id);

ALTER TABLE event_types
    ADD CONSTRAINT fk_et_status
        FOREIGN KEY (status_id) REFERENCES status(id);

ALTER TABLE product_category
    ADD CONSTRAINT fk_pc_status
        FOREIGN KEY (status_id) REFERENCES status(id);