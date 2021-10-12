SELECT 'CREATE DATABASE task_3' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'task_3')\gexec
\c 'task_3'

DO $$
BEGIN
    BEGIN
        CREATE TYPE location_type AS ENUM ('Force', 'BTP');
    EXCEPTION
        WHEN DUPLICATE_OBJECT THEN
            RAISE NOTICE 'type "location_type" already exists, skipping';
    END;

    BEGIN
        CREATE TYPE status AS ENUM (
            'awaiting-court-result', 'Awaiting court outcome',
            'court-result-unavailable', 'Court result unavailable',
            'unable-to-proceed', 'Court case unable to proceed',
            'local-resolution', 'Local resolution',
            'no-further-action', 'Investigation complete; no suspect identified',
            'deprived-of-property', 'Offender deprived of property',
            'fined', 'Offender fined',
            'absolute-discharge', 'Offender given absolute discharge',
            'cautioned', 'Offender given a caution',
            'drugs-possession-warning', 'Offender given a drugs possession warning',
            'penalty-notice-issued', 'Offender given a penalty notice',
            'community-penalty', 'Offender given community sentence',
            'conditional-discharge', 'Offender given conditional discharge',
            'suspended-sentence', 'Offender given suspended prison sentence',
            'imprisoned', 'Offender sent to prison',
            'other-court-disposal', 'Offender otherwise dealt with',
            'compensation', 'Offender ordered to pay compensation',
            'sentenced-in-another-case', 'Suspect charged as part of another case',
            'charged', 'Suspect charged',
            'not-guilty', 'Defendant found not guilty',
            'sent-to-crown-court', 'Defendant sent to Crown Court',
            'unable-to-prosecute', 'Unable to prosecute suspect',
            'formal-action-not-in-public-interest', 'Formal action is not in the public interest',
            'action-taken-by-another-organisation', 'Action to be taken by another organisation',
            'further-investigation-not-in-public-interest', 'Further investigation is not in the public interest',
            'further-action-not-in-public-interest', 'Further action is not in the public interest',
            'under-investigation', 'Under investigation',
            'status-update-unavailable', 'Status update unavailable'
            );
        EXCEPTION
            WHEN DUPLICATE_OBJECT THEN
                RAISE NOTICE 'type "outcome_status" already exists, skipping';
    END;

CREATE TABLE IF NOT EXISTS streets(
   "street_id" serial PRIMARY KEY,
   "id" bigint NOT NULL,
   "name" VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS locations(
    "location_id" bigint PRIMARY KEY,
    "latitude" VARCHAR(20) NOT NULL,
    "street" bigint NOT NULL REFERENCES streets (street_id),
    "longitude" VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS outcomes(
    "status_id" bigint PRIMARY KEY,
    "category" status,
    "date" DATE
);

CREATE TABLE IF NOT EXISTS crimes(
    "crime_id" bigserial PRIMARY KEY,
    "category" VARCHAR(255) NOT NULL,
    "persistent_id" VARCHAR(255) NOT NULL,
    "month" DATE,
    "location" bigint NOT NULL REFERENCES locations (location_id),
    "context" VARCHAR(255) NOT NULL,
    "id" bigint NOT NULL,
    "location_type" location_type NOT NULL,
    "location_subtype" VARCHAR(255) NOT NULL,
    "outcome_status" bigint REFERENCES outcomes (status_id)
);


END$$;