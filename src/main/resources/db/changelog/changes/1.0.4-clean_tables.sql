--liquibase formatted sql

--changeset dvelenteienko:1
TRUNCATE TABLE currency;
TRUNCATE TABLE rates;