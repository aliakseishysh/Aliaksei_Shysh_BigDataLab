WITH full_data AS (
    SELECT
        crimes.category AS "Crime category",
        crimes.month AS "Month",
        COALESCE(LAG(count(crimes.crime_id)) OVER(PARTITION BY crimes.category ORDER BY crimes.month), 0) AS "Previous month crimes",
        count(crimes.crime_id) AS "Current month crimes"
    FROM crimes
    GROUP BY crimes.month, crimes.category
    ORDER BY crimes.category, crimes.month ASC
)
SELECT * FROM full_data
WHERE "Month" >= :start_month AND "Month" <= :end_month;

