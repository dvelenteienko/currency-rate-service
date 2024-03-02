--liquibase formatted sql

--changeset dvelenteienko:1
CREATE TABLE currency (
    ID UUID PRIMARY KEY,
    code TEXT NOT NULL,
    type TEXT NOT NULL
);