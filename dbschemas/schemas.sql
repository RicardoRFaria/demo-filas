CREATE SCHEMA demofilas;

CREATE TABLE demofilas.item
(
    item_id     UUID PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description VARCHAR(255)   NOT NULL,
    price       NUMERIC(10, 2) NOT NULL,
    active      bool,
    version     bigint         NOT NULL DEFAULT 0
);

