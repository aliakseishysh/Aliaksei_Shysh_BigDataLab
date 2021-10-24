WITH cte_crimes AS (
    SELECT
        streets.id AS street_id,
        streets.name AS street_name,
        to_char(crimes.month, 'YYYY-MM') AS crime_month,
        sum(case when crimes.category = 'drugs' then 1 else 0 end) AS crime_drugs,
        sum(case when crimes.category = 'possession-of-weapons' then 1 else 0 end) AS crime_weapons,
        sum(case when crimes.category in ('theft-from-the-person', 'shoplifting') then 1 else 0 end) AS crime_theft
    FROM crimes
    INNER JOIN locations ON crimes.location = locations.location_id
    INNER JOIN streets ON locations.street = streets.street_id
    GROUP BY streets.id, streets.name, to_char(crimes.month, 'YYYY-MM')
), cte_sas AS (
    SELECT
        streets.id AS street_id,
        streets.name AS street_name,
        to_char(stopAndSearches.datetime, 'YYYY-MM') AS sas_month,
        sum(case when object_of_search = 'Controlled drugs' then 1 else 0 end) AS sas_drugs,
        sum(case when object_of_search in ('Offensive weapons', 'Firearms') then 1 else 0 end) AS sas_weapons,
        sum(case when object_of_search = 'Stolen goods' then 1 else 0 end) AS sas_theft
    FROM stopAndSearches
    INNER JOIN locations ON stopAndSearches.location = locations.location_id
    INNER JOIN streets ON locations.street = streets.street_id
    GROUP BY streets.id, streets.name, to_char(stopAndSearches.datetime, 'YYYY-MM')
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
WHERE to_date(cte_crimes.crime_month, 'YYYYMMDD') >= to_date(:start_month, 'YYYYMMDD')
    AND to_date(cte_crimes.crime_month, 'YYYYMMDD') <= to_date(:end_month, 'YYYYMMDD')
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
WHERE to_date(cte_sas.sas_month, 'YYYYMMDD') >= to_date(:start_month, 'YYYYMMDD')
    AND to_date(cte_sas.sas_month, 'YYYYMMDD') <= to_date(:end_month, 'YYYYMMDD')
ORDER BY "Street ID", "Month"