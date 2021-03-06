CREATE TYPE GENDER AS ENUM ('all', 'male', 'female');
CREATE TYPE ROLE AS ENUM ('admin', 'user');
CREATE TYPE DELIVERY_STATUS AS ENUM ('sent', 'delivered', 'failed', 'unknown');

CREATE TYPE MONTH AS ENUM (
    'january',
    'february',
    'march',
    'april',
    'may',
    'june',
    'july',
    'august',
    'september',
    'october',
    'november',
    'december'
    );

CREATE TABLE IF NOT EXISTS users(
    uuid     UUID PRIMARY KEY,
    name     VARCHAR        NOT NULL,
    email    VARCHAR UNIQUE NOT NULL,
    gender   GENDER         NOT NULL,
    password VARCHAR        NOT NULL,
    role     ROLE           NOT NULL DEFAULT 'user'
);

INSERT INTO "users" ("uuid", "name", "email", "gender", "password", "role")
VALUES ('c1039d34-425b-4f78-9a7f-893f5b4df478', 'Admin', 'admin@gmail.com', 'male',
        '$s0$e0801$5JK3Ogs35C2h5htbXQoeEQ==$N7HgNieSnOajn1FuEB7l4PhC6puBSq+e1E8WUaSJcGY=', 'admin');

INSERT INTO "users" ("uuid", "name", "email", "gender", "password", "role")
VALUES ('4b590039-892c-4bbf-bd6f-a12f102f3582', 'User', 'user@gmail.com', 'male',
        '$s0$e0801$5JK3Ogs35C2h5htbXQoeEQ==$N7HgNieSnOajn1FuEB7l4PhC6puBSq+e1E8WUaSJcGY=', 'user');

CREATE TABLE IF NOT EXISTS contacts(
    id         UUID PRIMARY KEY,
    user_id    UUID      NOT NULL
        CONSTRAINT fk_user_id REFERENCES users (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL,
    first_name VARCHAR   NOT NULL,
    last_name  VARCHAR   NOT NULL,
    gender     GENDER    NOT NULL,
    birthday   DATE      NOT NULL,
    phone      VARCHAR   NOT NULL,
    deleted    BOOLEAN   NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS template_categories(
    id      UUID PRIMARY KEY,
    user_id UUID    NOT NULL
        CONSTRAINT fk_user_id REFERENCES users (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
    name    VARCHAR NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS sms_templates(
    id                   UUID    PRIMARY KEY,
    user_id              UUID    NOT NULL
        CONSTRAINT fk_user_id REFERENCES users (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
    template_category_id UUID    NOT NULL
        CONSTRAINT fk_template_category_id REFERENCES template_categories (id) ON UPDATE CASCADE ON DELETE CASCADE,
    title                VARCHAR NOT NULL,
    text                 VARCHAR NOT NULL,
    gender_access        GENDER  NOT NULL,
    deleted              BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS holidays(
    id           UUID    PRIMARY KEY,
    user_id      UUID    NOT NULL
        CONSTRAINT fk_user_id REFERENCES users (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
    name         VARCHAR NOT NULL,
    day          INT     NOT NULL,
    month        MONTH   NOT NULL,
    sms_women_id UUID    NULL
        CONSTRAINT fk_sms_template_w_id REFERENCES sms_templates (id) ON UPDATE CASCADE ON DELETE CASCADE,
    sms_men_id   UUID    NULL
        CONSTRAINT fk_sms_template_m_id REFERENCES sms_templates (id) ON UPDATE CASCADE ON DELETE CASCADE,
    deleted      BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS messages(
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL
        CONSTRAINT fk_user_id REFERENCES users (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
    contact_id      UUID            NOT NULL
        CONSTRAINT fk_contact_id REFERENCES contacts (id) ON UPDATE CASCADE ON DELETE CASCADE,
    sms_temp_id     UUID            NOT NULL
        CONSTRAINT fk_sms_temp_id REFERENCES sms_templates (id) ON UPDATE CASCADE ON DELETE CASCADE,
    sent_date       TIMESTAMP       NOT NULL,
    delivery_status DELIVERY_STATUS NOT NULL
);

CREATE TABLE IF NOT EXISTS system_settings(
    user_id         UUID NOT NULL
        CONSTRAINT fk_user_id REFERENCES users (uuid) ON UPDATE CASCADE ON DELETE CASCADE,
    auto_send_b  BOOLEAN NOT NULL DEFAULT false,
    auto_send_h  BOOLEAN NOT NULL DEFAULT false,
    sms_men_id   UUID    NULL
        CONSTRAINT fk_sms_template_m_id REFERENCES sms_templates (id) ON UPDATE CASCADE ON DELETE CASCADE,
    sms_women_id UUID    NULL
        CONSTRAINT fk_sms_template_w_id REFERENCES sms_templates (id) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO "system_settings" ("user_id", "auto_send_b", "auto_send_h", "sms_men_id", "sms_women_id")
VALUES ('c1039d34-425b-4f78-9a7f-893f5b4df478', false, false, null, null);

INSERT INTO "system_settings" ("user_id", "auto_send_b", "auto_send_h", "sms_men_id", "sms_women_id")
VALUES ('4b590039-892c-4bbf-bd6f-a12f102f3582', false, false, null, null);
