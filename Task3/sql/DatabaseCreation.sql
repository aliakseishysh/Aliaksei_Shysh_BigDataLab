SELECT 'CREATE DATABASE task_3' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'task_3')\gexec
\c 'task_3'

DO $$
BEGIN

CREATE TABLE IF NOT EXISTS streets(
   "street_id" bigserial PRIMARY KEY,
   "id" bigint NOT NULL UNIQUE,
   "name" VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations(
    "location_id" bigserial PRIMARY KEY,
    "latitude" VARCHAR(20) NOT NULL,
    "street" bigint NOT NULL REFERENCES streets (street_id),
    "longitude" VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS outcomes(
    "status_id" bigserial PRIMARY KEY,
    -- "category" status,
    "category" VARCHAR(255),
    "date" DATE
);

CREATE TABLE IF NOT EXISTS crimes(
    "crime_id" bigserial PRIMARY KEY,
    "category" VARCHAR(255) NOT NULL,
    "persistent_id" VARCHAR(255) NOT NULL UNIQUE,
    "month" DATE NOT NULL,
    "location" bigint NOT NULL REFERENCES locations (location_id),
    "context" VARCHAR(255) NOT NULL,
    "id" bigint NOT NULL UNIQUE,
    -- "location_type" location_type NOT NULL,
    "location_type" VARCHAR(255) NOT NULL,
    "location_subtype" VARCHAR(255) NOT NULL,
    "outcome_status" bigint REFERENCES outcomes (status_id)
);

END $$;