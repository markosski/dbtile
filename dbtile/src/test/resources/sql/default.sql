SELECT MAX(ROUND(latitude, {{round}})) AS latitude, MAX(ROUND(longitude, {{round}})) AS longitude FROM {{table_name}} GROUP BY CONCAT(ROUND(latitude, {{round}}), ROUND(longitude, {{round}}))
