CREATE TABLE rate
(
    id          UUID PRIMARY KEY,
    base_code   VARCHAR(255) NOT NULL,
    source_code VARCHAR(255) NOT NULL,
    date        TIMESTAMP    NOT NULL,
    rate        FLOAT8       NOT NULL,
    CONSTRAINT fk_base_code FOREIGN KEY (base_code) REFERENCES currency (code)
);