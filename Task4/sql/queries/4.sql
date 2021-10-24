WITH cte AS (
    SELECT
        stopAndSearches.officer_defined_ethnicity AS cte_ethnicity,
        stopAndSearches.object_of_search AS cte_obj_of_search,
        count(*) AS n,
        ROW_NUMBER() OVER (PARTITION BY stopAndSearches.officer_defined_ethnicity ORDER BY count(*) DESC) AS rn
    FROM stopAndSearches
    GROUP BY officer_defined_ethnicity, object_of_search
)
SELECT DISTINCT
    stopAndSearches.officer_defined_ethnicity,
    count(stopAndSearches.sas_id) OVER(PARTITION BY stopAndSearches.officer_defined_ethnicity) AS "Sas for ethnicity",
    sum(case when stopAndSearches.outcome = 'Arrest' then 1 else 0 end)
       OVER (PARTITION BY stopAndSearches.officer_defined_ethnicity) ::float /
       count(stopAndSearches.sas_id) OVER(PARTITION BY stopAndSearches.officer_defined_ethnicity)::float * 100 AS "Arrest rate",
   sum(case when stopAndSearches.outcome = 'A no further action disposal' then 1 else 0 end)
      OVER (PARTITION BY stopAndSearches.officer_defined_ethnicity) ::float /
      count(stopAndSearches.sas_id) OVER(PARTITION BY stopAndSearches.officer_defined_ethnicity)::float * 100 AS "No action rate",
   sum(case when stopAndSearches.outcome NOT IN ('Arrest', 'A no further action disposal') then 1 else 0 end)
      OVER (PARTITION BY stopAndSearches.officer_defined_ethnicity) ::float /
      count(stopAndSearches.sas_id) OVER(PARTITION BY stopAndSearches.officer_defined_ethnicity)::float * 100 AS "Other outcome rate",
   cte.cte_obj_of_search AS "Most popular object of search"
FROM stopAndSearches
INNER JOIN cte ON stopAndSearches.officer_defined_ethnicity IS NOT DISTINCT FROM cte.cte_ethnicity
WHERE rn = 1 AND stopAndSearches.datetime >= :start_date AND stopAndSearches.datetime <= :end_date
ORDER BY stopAndSearches.officer_defined_ethnicity;

--SELECT DISTINCT ON (stopAndSearches.officer_defined_ethnicity)
--       stopAndSearches.officer_defined_ethnicity,
--       stopAndSearches.object_of_search,
--       count(*) AS ct
--FROM   stopAndSearches
--GROUP  BY stopAndSearches.officer_defined_ethnicity,
--          stopAndSearches.object_of_search
--ORDER  BY stopAndSearches.officer_defined_ethnicity, ct DESC, stopAndSearches.object_of_search;