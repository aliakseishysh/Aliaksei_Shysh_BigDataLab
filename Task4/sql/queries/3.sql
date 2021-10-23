SELECT DISTINCT
    streets.id AS "Street ID",
    streets.name AS "Street name",
    outcomes.category AS "Outcome category value",
    count(crimes.crime_id) OVER(PARTITION BY streets.id) AS "Count of crimes",
    100 * count(crimes.crime_id) OVER(PARTITION BY streets.id)::float / count(crimes.crime_id) OVER()::float AS "Percentage of the total crimes"
FROM crimes
INNER JOIN locations ON crimes.location = locations.location_id
INNER JOIN streets ON locations.street = streets.street_id
INNER JOIN outcomes ON crimes.outcome_status = outcomes.status_id
WHERE crimes.month >= :start_month AND crimes.month <= :end_month AND outcomes.category = :outcome_category
GROUP BY crimes.crime_id, streets.id, streets.name, outcomes.category;