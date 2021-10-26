SELECT DISTINCT
  streets.id AS "Street ID",
  streets.name AS "Street name",
  outcomes.category AS "Outcome category value",
  COUNT(crimes.crime_id) OVER(PARTITION BY streets.id) AS "Count of crimes",
  100 * COUNT(crimes.crime_id) OVER(PARTITION BY streets.id)::FLOAT /
    COUNT(crimes.crime_id) OVER()::FLOAT AS "Percentage of the total crimes"
FROM crimes
INNER JOIN locations ON crimes.location = locations.location_id
INNER JOIN streets ON locations.street = streets.street_id
INNER JOIN outcomes ON crimes.outcome_status = outcomes.status_id
WHERE
  crimes.month >= TO_DATE(:start_month, 'YYYY-MM')
  AND crimes.month <= TO_DATE(:end_month, 'YYYY-MM')
  AND outcomes.category = :outcome_category
GROUP BY crimes.crime_id, streets.id, streets.name, outcomes.category
ORDER BY "Count of crimes" DESC;