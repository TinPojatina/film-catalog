-- Film Catalog Sample Data
-- Flyway migration V2: Insert sample data for testing

-- Insert sample glumci
INSERT INTO glumci (opis) VALUES
                              ('Robert Downey Jr. - američki glumac poznat po ulozi Iron Mana'),
                              ('Chris Evans - američki glumac poznat po ulozi Captain America'),
                              ('Scarlett Johansson - američka glumica pozna po ulozi Black Widow'),
                              ('Mark Ruffalo - američki glumac poznat po ulozi Hulka'),
                              ('Chris Hemsworth - australski glumac poznat po ulozi Thora'),
                              ('Jeremy Renner - američki glumac poznat po ulozi Hawkeye'),
                              ('Leonardo DiCaprio - američki glumac i producent'),
                              ('Tom Hanks - američki glumac i filmaš'),
                              ('Meryl Streep - američka glumica'),
                              ('Denzel Washington - američki glumac i redatelj'),
                              ('Cate Blanchett - australska glumica'),
                              ('Morgan Freeman - američki glumac i narrator');

-- Insert sample filmovi
INSERT INTO filmovi (naziv) VALUES
                                ('Avengers: Endgame'),
                                ('Iron Man'),
                                ('Captain America: The First Avenger'),
                                ('Thor'),
                                ('The Avengers'),
                                ('Inception'),
                                ('Forrest Gump'),
                                ('The Devil Wears Prada'),
                                ('Malcolm X'),
                                ('The Lord of the Rings: The Fellowship of the Ring'),
                                ('The Shawshank Redemption'),
                                ('Black Widow'),
                                ('Thor: Ragnarok'),
                                ('Captain America: Civil War'),
                                ('Avengers: Infinity War');

-- Create relationships between films and actors using subqueries to get actual IDs
-- Avengers: Endgame cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Endgame'),
    (SELECT id FROM glumci WHERE opis LIKE 'Robert Downey Jr.%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Endgame'),
    (SELECT id FROM glumci WHERE opis LIKE 'Chris Evans%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Endgame'),
    (SELECT id FROM glumci WHERE opis LIKE 'Scarlett Johansson%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Endgame'),
    (SELECT id FROM glumci WHERE opis LIKE 'Mark Ruffalo%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Endgame'),
    (SELECT id FROM glumci WHERE opis LIKE 'Chris Hemsworth%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Endgame'),
    (SELECT id FROM glumci WHERE opis LIKE 'Jeremy Renner%');

-- Iron Man cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Iron Man'),
    (SELECT id FROM glumci WHERE opis LIKE 'Robert Downey Jr.%');

-- Captain America: The First Avenger cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Captain America: The First Avenger'),
    (SELECT id FROM glumci WHERE opis LIKE 'Chris Evans%');

-- Thor cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Thor'),
    (SELECT id FROM glumci WHERE opis LIKE 'Chris Hemsworth%');

-- The Avengers cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'The Avengers'),
    (SELECT id FROM glumci WHERE opis LIKE 'Robert Downey Jr.%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'The Avengers'),
    (SELECT id FROM glumci WHERE opis LIKE 'Chris Evans%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'The Avengers'),
    (SELECT id FROM glumci WHERE opis LIKE 'Scarlett Johansson%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'The Avengers'),
    (SELECT id FROM glumci WHERE opis LIKE 'Mark Ruffalo%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'The Avengers'),
    (SELECT id FROM glumci WHERE opis LIKE 'Chris Hemsworth%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'The Avengers'),
    (SELECT id FROM glumci WHERE opis LIKE 'Jeremy Renner%');

-- Inception cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Inception'),
    (SELECT id FROM glumci WHERE opis LIKE 'Leonardo DiCaprio%');

-- Forrest Gump cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Forrest Gump'),
    (SELECT id FROM glumci WHERE opis LIKE 'Tom Hanks%');

-- The Devil Wears Prada cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'The Devil Wears Prada'),
    (SELECT id FROM glumci WHERE opis LIKE 'Meryl Streep%');

-- Malcolm X cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Malcolm X'),
    (SELECT id FROM glumci WHERE opis LIKE 'Denzel Washington%');

-- The Lord of the Rings: The Fellowship of the Ring cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'The Lord of the Rings: The Fellowship of the Ring'),
    (SELECT id FROM glumci WHERE opis LIKE 'Cate Blanchett%');

-- The Shawshank Redemption cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'The Shawshank Redemption'),
    (SELECT id FROM glumci WHERE opis LIKE 'Morgan Freeman%');

-- Black Widow cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Black Widow'),
    (SELECT id FROM glumci WHERE opis LIKE 'Scarlett Johansson%');

-- Thor: Ragnarok cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Thor: Ragnarok'),
    (SELECT id FROM glumci WHERE opis LIKE 'Chris Hemsworth%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Thor: Ragnarok'),
    (SELECT id FROM glumci WHERE opis LIKE 'Cate Blanchett%');

-- Captain America: Civil War cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Captain America: Civil War'),
    (SELECT id FROM glumci WHERE opis LIKE 'Chris Evans%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Captain America: Civil War'),
    (SELECT id FROM glumci WHERE opis LIKE 'Robert Downey Jr.%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Captain America: Civil War'),
    (SELECT id FROM glumci WHERE opis LIKE 'Scarlett Johansson%');

-- Avengers: Infinity War cast
INSERT INTO film_glumac (film_id, glumac_id)
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Infinity War'),
    (SELECT id FROM glumci WHERE opis LIKE 'Robert Downey Jr.%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Infinity War'),
    (SELECT id FROM glumci WHERE opis LIKE 'Chris Evans%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Infinity War'),
    (SELECT id FROM glumci WHERE opis LIKE 'Scarlett Johansson%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Infinity War'),
    (SELECT id FROM glumci WHERE opis LIKE 'Mark Ruffalo%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Infinity War'),
    (SELECT id FROM glumci WHERE opis LIKE 'Chris Hemsworth%')
UNION ALL
SELECT
    (SELECT id FROM filmovi WHERE naziv = 'Avengers: Infinity War'),
    (SELECT id FROM glumci WHERE opis LIKE 'Jeremy Renner%');

-- Verify data insertion
DO $
DECLARE
film_count INTEGER;
    glumac_count INTEGER;
    relationship_count INTEGER;
BEGIN
SELECT COUNT(*) INTO film_count FROM filmovi;
SELECT COUNT(*) INTO glumac_count FROM glumci;
SELECT COUNT(*) INTO relationship_count FROM film_glumac;

RAISE NOTICE 'Sample data inserted successfully:';
    RAISE NOTICE '- Filmovi: % records', film_count;
    RAISE NOTICE '- Glumci: % records', glumac_count;
    RAISE NOTICE '- Film-Glumac relationships: % records', relationship_count;
END $;