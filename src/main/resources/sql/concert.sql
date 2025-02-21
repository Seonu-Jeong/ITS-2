insert into `concert` (title, singer, start_at, end_at, running_start_time, running_end_time, price, hall_id)
values ('Samurai Assassin (Samurai)', 'Francis Heintz', '2025-03-21', '2025-03-22', '20:00', '22:00', 100000,
        (SELECT id FROM hall ORDER BY RAND() LIMIT 1));
insert into `concert` (title, singer, start_at, end_at, running_start_time, running_end_time, price, hall_id)
values ('Stay Tuned', 'Melonie Arbuckle', '2025-03-21', '2025-03-21', '20:00', '22:00', 100000,
        (SELECT id FROM hall ORDER BY RAND() LIMIT 1));
