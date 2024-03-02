--liquibase formatted sql

--changeset dvelenteienko:1
CREATE TABLE RATES (
    ID UUID PRIMARY KEY,
    SOURCE VARCHAR(5) NOT NULL,
    BASE VARCHAR(5) NOT NULL,
    DATE DATE NOT NULL,
    RATE FLOAT8 NOT NULL
);