-- 유저 테스트 데이터
INSERT INTO user (id, email, password, name, phone_number, created_at, modified_at, status, role)
WITH RECURSIVE cte (n) AS
                   (SELECT 1
                    UNION ALL
                    SELECT n + 1
                    FROM cte
                    WHERE n < 10 -- 생성하고 싶은 더미 데이터의 개수
                   )
SELECT n                       AS id,
       concat(n, '@email.com') AS email,
       'Password1234@'         AS password,
       concat('user', n)       AS name,
       '010-1234-5678'         AS phone_number,
       now()                   AS created_at,
       now()                   AS modified_at,
       'ACTIVATED'             AS status,
       'USER'                  AS role
FROM cte;

-- 공연장 테스트 데이터
INSERT INTO hall (id, name, location, capacity, created_at, modified_at, is_open)
VALUES (1, '상암 경기장', '상암', 10, now(), now(), 1);

-- 공연장 좌석 테스트 데이터
INSERT INTO seat (id, hall_id, seat_number)
WITH RECURSIVE cte (n) AS
                   (SELECT 1
                    UNION ALL
                    SELECT n + 1
                    FROM cte
                    WHERE n < 10 -- 생성하고 싶은 더미 데이터의 개수
                   )
SELECT n AS id,
       1 AS hall_id,
       n AS seat_number
FROM cte;

-- 콘서트 테스트 데이터
INSERT INTO concert (id, hall_id, title, singer, start_at, end_at, running_start_time, running_end_time, price)
VALUES (1, 1, '아이유 콘서트', '아이유', '2025-05-01', '2025-05-15', '06:00', '23:59', 1000);