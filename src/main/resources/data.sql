INSERT INTO APP_user (ID, USERNAME, PASSWORD, AUTHORITIES, FIRST_NAME, LAST_NAME, EMAIL) VALUES ('test-01', 'admin@oeh.at', '$2a$10$DAG/wOAS0MU/egnCqlG4YOITO7ZIuGqUrsrFCrE7xPJxACwWQKl3.', 'user::admin', 'Admin', 'ISTRATOR', 'admin@oeh.at');

INSERT INTO VALIDATION (
    TYPE,
    LAST_UPDATE,
    VALID_UNTIL,
    EMAIL,
    STUDENT_ID
)
VALUES
    ('MANUAL',    '2026-03-01 10:15:00', '2026-09-01 00:00:00', 'anna.mueller@example.com',     '1000001'),
    ('AUTOMATIC', '2026-03-02 11:20:00', '2026-09-02 00:00:00', 'ben.schmidt@example.com',      '1000002'),
    ('MANUAL',    '2026-03-03 09:10:00', '2026-09-03 00:00:00', 'clara.weber@example.com',      '1000003'),
    ('AUTOMATIC', '2026-03-04 14:05:00', '2026-09-04 00:00:00', 'david.fischer@example.com',    '1000004'),
    ('MANUAL',    '2026-03-05 08:45:00', '2026-09-05 00:00:00', 'emma.wagner@example.com',      '1000005'),
    ('AUTOMATIC', '2026-03-06 16:30:00', '2026-09-06 00:00:00', 'felix.bauer@example.com',      '1000006'),
    ('MANUAL',    '2026-03-07 12:00:00', '2026-09-07 00:00:00', 'greta.hoffmann@example.com',   '1000007'),
    ('AUTOMATIC', '2026-03-08 13:25:00', '2026-09-08 00:00:00', 'hans.schneider@example.com',   '1000008'),
    ('MANUAL',    '2026-03-09 10:40:00', '2026-09-09 00:00:00', 'irina.klein@example.com',      '1000009'),
    ('AUTOMATIC', '2026-03-10 17:55:00', '2026-09-10 00:00:00', 'jonas.braun@example.com',      '1000010'),
    ('MANUAL',    '2026-03-11 09:15:00', '2026-09-11 00:00:00', 'kira.lang@example.com',        '1000011'),
    ('AUTOMATIC', '2026-03-12 11:45:00', '2026-09-12 00:00:00', 'lukas.meier@example.com',      '1000012'),
    ('MANUAL',    '2026-03-13 08:20:00', '2026-09-13 00:00:00', 'mia.keller@example.com',       '1000013'),
    ('AUTOMATIC', '2026-03-14 15:10:00', '2026-09-14 00:00:00', 'noah.fuchs@example.com',       '1000014'),
    ('MANUAL',    '2026-03-15 10:05:00', '2026-09-15 00:00:00', 'olivia.brunner@example.com',   '1000015'),
    ('AUTOMATIC', '2026-03-16 18:30:00', '2026-09-16 00:00:00', 'paul.lehner@example.com',      '1000016'),
    ('MANUAL',    '2026-03-17 09:50:00', '2026-09-17 00:00:00', 'quinn.schulz@example.com',     '1000017'),
    ('AUTOMATIC', '2026-03-18 14:40:00', '2026-09-18 00:00:00', 'lea.boehm@example.com',        '1000018'),
    ('MANUAL',    '2026-03-19 07:35:00', '2026-09-19 00:00:00', 'tom.neumann@example.com',      '1000019'),
    ('AUTOMATIC', '2026-03-20 13:15:00', '2026-09-20 00:00:00', 'lara.schuster@example.com',    '1000020');