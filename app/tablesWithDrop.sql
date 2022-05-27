CREATE TYPE GENDER AS ENUM ('male', 'female');
CREATE TYPE ROLE AS ENUM ('admin', 'user');
CREATE TYPE STATUS AS ENUM ('sent', 'delivered', 'failed', 'unknown');

CREATE TABLE IF NOT EXISTS users (
  uuid UUID PRIMARY KEY,
  name VARCHAR NOT NULL,
  email VARCHAR UNIQUE NOT NULL,
  gender GENDER NOT NULL,
  password VARCHAR NOT NULL,
  role ROLE NOT NULL DEFAULT 'user'
);

INSERT INTO "users" ("uuid", "name", "email", "gender", "password", "role")
VALUES ('c1039d34-425b-4f78-9a7f-893f5b4df478', 'Admin', 'saroyadmin@gmail.com', 'male',
        '$s0$e0801$5JK3Ogs35C2h5htbXQoeEQ==$N7HgNieSnOajn1FuEB7l4PhC6puBSq+e1E8WUaSJcGY=', 'admin');

CREATE TABLE IF NOT EXISTS contacts (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    birthday TIMESTAMP NOT NULL,
    phone VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS sms_templates (
    id UUID PRIMARY KEY,
    text VARCHAR NOT NULL,
    active BOOLEAN NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY,
    contact_id UUID NOT NULL CONSTRAINT fk_contact_id REFERENCES contacts (id) ON UPDATE CASCADE ON DELETE CASCADE,
    sms_temp_id UUID NOT NULL CONSTRAINT fk_sms_temp_id REFERENCES sms_templates (id) ON UPDATE CASCADE ON DELETE CASCADE,
    sent_date TIMESTAMP NOT NULL,
    delivery_status STATUS NOT NULL
);

DROP TABLE users;
DROP TABLE contacts;
DROP TABLE sms_templates;
DROP TABLE messages;
