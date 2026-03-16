-- liquibase formatted sql

-- changeset promedicus:001-create-admissions-table
CREATE TABLE admissions (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name               VARCHAR(255) NOT NULL,
    birthday           DATE         NOT NULL,
    sex                VARCHAR(20)  NOT NULL,
    category           VARCHAR(20)  NOT NULL,
    date_of_admission  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    external_system_id VARCHAR(255) NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_sex CHECK (sex IN ('FEMALE','MALE','INTERSEX','UNKNOWN')),
    CONSTRAINT chk_category CHECK (category IN ('NORMAL','INPATIENT','EMERGENCY','OUTPATIENT'))
);

CREATE INDEX idx_admissions_date_of_admission
    ON admissions (date_of_admission DESC);
