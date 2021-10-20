SELECT
    streets.id AS "Street ID",
    streets.name AS "Street name",
    FORMAT('from %s till %s', substring(:start_month from 1 for 7), substring(:end_month from 1 for 7)) AS "Period",
    COUNT(locations.street) AS "Crime count"
FROM crimes
INNER JOIN locations ON crimes.location = locations.location_id
INNER JOIN streets ON locations.street = streets.street_id
WHERE crimes.month >= :start_month AND crimes.month <= :end_month
GROUP BY streets.id, streets.name
ORDER BY "Crime count" DESC;