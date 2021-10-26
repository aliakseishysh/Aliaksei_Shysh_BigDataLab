WITH cte_crimes AS (
  SELECT
    streets.id AS street_id,
    streets.name AS street_name,
    crimes.month AS crime_month,
    SUM(CASE WHEN crimes.category = 'drugs'
             THEN 1
             ELSE 0
        END) AS crime_drugs,
    sum(CASE WHEN crimes.category = 'possession-of-weapons'
             THEN 1
             ELSE 0
        END) AS crime_weapons,
    sum(CASE WHEN crimes.category IN ('theft-from-the-person', 'shoplifting')
             THEN 1
             ELSE 0
        END) AS crime_theft
  FROM crimes
  INNER JOIN locations ON crimes.location = locations.location_id
  INNER JOIN streets ON locations.street = streets.street_id
  GROUP BY streets.id, streets.name, crimes.month
), cte_sas AS (
  SELECT
    streets.id AS street_id,
    streets.name AS street_name,
    TO_DATE(TO_CHAR(stop_and_searches.datetime, 'YYYY-MM'), 'YYYY-MM') AS sas_month,
    SUM(CASE WHEN object_of_search = 'Controlled drugs'
             THEN 1
             ELSE 0
        END) AS sas_drugs,
    SUM(CASE WHEN object_of_search IN ('Offensive weapons', 'Firearms')
             THEN 1
             ELSE 0
        END) AS sas_weapons,
    SUM(CASE WHEN object_of_search = 'Stolen goods'
             THEN 1
             ELSE 0
        END) AS sas_theft
  FROM stop_and_searches
  INNER JOIN locations ON stop_and_searches.location = locations.location_id
  INNER JOIN streets ON locations.street = streets.street_id
  GROUP BY streets.id, streets.name, TO_DATE(TO_CHAR(stop_and_searches.datetime, 'YYYY-MM'), 'YYYY-MM')
)
SELECT
  cte_crimes.street_id AS "Street ID",
  cte_crimes.street_name AS "Street name",
  cte_crimes.crime_month AS "Month",
  cte_crimes.crime_drugs AS "Drugs crimes count",
  cte_sas.sas_drugs AS "Drugs sas count",
  cte_crimes.crime_weapons AS "Weapons crimes count",
  cte_sas.sas_weapons AS "Weapons sas count",
  cte_crimes.crime_theft AS "Theft crimes count",
  cte_sas.sas_theft AS "Theft sas count"
FROM cte_crimes
LEFT JOIN cte_sas ON cte_crimes.street_id = cte_sas.street_id
  AND cte_crimes.street_name = cte_sas.street_name
  AND cte_crimes.crime_month = cte_sas.sas_month
WHERE cte_crimes.crime_month >= to_date(:start_month, 'YYYY-MM')
  AND cte_crimes.crime_month <= to_date(:end_month, 'YYYY-MM')
UNION
SELECT
  cte_sas.street_id AS "Street ID",
  cte_sas.street_name AS "Street name",
  cte_sas.sas_month AS "Month",
  cte_crimes.crime_drugs AS "Drugs crimes count",
  cte_sas.sas_drugs AS "Drugs sas count",
  cte_crimes.crime_weapons AS "Weapons crimes count",
  cte_sas.sas_weapons AS "Weapons sas count",
  cte_crimes.crime_theft AS "Theft crimes count",
  cte_sas.sas_theft AS "Theft sas count"
FROM cte_sas
LEFT JOIN cte_crimes ON cte_crimes.street_id = cte_sas.street_id
  AND cte_crimes.street_name = cte_sas.street_name
  AND cte_crimes.crime_month = cte_sas.sas_month
WHERE cte_sas.sas_month >= TO_DATE(:start_month, 'YYYY-MM')
  AND cte_sas.sas_month <= TO_DATE(:end_month, 'YYYY-MM')
ORDER BY "Street ID", "Month";