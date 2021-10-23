SELECT DISTINCT
    crimes.category AS "Crime category",
    crimes.month AS "Month",
    -- (crimes.month::date - interval '1 month')::date AS "Month2",
    -- count(crimes.crime_id) OVER (PARTITION BY ((crimes.month::date - interval '1 month')::date), crimes.category) AS "Previous month crimes",
    -- count(crimes.crime_id) OVER (PARTITION BY crimes.month, crimes.category) AS "Previous month",
    count(crimes.crime_id) OVER (PARTITION BY crimes.month, crimes.category) AS "Current month crimes"
FROM crimes
WHERE crimes.month >= :start_month AND crimes.month <= :end_month
GROUP BY crimes.month, crimes.category, crimes.crime_id
ORDER BY crimes.category, crimes.month ASC;