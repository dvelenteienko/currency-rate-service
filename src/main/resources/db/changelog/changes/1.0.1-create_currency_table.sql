--liquibase formatted sql

--changeset dvelenteienko:1
CREATE TABLE currency
(
    id   UUID PRIMARY KEY,
    code VARCHAR(3) NOT NULL unique
);