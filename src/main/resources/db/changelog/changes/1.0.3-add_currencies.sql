--liquibase formatted sql

--changeset dvelenteienko:1
INSERT INTO currency (id, code, type) VALUES
('f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454', 'USD', 'BASE')
