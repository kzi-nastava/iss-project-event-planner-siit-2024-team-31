CREATE TABLE roles (
                       id         BIGSERIAL PRIMARY KEY,
                       version    INTEGER,
                       created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                       name       VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE status (
                        id          BIGSERIAL PRIMARY KEY,
                        version     INTEGER,
                        created_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                        name        VARCHAR(255),
                        description VARCHAR(255)
);

CREATE TABLE photos (
                        id         BIGSERIAL PRIMARY KEY,
                        version    INTEGER,
                        created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE users (
                       id                   BIGSERIAL PRIMARY KEY,
                       version              INTEGER,
                       created_at           TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                       is_active            BOOLEAN   DEFAULT FALSE,
                       is_deleted           BOOLEAN,
                       registration_date    TIMESTAMP(6),
                       role_id              BIGINT    NOT NULL,
                       address              VARCHAR(255),
                       city                 VARCHAR(255),
                       company_description  VARCHAR(255),
                       country              VARCHAR(255),
                       email                VARCHAR(255),
                       first_name           VARCHAR(255),
                       last_name            VARCHAR(255),
                       password_hash        VARCHAR(255),
                       phone_number         VARCHAR(255),
                       zip_code             VARCHAR(255),
                       birth_date           DATE,
                       gender               VARCHAR(50)
);

CREATE TABLE user_photo (
                            id         BIGSERIAL PRIMARY KEY,
                            version    INTEGER,
                            created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                            user_id    BIGINT    NOT NULL,
                            url        VARCHAR(255)
);

CREATE TABLE password_reset_tokens (
                                       id         BIGSERIAL PRIMARY KEY,
                                       version    INTEGER,
                                       created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                                       used       BOOLEAN,
                                       expiry_at  TIMESTAMP(6) WITH TIME ZONE,
                                       user_id    BIGINT    NOT NULL,
                                       token_hash VARCHAR(255)
);

CREATE TABLE event_types (
                             id          BIGSERIAL PRIMARY KEY,
                             version     INTEGER,
                             created_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                             status_id   BIGINT    NOT NULL,
                             name        VARCHAR(255),
                             description VARCHAR(255)
);

CREATE TABLE event_locations (
                                 id         BIGSERIAL PRIMARY KEY,
                                 version    INTEGER,
                                 created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                                 lat        DOUBLE PRECISION,
                                 lng        DOUBLE PRECISION,
                                 address    VARCHAR(255)
);

CREATE TABLE event (
                       id                     BIGSERIAL PRIMARY KEY,
                       version                INTEGER,
                       created_at             TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                       name                   VARCHAR(255),
                       description            VARCHAR(255),
                       start_time             TIMESTAMP(6) WITH TIME ZONE,
                       end_time               TIMESTAMP(6) WITH TIME ZONE,
                       max_num_guests         INTEGER,
                       is_private             BOOLEAN,
                       event_type_id          BIGINT    NOT NULL,
                       location_id            BIGINT,
                       organizer_id           BIGINT    NOT NULL,
                       status_id              BIGINT    NOT NULL,
                       likes_count            BIGINT    DEFAULT 0,
                       rating                 DOUBLE PRECISION DEFAULT 0.0,
                       registration_deadline  TIMESTAMP(6) WITH TIME ZONE
);

CREATE TABLE product_category (
                                  id          BIGSERIAL PRIMARY KEY,
                                  version     INTEGER,
                                  created_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                                  status_id   BIGINT    NOT NULL,
                                  name        VARCHAR(255) NOT NULL UNIQUE,
                                  description VARCHAR(255)
);

CREATE TABLE service_category (
                                  id          BIGSERIAL PRIMARY KEY,
                                  version     INTEGER,
                                  created_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                                  status_id   BIGINT    NOT NULL,
                                  name        VARCHAR(255) NOT NULL UNIQUE,
                                  description VARCHAR(255)
);

CREATE TABLE product (
                         id           BIGSERIAL PRIMARY KEY,
                         version      INTEGER,
                         created_at   TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                         pup_id       BIGINT    NOT NULL,
                         name         VARCHAR(255),
                         category_id  BIGINT    NOT NULL,
                         description  VARCHAR(255),
                         peculiarities VARCHAR(255),
                         price        DOUBLE PRECISION,
                         discount     DOUBLE PRECISION,
                         is_visible   BOOLEAN,
                         is_available BOOLEAN,
                         is_deleted   BOOLEAN,
                         rating       DOUBLE PRECISION,
                         status_id    BIGINT NOT NULL DEFAULT 1
);

CREATE TABLE service (
                         id                             BIGSERIAL PRIMARY KEY,
                         version                        INTEGER,
                         created_at                     TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                         pup_id                         BIGINT    NOT NULL,
                         category_id                    BIGINT    NOT NULL,
                         name                           VARCHAR(255),
                         description                    VARCHAR(255),
                         peculiarities                  VARCHAR(255),
                         price_per_hour                 DOUBLE PRECISION,
                         discount                       DOUBLE PRECISION,
                         is_visible                     BOOLEAN,
                         is_available                   BOOLEAN,
                         is_deleted                     BOOLEAN,
                         time_management                BOOLEAN,
                         service_duration_min_minutes   INTEGER,
                         service_duration_max_minutes   INTEGER,
                         booking_confirmation           BOOLEAN,
                         booking_decline_deadline_hours INTEGER,
                         rating                         DOUBLE PRECISION,
                         status_id                      BIGINT NOT NULL DEFAULT 1
);

CREATE TABLE item_photo (
                            id         BIGSERIAL PRIMARY KEY,
                            version    INTEGER,
                            created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                            photo_url  VARCHAR(255) NOT NULL,
                            service_id BIGINT,
                            product_id BIGINT,
                            CONSTRAINT chk_ip_one_fk CHECK (
                                (service_id IS NOT NULL AND product_id IS NULL)
                                    OR (service_id IS NULL     AND product_id IS NOT NULL)
                                )
);

CREATE TABLE budget_items (
                              id                   BIGSERIAL PRIMARY KEY,
                              version              INTEGER,
                              created_at           TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                              event_id             BIGINT    NOT NULL,
                              status_id            BIGINT    NOT NULL,
                              service_id           BIGINT,
                              product_id           BIGINT,
                              product_count        INTEGER   NOT NULL DEFAULT 1,
                              service_start_time   TIMESTAMP(6) WITH TIME ZONE,
                              service_end_time     TIMESTAMP(6) WITH TIME ZONE,
                              total_price          DOUBLE PRECISION NOT NULL,
                              CONSTRAINT chk_bi_one_fk CHECK (
                                  (service_id IS NOT NULL AND product_id IS NULL)
                                      OR (service_id IS NULL     AND product_id IS NOT NULL)
                                  ),
                              CONSTRAINT chk_bi_product_count CHECK (
                                  (product_id IS NOT NULL AND product_count  >  0)
                                      OR (product_id IS NULL     AND product_count  =  0)
                                  ),
                              CONSTRAINT chk_bi_service_time CHECK (
                                  (service_id IS NOT NULL
                                      AND service_start_time IS NOT NULL
                                      AND service_end_time   IS NOT NULL
                                      AND service_end_time > service_start_time)
                                      OR (service_id IS NULL
                                      AND service_start_time IS NULL
                                      AND service_end_time   IS NULL)
                                  )
);

CREATE TABLE reservation_list (
                                  id              BIGSERIAL PRIMARY KEY,
                                  version         INTEGER,
                                  created_at      TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                                  count           INTEGER,
                                  start_time      TIMESTAMP(6) WITH TIME ZONE,
                                  end_time        TIMESTAMP(6) WITH TIME ZONE,
                                  service_id      BIGINT,
                                  budget_item_id  BIGINT,
                                  status_id       BIGINT
);

CREATE TABLE agenda_items (
                              id          BIGSERIAL PRIMARY KEY,
                              version     INTEGER,
                              created_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                              title       VARCHAR(255),
                              description VARCHAR(255),
                              location    VARCHAR(255),
                              start_time  TIMESTAMP(6) WITH TIME ZONE,
                              end_time    TIMESTAMP(6) WITH TIME ZONE,
                              event_id    BIGINT    NOT NULL
);

CREATE TABLE comments (
                          id          BIGSERIAL PRIMARY KEY,
                          version     INTEGER,
                          created_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                          description VARCHAR(255),
                          date        TIMESTAMP(6),
                          likes       BIGINT,
                          user_id     BIGINT    NOT NULL
);

CREATE TABLE company (
                         id                   BIGSERIAL PRIMARY KEY,
                         version              INTEGER,
                         created_at           TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
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

CREATE TABLE event_photos (
                              id         BIGSERIAL PRIMARY KEY,
                              version    INTEGER,
                              created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                              event_id   BIGINT    NOT NULL,
                              url        VARCHAR(255) NOT NULL
);

CREATE TABLE invite_list (
                             id         BIGSERIAL PRIMARY KEY,
                             version    INTEGER,
                             created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                             user_id    BIGINT    NOT NULL,
                             event_id   BIGINT    NOT NULL,
                             status_id  BIGINT    NOT NULL,
                             sent_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- статистика
CREATE TABLE event_registrations (
                                     id                  BIGSERIAL PRIMARY KEY,
                                     version             INTEGER,
                                     created_at          TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                                     event_id            BIGINT    NOT NULL,
                                     user_id             BIGINT    NOT NULL,
                                     registration_date   TIMESTAMP(6) WITH TIME ZONE,
                                     status_id           BIGINT,
                                     cancellation_reason VARCHAR(255),
                                     age_group           VARCHAR(50),
                                     location            VARCHAR(255)
);

CREATE TABLE event_attendances (
                                   id                          BIGSERIAL PRIMARY KEY,
                                   version                     INTEGER,
                                   created_at                  TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                                   event_id                    BIGINT    NOT NULL,
                                   user_id                     BIGINT    NOT NULL,
                                   check_in_time               TIMESTAMP(6) WITH TIME ZONE,
                                   check_out_time              TIMESTAMP(6) WITH TIME ZONE,
                                   status_id                   BIGINT,
                                   attendance_duration_minutes INTEGER
);

CREATE TABLE event_ratings (
                               id            BIGSERIAL PRIMARY KEY,
                               version       INTEGER,
                               created_at    TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now(),
                               event_id      BIGINT    NOT NULL,
                               user_id       BIGINT    NOT NULL,
                               rating        INTEGER,
                               comment       VARCHAR(255),
                               rating_date   TIMESTAMP(6) WITH TIME ZONE,
                               is_anonymous  BOOLEAN   DEFAULT FALSE
);

-- join- и link-таблицы (без created_at)
CREATE TABLE product_event_type_link (
                                         product_id    BIGINT NOT NULL,
                                         event_type_id BIGINT NOT NULL
);

CREATE TABLE service_event_type_link (
                                         service_id    BIGINT NOT NULL,
                                         event_type_id BIGINT NOT NULL
);

CREATE TABLE event_type_product_category_link (
                                                  event_type_id BIGINT NOT NULL,
                                                  category_id   BIGINT NOT NULL
);

CREATE TABLE event_type_service_category_link (
                                                  event_type_id BIGINT NOT NULL,
                                                  category_id   BIGINT NOT NULL
);

CREATE TABLE product_reservation (
                                     product_id     BIGINT NOT NULL,
                                     reservation_id BIGINT NOT NULL
);

CREATE TABLE service_reservation (
                                     service_id     BIGINT NOT NULL,
                                     reservation_id BIGINT NOT NULL
);

CREATE TABLE user_favorite_service (
                                       id         BIGSERIAL PRIMARY KEY,
                                       user_id    BIGINT    NOT NULL,
                                       service_id BIGINT    NOT NULL
);

CREATE TABLE user_favorite_product (
                                       id         BIGSERIAL PRIMARY KEY,
                                       user_id    BIGINT    NOT NULL,
                                       product_id BIGINT    NOT NULL
);

CREATE TABLE user_favorite_event (
                                     id        BIGSERIAL PRIMARY KEY,
                                     user_id   BIGINT    NOT NULL,
                                     event_id  BIGINT    NOT NULL
);

BEGIN;

ALTER TABLE users            ADD CONSTRAINT fk_users_role           FOREIGN KEY(role_id)       REFERENCES roles(id);
ALTER TABLE user_photo       ADD CONSTRAINT fk_uphoto_user          FOREIGN KEY(user_id)       REFERENCES users(id);
ALTER TABLE password_reset_tokens
    ADD CONSTRAINT fk_prt_user            FOREIGN KEY(user_id)       REFERENCES users(id);

ALTER TABLE event_types      ADD CONSTRAINT fk_etypes_status        FOREIGN KEY(status_id)     REFERENCES status(id);
ALTER TABLE event            ADD CONSTRAINT fk_event_type           FOREIGN KEY(event_type_id) REFERENCES event_types(id),
                             ADD CONSTRAINT fk_event_location       FOREIGN KEY(location_id)    REFERENCES event_locations(id),
                             ADD CONSTRAINT fk_event_organizer      FOREIGN KEY(organizer_id)   REFERENCES users(id),
                             ADD CONSTRAINT fk_event_status         FOREIGN KEY(status_id)      REFERENCES status(id);

ALTER TABLE product_category ADD CONSTRAINT fk_pcat_status          FOREIGN KEY(status_id)     REFERENCES status(id);
ALTER TABLE service_category ADD CONSTRAINT fk_scat_status          FOREIGN KEY(status_id)     REFERENCES status(id);

ALTER TABLE product          ADD CONSTRAINT fk_product_pup         FOREIGN KEY(pup_id)        REFERENCES users(id),
                             ADD CONSTRAINT fk_product_category    FOREIGN KEY(category_id)   REFERENCES product_category(id),
                             ADD CONSTRAINT fk_product_status      FOREIGN KEY(status_id)     REFERENCES status(id);

ALTER TABLE service          ADD CONSTRAINT fk_service_pup         FOREIGN KEY(pup_id)        REFERENCES users(id),
                             ADD CONSTRAINT fk_service_category    FOREIGN KEY(category_id)   REFERENCES service_category(id),
                             ADD CONSTRAINT fk_service_status      FOREIGN KEY(status_id)     REFERENCES status(id);

ALTER TABLE item_photo       ADD CONSTRAINT fk_ip_service          FOREIGN KEY(service_id)    REFERENCES service(id),
                             ADD CONSTRAINT fk_ip_product          FOREIGN KEY(product_id)    REFERENCES product(id);

ALTER TABLE budget_items     ADD CONSTRAINT fk_bi_event           FOREIGN KEY(event_id)      REFERENCES event(id),
                             ADD CONSTRAINT fk_bi_status          FOREIGN KEY(status_id)     REFERENCES status(id),
                             ADD CONSTRAINT fk_bi_service         FOREIGN KEY(service_id)    REFERENCES service(id),
                             ADD CONSTRAINT fk_bi_product         FOREIGN KEY(product_id)    REFERENCES product(id);

ALTER TABLE reservation_list ADD CONSTRAINT fk_rl_service          FOREIGN KEY(service_id)    REFERENCES service(id),
                             ADD CONSTRAINT fk_rl_budget_item     FOREIGN KEY(budget_item_id)REFERENCES budget_items(id),
                             ADD CONSTRAINT fk_rl_status          FOREIGN KEY(status_id)     REFERENCES status(id);

ALTER TABLE product_reservation
    ADD CONSTRAINT fk_pr_prod            FOREIGN KEY(product_id)    REFERENCES product(id),
    ADD CONSTRAINT fk_pr_res             FOREIGN KEY(reservation_id)REFERENCES reservation_list(id);

ALTER TABLE service_reservation
    ADD CONSTRAINT fk_sr_serv            FOREIGN KEY(service_id)    REFERENCES service(id),
    ADD CONSTRAINT fk_sr_res             FOREIGN KEY(reservation_id)REFERENCES reservation_list(id);

ALTER TABLE agenda_items      ADD CONSTRAINT fk_ag_event           FOREIGN KEY(event_id)      REFERENCES event(id);
ALTER TABLE comments          ADD CONSTRAINT fk_comm_user          FOREIGN KEY(user_id)       REFERENCES users(id);
ALTER TABLE company           ADD CONSTRAINT fk_comp_photo         FOREIGN KEY(photo_id)      REFERENCES photos(id);

ALTER TABLE event_photos      ADD CONSTRAINT fk_event_photos_event FOREIGN KEY(event_id)      REFERENCES event(id);
ALTER TABLE invite_list       ADD CONSTRAINT fk_invite_user        FOREIGN KEY(user_id)       REFERENCES users(id),
                              ADD CONSTRAINT fk_invite_event       FOREIGN KEY(event_id)      REFERENCES event(id),
                              ADD CONSTRAINT fk_invite_status      FOREIGN KEY(status_id)     REFERENCES status(id);

ALTER TABLE event_registrations
    ADD CONSTRAINT fk_er_event           FOREIGN KEY(event_id)      REFERENCES event(id),
    ADD CONSTRAINT fk_er_user            FOREIGN KEY(user_id)       REFERENCES users(id),
    ADD CONSTRAINT fk_er_status          FOREIGN KEY(status_id)     REFERENCES status(id);

ALTER TABLE event_attendances
    ADD CONSTRAINT fk_ea_event           FOREIGN KEY(event_id)      REFERENCES event(id),
    ADD CONSTRAINT fk_ea_user            FOREIGN KEY(user_id)       REFERENCES users(id),
    ADD CONSTRAINT fk_ea_status          FOREIGN KEY(status_id)     REFERENCES status(id);

ALTER TABLE event_ratings     ADD CONSTRAINT fk_rt_event           FOREIGN KEY(event_id)      REFERENCES event(id),
                              ADD CONSTRAINT fk_rt_user            FOREIGN KEY(user_id)       REFERENCES users(id);

COMMIT;