CREATE TABLE parking_event (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    license_plate   VARCHAR(255) NOT NULL,
    sector          VARCHAR(255),
    entry_time      DATETIME     NOT NULL,
    exit_time       DATETIME,
    amount_charged  DOUBLE,
    event_date      VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_parking_event_sector FOREIGN KEY (sector) REFERENCES sector (sector)
);