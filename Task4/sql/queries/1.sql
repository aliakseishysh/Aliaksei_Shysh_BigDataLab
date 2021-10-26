SELECT
  streets.id AS "Street ID",
  streets.name AS "Street name",
  FORMAT('from %s till %s', :start_month, :end_month) AS "Period",
  COUNT(locations.street) AS "Crime count"
FROM crimes
INNER JOIN locations ON crimes.location = locations.location_id
INNER JOIN streets ON locations.street = streets.street_id
WHERE
    crimes.month >= TO_DATE(:start_month, 'YYYY-MM')
    AND crimes.month <= TO_DATE(:end_month, 'YYYY-MM')
GROUP BY streets.id, streets.name
ORDER BY "Crime count" DESC;