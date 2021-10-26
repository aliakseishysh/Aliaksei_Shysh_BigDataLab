WITH cte_full_data AS (
  SELECT
    crimes.category AS crime_category,
    crimes.month AS crime_month,
    COALESCE(LAG(COUNT(crimes.crime_id)) OVER(PARTITION BY crimes.category
                                              ORDER BY crimes.month), 0) AS prev_month_crimes,
    COUNT(crimes.crime_id) AS curr_month_crimes
  FROM crimes
  GROUP BY crimes.month, crimes.category
  ORDER BY crimes.category, crimes.month ASC
)
SELECT
  cte_full_data.crime_category AS "Crime category",
  cte_full_data.crime_month AS "Month",
  cte_full_data.prev_month_crimes AS "Previous month crimes",
  cte_full_data.curr_month_crimes AS "Current month crimes",
  cte_full_data.curr_month_crimes - cte_full_data.prev_month_crimes AS "Delta count",
  100 * (cte_full_data.curr_month_crimes - cte_full_data.prev_month_crimes)::FLOAT / cte_full_data.prev_month_crimes
    AS "Basic growth rate"
FROM cte_full_data
WHERE
  cte_full_data.crime_month >= TO_DATE(:start_month, 'YYYY-MM')
  AND cte_full_data.crime_month <= TO_DATE(:end_month, 'YYYY-MM');


