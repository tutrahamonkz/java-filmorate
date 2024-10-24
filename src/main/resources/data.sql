MERGE INTO mpa_type AS mt
USING (VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17')
) AS source(mpa_name)
ON mt.mpa_name = source.mpa_name
WHEN NOT MATCHED THEN
    INSERT (mpa_name) VALUES (source.mpa_name);

MERGE INTO genre_type AS gt
USING (VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик')
) AS source(genre_name)
ON gt.genre_name = source.genre_name
WHEN NOT MATCHED THEN
    INSERT (genre_name) VALUES (source.genre_name);