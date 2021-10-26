WITH cte_full_data AS (
  SELECT
    crimes.category AS "Crime category",
    crimes.month AS "Month",
    COALESCE(LAG(COUNT(crimes.crime_id)) OVER(PARTITION BY crimes.category
                                              ORDER BY crimes.month), 0) AS "Previous month crimes",
    COUNT(crimes.crime_id) AS "Current month crimes"
  FROM crimes
  GROUP BY crimes.month, crimes.category
  ORDER BY crimes.category, crimes.month ASC
)
SELECT
   *
FROM cte_full_data
WHERE
  "Month" >= TO_DATE(:start_month, 'YYYY-MM')
  AND "Month" <= TO_DATE(:end_month, 'YYYY-MM');


