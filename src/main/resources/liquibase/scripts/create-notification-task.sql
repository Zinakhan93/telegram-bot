-- liquibase formatted sql

-- changeset Zina:1
CREATE Table notificationitask (
                                   id bigserial primary key,
                                   text VARCHAR(255),
                                   chat_Id BIGINT,
                                   date_time TIMESTAMP
)