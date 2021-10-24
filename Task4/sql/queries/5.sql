WITH cte_age_range AS (
    SELECT
        streets.id AS street_id,
        streets.name AS street_name,
        stopAndSearches.age_range AS age_range,
        ROW_NUMBER() OVER (PARTITION BY streets.id ORDER BY count(*) DESC) AS rn
    FROM stopAndSearches
    INNER JOIN locations ON locations.location_id = stopAndSearches.location
    INNER JOIN streets ON streets.street_id = locations.street
    WHERE stopAndSearches.datetime >= :start_date AND stopAndSearches.datetime <= :end_date
    GROUP BY streets.id, streets.name, stopAndSearches.age_range
), cte_gender AS (
    SELECT
        streets.id AS street_id,
        stopAndSearches.gender AS gender,
        ROW_NUMBER() OVER (PARTITION BY streets.id ORDER BY count(*) DESC) AS rn
    FROM stopAndSearches
    INNER JOIN locations ON locations.location_id = stopAndSearches.location
    INNER JOIN streets ON streets.street_id = locations.street
    WHERE stopAndSearches.datetime >= :start_date AND stopAndSearches.datetime <= :end_date
    GROUP BY streets.id, stopAndSearches.gender
), cte_ethnicity AS (
    SELECT
        streets.id AS street_id,
        stopAndSearches.officer_defined_ethnicity AS ethnicity,
        ROW_NUMBER() OVER (PARTITION BY streets.id ORDER BY count(*) DESC) AS rn
    FROM stopAndSearches
    INNER JOIN locations ON locations.location_id = stopAndSearches.location
    INNER JOIN streets ON streets.street_id = locations.street
    WHERE stopAndSearches.datetime >= :start_date AND stopAndSearches.datetime <= :end_date
    GROUP BY streets.id, stopAndSearches.officer_defined_ethnicity
), cte_object_of_search AS (
    SELECT
        streets.id AS street_id,
        stopAndSearches.object_of_search AS object_of_search,
        ROW_NUMBER() OVER (PARTITION BY streets.id ORDER BY count(*) DESC) AS rn
    FROM stopAndSearches
    INNER JOIN locations ON locations.location_id = stopAndSearches.location
    INNER JOIN streets ON streets.street_id = locations.street
    WHERE stopAndSearches.datetime >= :start_date AND stopAndSearches.datetime <= :end_date
    GROUP BY streets.id, stopAndSearches.object_of_search
), cte_outcome AS (
    SELECT
         streets.id AS street_id,
         stopAndSearches.outcome AS outcome,
         ROW_NUMBER() OVER (PARTITION BY streets.id ORDER BY count(*) DESC) AS rn
    FROM stopAndSearches
    INNER JOIN locations ON locations.location_id = stopAndSearches.location
    INNER JOIN streets ON streets.street_id = locations.street
    WHERE stopAndSearches.datetime >= :start_date AND stopAndSearches.datetime <= :end_date
    GROUP BY streets.id, stopAndSearches.outcome
 )
SELECT
    cte_age_range.street_id,
    cte_age_range.street_name,
    cte_age_range.age_range,
    cte_gender.gender,
    cte_ethnicity.ethnicity,
    cte_object_of_search.object_of_search,
    cte_outcome.outcome
FROM cte_age_range
INNER JOIN cte_gender ON cte_age_range.street_id = cte_gender.street_id
INNER JOIN cte_ethnicity ON cte_age_range.street_id = cte_ethnicity.street_id
INNER JOIN cte_object_of_search ON cte_age_range.street_id = cte_object_of_search.street_id
INNER JOIN cte_outcome ON cte_age_range.street_id = cte_outcome.street_id
WHERE cte_age_range.rn = 1
    AND cte_gender.rn = 1
    AND cte_ethnicity.rn = 1
    AND cte_object_of_search.rn = 1
    AND cte_outcome.rn = 1;
