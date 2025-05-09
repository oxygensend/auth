-- liquibase formatted sql

-- changeset sberdzik:1746782493336-1
CREATE TABLE domain_events
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    event_id       VARCHAR(255) NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type     VARCHAR(255) NOT NULL,
    event_payload  TEXT         NOT NULL,
    occurred_on    datetime     NOT NULL,
    CONSTRAINT pk_domain_events PRIMARY KEY (id)
);

-- changeset sberdzik:1746782493336-2
CREATE TABLE sessions
(
    id      BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    CONSTRAINT pk_sessions PRIMARY KEY (id)
);

-- changeset sberdzik:1746782493336-3
CREATE TABLE users
(
    id                      BINARY(16) NOT NULL,
    business_id             VARCHAR(255) NULL,
    email                   VARCHAR(255) NOT NULL,
    username                VARCHAR(255) NOT NULL,
    roles                   VARCHAR(255) NOT NULL,
    password                VARCHAR(255) NOT NULL,
    `locked`                BIT(1)       NOT NULL,
    verified                BIT(1)       NOT NULL,
    account_activation_type SMALLINT     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

