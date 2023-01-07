CREATE SCHEMA demofilas;

CREATE TABLE demofilas.item
(
    item_id     UUID PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description VARCHAR(255)   NOT NULL,
    price       NUMERIC(10, 2) NOT NULL,
    active      bool,
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE demofilas.price_change_history
(
    event_id   UUID PRIMARY KEY,
    item_id    UUID           NOT NULL,
    new_price  NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP      NOT NULL DEFAULT NOW()
);

ALTER TABLE demofilas.price_change_history
    ADD CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES demofilas.item (item_id);