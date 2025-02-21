-- setup.sql
use its;
-- hall insert
INSERT INTO hall (id, name, location, capacity, is_open)
VALUES (1, '올림픽 경기장', '잠실', 400, 1);
-- concert insert
INSERT INTO concert (id, hall_id, title, singer, start_at, end_at, running_start_time, running_end_time, price)
VALUES (1, 1, '아이유 콘서트', '아이유', DATE('2025-03-01'), DATE('2025-03-30'),
        TIME_FORMAT('20:00', '%h:%i'), TIME_FORMAT('22:00', '%h:%i'), 110000),

       (2, 1, '빅뱅 콘서트', '빅뱅', DATE('2025-03-11'), DATE('2025-03-30'),
        TIME_FORMAT('20:00', '%h:%i'), TIME_FORMAT('22:00', '%h:%i'), 110000),

       (3, 1, '제니 콘서트', '제니', DATE('2025-03-21'), DATE('2025-03-30'),
        TIME_FORMAT('20:00', '%h:%i'), TIME_FORMAT('22:00', '%h:%i'), 110000);

-- seat insert
INSERT INTO seat (id, hall_id, seat_number)
VALUES (1, 1, 1),
       (2, 1, 2),
       (3, 1, 3);
-- user insert
INSERT INTO user (id, email, password, name, phone_number, status, role)
VALUES (1, 'test@naver.com', 'AAaa00!!', '이름',
        '010-1234-5678', 'ACTIVATED', 'USER');
-- reservation insert
INSERT INTO reservation (id, concert_id, seat_id, user_id, concert_date, status)
VALUES (1, 1, 1, 1, CURRENT_DATE, 'PENDING');
