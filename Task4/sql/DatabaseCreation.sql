SELECT 'CREATE DATABASE task_4' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'task_4')\gexec
\c 'task_4'

DO $$
BEGIN

CREATE TABLE IF NOT EXISTS streets(
   "street_id" bigserial PRIMARY KEY,
   "id" bigint NOT NULL UNIQUE,
   "name" VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations(
    "location_id" bigserial PRIMARY KEY,
    "latitude" VARCHAR(20),
    "street" bigint REFERENCES streets (street_id),
    "longitude" VARCHAR(20)
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

CREATE TABLE IF NOT EXISTS outcome_objects(
    "outcome_object_id" bigserial PRIMARY KEY,
    "id" bigint,
    "name" VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS stopAndSearches(
    "sas_id" bigserial PRIMARY KEY,
    "type" VARCHAR(25),
    "involved_person" BOOLEAN,
    "datetime" TIMESTAMPTZ,
    "operation" BOOLEAN,
    "operation_name" VARCHAR(255),
    "location" bigint REFERENCES locations (location_id),
    "gender" VARCHAR(255),
    "age_range" VARCHAR(30),
    "self_defined_ethnicity" VARCHAR(255),
    "officer_defined_ethnicity" VARCHAR(255),
    "legislation" VARCHAR(255),
    "object_of_search" VARCHAR(255),
    "outcome" VARCHAR(255),
    "outcome_linked_to_object_of_search" BOOLEAN,
    "removal_of_more_than_outer_clothing" BOOLEAN
);

END $$;