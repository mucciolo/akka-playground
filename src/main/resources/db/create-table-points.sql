-- liquibase formatted sql

-- changeset mucciolo:1
CREATE TABLE points
(
    seq_id BIGSERIAL PRIMARY KEY,
    id     UUID UNIQUE NOT NULL,
    value  BIGINT      NOT NULL
)
-- rollback DROP TABLE points