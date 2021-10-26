WITH cte AS (
  SELECT
    stop_and_searches.officer_defined_ethnicity AS cte_ethnicity,
    stop_and_searches.object_of_search AS cte_obj_of_search,
    COUNT(*) AS n,
    ROW_NUMBER() OVER (PARTITION BY stop_and_searches.officer_defined_ethnicity ORDER BY COUNT(*) DESC) AS rn
  FROM stop_and_searches
  GROUP BY officer_defined_ethnicity, object_of_search
)
SELECT DISTINCT
  stop_and_searches.officer_defined_ethnicity,
  COUNT(stop_and_searches.sas_id) OVER(PARTITION BY stop_and_searches.officer_defined_ethnicity) AS "Sas for ethnicity",
  SUM(CASE WHEN stop_and_searches.outcome = 'Arrest'
           THEN 1
           ELSE 0
      END)
    OVER (PARTITION BY stop_and_searches.officer_defined_ethnicity)::FLOAT /
    COUNT(stop_and_searches.sas_id) OVER(PARTITION BY stop_and_searches.officer_defined_ethnicity)::FLOAT *
      100 AS "Arrest rate",
  SUM(CASE WHEN stop_and_searches.outcome = 'A no further action disposal'
           THEN 1
           ELSE 0
      END)
    OVER (PARTITION BY stop_and_searches.officer_defined_ethnicity)::FLOAT /
    COUNT(stop_and_searches.sas_id) OVER(PARTITION BY stop_and_searches.officer_defined_ethnicity)::FLOAT *
      100 AS "No action rate",
  SUM(CASE WHEN stop_and_searches.outcome NOT IN ('Arrest', 'A no further action disposal')
           THEN 1
           ELSE 0
      END)
    OVER (PARTITION BY stop_and_searches.officer_defined_ethnicity)::FLOAT /
    COUNT(stop_and_searches.sas_id) OVER(PARTITION BY stop_and_searches.officer_defined_ethnicity)::FLOAT *
      100 AS "Other outcome rate",
  cte.cte_obj_of_search AS "Most popular object of search"
FROM stop_and_searches
INNER JOIN cte ON stop_and_searches.officer_defined_ethnicity IS NOT DISTINCT FROM cte.cte_ethnicity
WHERE
  rn = 1
  AND stop_and_searches.datetime >= :start_date
  AND stop_and_searches.datetime <= :end_date
ORDER BY stop_and_searches.officer_defined_ethnicity;