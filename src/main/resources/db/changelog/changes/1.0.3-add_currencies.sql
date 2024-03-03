--liquibase formatted sql

--changeset dvelenteienko:1
INSERT INTO currency (id, code, type) VALUES
('00000001-0000-0000-0000-000000000001', 'USD', 'BASE')
