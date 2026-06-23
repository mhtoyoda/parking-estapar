CREATE TABLE spot (
    id                    BIGINT       NOT NULL,
    sector                VARCHAR(255) NOT NULL,
    lat                   DOUBLE       NOT NULL,
    lng                   DOUBLE       NOT NULL,
    occupied              BOOLEAN      NOT NULL DEFAULT FALSE,
    current_license_plate VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_spot_sector FOREIGN KEY (sector) REFERENCES sector (sector)
);