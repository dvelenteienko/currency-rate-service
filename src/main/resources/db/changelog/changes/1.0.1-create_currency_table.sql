--liquibase formatted sql

--changeset dvelenteienko:1
CREATE TABLE currency
(
    id   UUID PRIMARY KEY,
    code VARCHAR(255) NOT NULL unique,
    type VARCHAR(255) NOT NULL
);