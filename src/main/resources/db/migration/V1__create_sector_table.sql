CREATE TABLE sector (
    sector       VARCHAR(255) NOT NULL,
    base_price   DOUBLE       NOT NULL,
    max_capacity INT          NOT NULL,
    is_open      BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (sector)
);
