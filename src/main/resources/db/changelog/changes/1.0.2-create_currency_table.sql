--liquibase formatted sql

--changeset dvelenteienko:1
CREATE TABLE currency (
    code TEXT NOT NULL,
    type TEXT NOT NULL,
    CONSTRAINT PR_currency PRIMARY KEY (code, type)
);