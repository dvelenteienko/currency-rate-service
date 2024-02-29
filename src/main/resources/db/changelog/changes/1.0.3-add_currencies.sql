--liquibase formatted sql

--changeset dvelenteienko:1
INSERT INTO currency (code, type) VALUES
('USD', 'TARGET'),
('EUR', 'SOURCE'),
('GBR', 'SOURCE');
