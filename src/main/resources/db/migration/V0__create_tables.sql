CREATE TABLE roles (
                       id      BIGSERIAL PRIMARY KEY,
                       version INTEGER,
                       name    VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE status (
                        id          BIGSERIAL PRIMARY KEY,
                        version     INTEGER,
                        name        VARCHAR(255),
                        description VARCHAR(255)
);

CREATE TABLE photos (
                        id      BIGSERIAL PRIMARY KEY,
                        version INTEGER
);

CREATE TABLE users (
                       id                BIGSERIAL PRIMARY KEY,
                       version           INTEGER,
                       is_active         BOOLEAN   DEFAULT FALSE,
                       is_deleted        BOOLEAN,
                       registration_date TIMESTAMP(6),
                       role_id           BIGINT    NOT NULL,
                       address           VARCHAR(255),
                       city              VARCHAR(255),
                       company_description VARCHAR(255),
                       country           VARCHAR(255),
                       email             VARCHAR(255),
                       first_name        VARCHAR(255),
                       last_name         VARCHAR(255),
                       password_hash     VARCHAR(255),
                       phone_number      VARCHAR(255),
                       zip_code          VARCHAR(255)
);

CREATE TABLE user_photo (
                            id      BIGSERIAL PRIMARY KEY,
                            version INTEGER,
                            user_id BIGINT    NOT NULL,
                            url     VARCHAR(255)
);

CREATE TABLE password_reset_tokens (
                                       id         BIGSERIAL PRIMARY KEY,
                                       version    INTEGER,
                                       used       BOOLEAN,
                                       expiry_at  TIMESTAMP(6) WITH TIME ZONE,
                                       user_id    BIGINT    NOT NULL,
                                       token_hash VARCHAR(255)
);

CREATE TABLE event_types (
                             id         BIGSERIAL PRIMARY KEY,
                             version    INTEGER,
                             status_id  BIGINT    NOT NULL,
                             name       VARCHAR(255),
                             description VARCHAR(255)
);

CREATE TABLE event_locations (
                                 id      BIGSERIAL PRIMARY KEY,
                                 version INTEGER
);

CREATE TABLE event (
                       id               BIGSERIAL PRIMARY KEY,
                       version          INTEGER,
                       name             VARCHAR(255),
                       description      VARCHAR(255),
                       start_time       TIMESTAMP(6),
                       end_time         TIMESTAMP(6),
                       max_num_guests   INTEGER,
                       is_private       BOOLEAN,
                       event_type_id    BIGINT    NOT NULL,
                       location_id      BIGINT
);

CREATE TABLE product_category (
                                  id          BIGSERIAL PRIMARY KEY,
                                  version     INTEGER,
                                  status_id   BIGINT    NOT NULL,
                                  name        VARCHAR(255) NOT NULL UNIQUE,
                                  description VARCHAR(255)
);

CREATE TABLE service_category (
                                  id          BIGSERIAL PRIMARY KEY,
                                  version     INTEGER,
                                  status_id   BIGINT    NOT NULL,
                                  name        VARCHAR(255) NOT NULL UNIQUE,
                                  description VARCHAR(255)
);

CREATE TABLE event_type_product_category_link (
                                                  event_type_id BIGINT NOT NULL,
                                                  category_id   BIGINT NOT NULL
);

CREATE TABLE event_type_service_category_link (
                                                  event_type_id BIGINT NOT NULL,
                                                  category_id   BIGINT NOT NULL
);

CREATE TABLE product (
                         id                             BIGSERIAL PRIMARY KEY,
                         version                        INTEGER,
                         name                           VARCHAR(255),
                         description                    VARCHAR(255),
                         peculiarities                  VARCHAR(255),
                         likes                          BIGINT,
                         price                          DOUBLE PRECISION,
                         discount                       DOUBLE PRECISION,
                         is_available                   BOOLEAN,
                         is_deleted                     BOOLEAN,
                         is_visible                     BOOLEAN,
                         service_duration_min_minutes   INTEGER,
                         service_duration_max_minutes   INTEGER,
                         time_management                BOOLEAN,
                         booking_confirmation           BOOLEAN,
                         booking_decline_deadline_hours INTEGER,
                         category_id                    BIGINT    NOT NULL,
                         pup_id                         BIGINT    NOT NULL
);

CREATE TABLE service (
                         id            BIGSERIAL PRIMARY KEY,
                         version       INTEGER,
                         name          VARCHAR(255),
                         peculiarities VARCHAR(255),
                         price         DOUBLE PRECISION,
                         discount      DOUBLE PRECISION,
                         is_visible    BOOLEAN,
                         category_id   BIGINT    NOT NULL,
                         pup_id        BIGINT    NOT NULL
);

CREATE TABLE product_event_type_link (
                                         product_id    BIGINT NOT NULL,
                                         event_type_id BIGINT NOT NULL
);

CREATE TABLE service_event_types (
                                     service_id    BIGINT NOT NULL,
                                     event_type_id BIGINT NOT NULL
);

CREATE TABLE item_photo (
                            id         BIGSERIAL PRIMARY KEY,
                            version    INTEGER,
                            photo_url  VARCHAR(255) NOT NULL,
                            service_id BIGINT,
                            product_id BIGINT
);

CREATE TABLE budget_items (
                              id             BIGSERIAL PRIMARY KEY,
                              version        INTEGER,
                              price          DOUBLE PRECISION,
                              event_id       BIGINT    NOT NULL,
                              reservation_id BIGINT
);

CREATE TABLE reservation_list (
                                  id              BIGSERIAL PRIMARY KEY,
                                  version         INTEGER,
                                  count           INTEGER,
                                  start_time      TIMESTAMP(6),
                                  end_time        TIMESTAMP(6),
                                  product_id      BIGINT    NOT NULL,
                                  budget_item_id  BIGINT,
                                  status_id       BIGINT
);

CREATE TABLE product_reservation (
                                     product_id     BIGINT NOT NULL,
                                     reservation_id BIGINT NOT NULL
);

CREATE TABLE agenda_items (
                              id          BIGSERIAL PRIMARY KEY,
                              version     INTEGER,
                              title       VARCHAR(255),
                              description VARCHAR(255),
                              location    VARCHAR(255),
                              start_time  TIMESTAMP(6),
                              end_time    TIMESTAMP(6),
                              event_id    BIGINT    NOT NULL
);

CREATE TABLE comments (
                          id          BIGSERIAL PRIMARY KEY,
                          version     INTEGER,
                          description VARCHAR(255),
                          date        TIMESTAMP(6),
                          likes       BIGINT,
                          user_id     BIGINT    NOT NULL
);

CREATE TABLE company (
                         id                   BIGSERIAL PRIMARY KEY,
                         version              INTEGER,
                         company_name         VARCHAR(255),
                         company_description  VARCHAR(255),
                         company_email        VARCHAR(255),
                         company_phone_number VARCHAR(255),
                         company_address      VARCHAR(255),
                         company_city         VARCHAR(255),
                         company_password     VARCHAR(255) NOT NULL,
                         registration_date    TIMESTAMP(6),
                         photo_id             BIGINT,
                         is_active            BOOLEAN
);

BEGIN;

-- users → roles
ALTER TABLE users
    ADD CONSTRAINT fk_users_role
        FOREIGN KEY(role_id) REFERENCES roles(id);

-- user_photo → users
ALTER TABLE user_photo
    ADD CONSTRAINT fk_user_photo_user
        FOREIGN KEY(user_id) REFERENCES users(id);

-- password_reset_tokens → users
ALTER TABLE password_reset_tokens
    ADD CONSTRAINT fk_prt_user
        FOREIGN KEY(user_id) REFERENCES users(id);

-- event_types → status
ALTER TABLE event_types
    ADD CONSTRAINT fk_event_types_status
        FOREIGN KEY(status_id) REFERENCES status(id);

-- event → event_types, event_locations
ALTER TABLE event
    ADD CONSTRAINT fk_event_event_type
        FOREIGN KEY(event_type_id) REFERENCES event_types(id),
    ADD CONSTRAINT fk_event_location
        FOREIGN KEY(location_id) REFERENCES event_locations(id),
    ADD CONSTRAINT uq_event_location UNIQUE(location_id);

-- product_category → status
ALTER TABLE product_category
    ADD CONSTRAINT fk_product_category_status
        FOREIGN KEY(status_id) REFERENCES status(id);

-- service_category → status
ALTER TABLE service_category
    ADD CONSTRAINT fk_service_category_status
        FOREIGN KEY(status_id) REFERENCES status(id);

-- link tables
ALTER TABLE event_type_product_category_link
    ADD CONSTRAINT fk_et_pcat_et FOREIGN KEY(event_type_id) REFERENCES event_types(id),
    ADD CONSTRAINT fk_et_pcat_cat FOREIGN KEY(category_id)    REFERENCES product_category(id);

ALTER TABLE event_type_service_category_link
    ADD CONSTRAINT fk_et_scat_et FOREIGN KEY(event_type_id) REFERENCES event_types(id),
    ADD CONSTRAINT fk_et_scat_cat FOREIGN KEY(category_id)    REFERENCES service_category(id);

-- product → product_category, users
ALTER TABLE product
    ADD CONSTRAINT fk_product_category FOREIGN KEY(category_id) REFERENCES product_category(id),
    ADD CONSTRAINT fk_product_pup      FOREIGN KEY(pup_id)      REFERENCES users(id);

-- service → service_category, users
ALTER TABLE service
    ADD CONSTRAINT fk_service_category FOREIGN KEY(category_id) REFERENCES service_category(id),
    ADD CONSTRAINT fk_service_pup      FOREIGN KEY(pup_id)      REFERENCES users(id);

-- product_event_type_link
ALTER TABLE product_event_type_link
    ADD CONSTRAINT fk_petl_prod FOREIGN KEY(product_id)    REFERENCES product(id),
    ADD CONSTRAINT fk_petl_et   FOREIGN KEY(event_type_id) REFERENCES event_types(id);

-- service_event_types
ALTER TABLE service_event_types
    ADD CONSTRAINT fk_set_serv FOREIGN KEY(service_id)    REFERENCES service(id),
    ADD CONSTRAINT fk_set_et   FOREIGN KEY(event_type_id) REFERENCES event_types(id);

-- item_photo → service, product + CHECK one-of-two
ALTER TABLE item_photo
    ADD CONSTRAINT fk_item_photo_service FOREIGN KEY(service_id) REFERENCES service(id),
    ADD CONSTRAINT fk_item_photo_product FOREIGN KEY(product_id) REFERENCES product(id),
    ADD CONSTRAINT chk_item_photo_one_fk
        CHECK (
            (service_id IS NOT NULL AND product_id IS NULL)
                OR
            (service_id IS NULL AND product_id IS NOT NULL)
            );

-- budget_items → event, reservation_list
ALTER TABLE budget_items
    ADD CONSTRAINT fk_budget_items_event      FOREIGN KEY(event_id)       REFERENCES event(id),
    ADD CONSTRAINT fk_budget_items_reservation FOREIGN KEY(reservation_id) REFERENCES reservation_list(id);

-- reservation_list → product, budget_items, status
ALTER TABLE reservation_list
    ADD CONSTRAINT fk_reslist_product      FOREIGN KEY(product_id)     REFERENCES product(id),
    ADD CONSTRAINT fk_reslist_budget_item FOREIGN KEY(budget_item_id) REFERENCES budget_items(id),
    ADD CONSTRAINT fk_reslist_status      FOREIGN KEY(status_id)      REFERENCES status(id);

-- product_reservation → product, reservation_list
ALTER TABLE product_reservation
    ADD CONSTRAINT fk_pr_prod FOREIGN KEY(product_id)     REFERENCES product(id),
    ADD CONSTRAINT fk_pr_res  FOREIGN KEY(reservation_id) REFERENCES reservation_list(id);

-- agenda_items → event
ALTER TABLE agenda_items
    ADD CONSTRAINT fk_agenda_event FOREIGN KEY(event_id) REFERENCES event(id);

-- comments → users
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_user FOREIGN KEY(user_id) REFERENCES users(id);

-- company → photos
ALTER TABLE company
    ADD CONSTRAINT fk_company_photo FOREIGN KEY(photo_id) REFERENCES photos(id);

COMMIT;