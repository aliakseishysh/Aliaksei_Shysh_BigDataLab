SELECT DISTINCT
    stopAndSearches.officer_defined_ethnicity,
    count(stopAndSearches.sas_id) OVER(PARTITION BY stopAndSearches.officer_defined_ethnicity) AS "Sas for ethnicity",
    sum(case when stopAndSearches.outcome = 'Arrest' then 1 else 0 end)
       over (partition by stopAndSearches.officer_defined_ethnicity) ::float /
       count(stopAndSearches.sas_id) OVER(PARTITION BY stopAndSearches.officer_defined_ethnicity) ::float * 100 AS "Arrest rate",
    sum(case when stopAndSearches.outcome = 'A no further action disposal' then 1 else 0 end)
       over (partition by stopAndSearches.officer_defined_ethnicity) ::float /
       count(stopAndSearches.sas_id) OVER(PARTITION BY stopAndSearches.officer_defined_ethnicity) ::float * 100 AS "No action rate",
    sum(case when stopAndSearches.outcome NOT IN ('Arrest', 'A no further action disposal') then 1 else 0 end)
       over (partition by stopAndSearches.officer_defined_ethnicity) ::float /
       count(stopAndSearches.sas_id) OVER(PARTITION BY stopAndSearches.officer_defined_ethnicity) ::float * 100 AS "Other outcome rate"
    -- mode() WITHIN GROUP (ORDER BY stopAndSearches.object_of_search)-- stopAndSearches.object_of_search ) -- don't work
FROM stopAndSearches
WHERE stopAndSearches.datetime >= :start_date::date AND stopAndSearches.datetime <= :end_date::date
GROUP BY stopAndSearches.sas_id