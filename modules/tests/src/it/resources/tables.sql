CREATE TYPE GENDER AS ENUM ('male', 'female');
CREATE TYPE ROLE AS ENUM ('admin', 'user');
CREATE TYPE DELIVERY_STATUS AS ENUM ('sent', 'delivered', 'failed', 'unknown');

CREATE TYPE GENDER_ACCESS AS ENUM ('all', 'male', 'female');

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

CREATE TABLE IF NOT EXISTS users
(
    uuid     UUID PRIMARY KEY,
    name     VARCHAR        NOT NULL,
    email    VARCHAR UNIQUE NOT NULL,
    gender   GENDER         NOT NULL,
    password VARCHAR        NOT NULL,
    role     ROLE           NOT NULL DEFAULT 'user'
);

INSERT INTO "users" ("uuid", "name", "email", "gender", "password", "role")
VALUES ('c1039d34-425b-4f78-9a7f-893f5b4df478', 'Admin', 'saroyadmin@gmail.com', 'male',
        '$s0$e0801$5JK3Ogs35C2h5htbXQoeEQ==$N7HgNieSnOajn1FuEB7l4PhC6puBSq+e1E8WUaSJcGY=', 'admin');

CREATE TABLE IF NOT EXISTS contacts
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    gender GENDER NOT NULL,
    birthday DATE NOT NULL,
    phone VARCHAR NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false
);

DELIVERY_STATUS

CREATE TABLE IF NOT EXISTS holidays
(
    id      UUID PRIMARY KEY,
    name    VARCHAR NOT NULL,
    day     INT     NOT NULL,
    month   MONTH   NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS template_categories
(
    id      UUID PRIMARY KEY,
    name    VARCHAR NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false
);

INSERT INTO "template_categories" ("id", "name")
VALUES ('c1039d34-425b-4f78-9a7f-893f5b4ac478', 'dog''ilgan gun');

CREATE TABLE IF NOT EXISTS sms_templates
(
    id                   UUID PRIMARY KEY,
    template_category_id UUID          NOT NULL
        CONSTRAINT fk_template_category_id REFERENCES template_categories (id) ON UPDATE CASCADE ON DELETE CASCADE,
    title                VARCHAR       NOT NULL,
    text                 VARCHAR       NOT NULL,
    gender_access        GENDER_ACCESS NOT NULL,
    active               BOOLEAN       NOT NULL,
    deleted              BOOLEAN       NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS messages
(
    id              UUID PRIMARY KEY,
    created_at      TIMESTAMP NOT NULL,
    contact_id      UUID      NOT NULL
        CONSTRAINT fk_contact_id REFERENCES contacts (id) ON UPDATE CASCADE ON DELETE CASCADE,
    sms_temp_id     UUID      NOT NULL
        CONSTRAINT fk_sms_temp_id REFERENCES sms_templates (id) ON UPDATE CASCADE ON DELETE CASCADE,
    sent_date       TIMESTAMP NOT NULL,
    delivery_status DELIVERY_STATUS    NOT NULL
);
