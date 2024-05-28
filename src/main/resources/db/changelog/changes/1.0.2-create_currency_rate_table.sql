CREATE TABLE rate
(
    id          UUID PRIMARY KEY,
    base_currency_id   UUID NOT NULL,
    source_currency_id UUID NOT NULL,
    date        TIMESTAMP    NOT NULL,
    rate        FLOAT8       NOT NULL,
    FOREIGN KEY (base_currency_id) REFERENCES currency(id) ON DELETE CASCADE,
    FOREIGN KEY (source_currency_id) REFERENCES currency(id) ON DELETE CASCADE
);