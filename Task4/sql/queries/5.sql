WITH cte_data_provider AS (
  SELECT
    streets.id AS street_id,
    streets.name AS street_name,
    stop_and_searches.age_range AS age_range,
    stop_and_searches.gender AS gender,
    stop_and_searches.officer_defined_ethnicity AS ethnicity,
    stop_and_searches.object_of_search AS object_of_search,
    stop_and_searches.outcome AS outcome
  FROM stop_and_searches
  INNER JOIN locations ON locations.location_id = stop_and_searches.location
  INNER JOIN streets ON streets.street_id = locations.street
  WHERE stop_and_searches.datetime >= :start_date AND stop_and_searches.datetime <= :end_date
), cte_age_range AS (
    SELECT
      cte_data_provider.street_id AS street_id,
      cte_data_provider.age_range AS age_range,
      FIRST_VALUE() OVER (PARTITION BY cte_data_provider.street_id ORDER BY cte_data_provider.age_range) AS q1
    FROM cte_data_provider
    GROUP BY cte_data_provider.street_id, cte_data_provider.age_range
), cte_gender AS (
  SELECT
    cte_data_provider.street_id AS street_id,
    cte_data_provider.gender AS gender,
    FIRST_VALUE() OVER (PARTITION BY cte_data_provider.street_id ORDER BY cte_data_provider.gender) AS q2
  FROM cte_data_provider
  GROUP BY cte_data_provider.street_id, cte_data_provider.gender
), cte_ethnicity AS (
  SELECT
    cte_data_provider.street_id AS street_id,
    cte_data_provider.ethnicity AS ethnicity,
    FIRST_VALUE() OVER (PARTITION BY cte_data_provider.street_id ORDER BY cte_data_provider.ethnicity) AS q3
  FROM cte_data_provider
  GROUP BY cte_data_provider.street_id, cte_data_provider.ethnicity
), cte_object_of_search AS (
  SELECT
    cte_data_provider.street_id AS street_id,
    cte_data_provider.object_of_search AS object_of_search,
    FIRST_VALUE() OVER (PARTITION BY cte_data_provider.street_id ORDER BY cte_data_provider.object_of_search) AS q4
  FROM cte_data_provider
  GROUP BY cte_data_provider.street_id, cte_data_provider.object_of_search
), cte_outcome AS (
  SELECT
    cte_data_provider.street_id AS street_id,
    cte_data_provider.outcome AS outcome,
    FIRST_VALUE() OVER (PARTITION BY cte_data_provider.street_id ORDER BY cte_data_provider.outcome) AS q5
  FROM cte_data_provider
  GROUP BY cte_data_provider.street_id, cte_data_provider.outcome
)
SELECT
  cte_data_provider.street_id,
  cte_data_provider.street_name,
  cte_age_range.age_range,
  cte_gender.gender,
  cte_ethnicity.ethnicity,
  cte_object_of_search.object_of_search,
  cte_outcome.outcome
FROM cte_data_provider
INNER JOIN cte_age_range ON cte_data_provider.street_id = cte_age_range.street_id
INNER JOIN cte_gender ON cte_data_provider.street_id = cte_gender.street_id
INNER JOIN cte_ethnicity ON cte_data_provider.street_id = cte_ethnicity.street_id
INNER JOIN cte_object_of_search ON cte_data_provider.street_id = cte_object_of_search.street_id
INNER JOIN cte_outcome ON cte_data_provider.street_id = cte_outcome.street_id;