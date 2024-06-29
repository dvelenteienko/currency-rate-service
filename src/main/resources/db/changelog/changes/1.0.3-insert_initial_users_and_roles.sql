--liquibase formatted sql

--changeset dvelenteienko:1

INSERT INTO role (id, name)
VALUES ('3f6482bd-f61d-4561-b5f4-8ff69f691402', 'ADMIN');

-- Insert some sample data into User table
INSERT INTO user_account (id, username, password)
-- init pwd admin
VALUES ('1b531074-5a6d-409d-b166-e007ceac301a', 'admin', '$2y$12$hNu71GcqkqjpvigNjrPK0uaqtWvn2iARA3YjgXiJHgqGJJSpgJkNa');

-- Assign roles to users
INSERT INTO users_roles (user_id, role_id)
VALUES ('1b531074-5a6d-409d-b166-e007ceac301a', '3f6482bd-f61d-4561-b5f4-8ff69f691402');