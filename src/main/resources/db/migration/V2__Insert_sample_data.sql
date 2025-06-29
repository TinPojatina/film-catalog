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

-- Create relationships between films and actors
-- Avengers: Endgame cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
                                                 (1, 1), -- Robert Downey Jr.
                                                 (1, 2), -- Chris Evans
                                                 (1, 3), -- Scarlett Johansson
                                                 (1, 4), -- Mark Ruffalo
                                                 (1, 5), -- Chris Hemsworth
                                                 (1, 6); -- Jeremy Renner

-- Iron Man cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
    (2, 1); -- Robert Downey Jr.

-- Captain America: The First Avenger cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
    (3, 2); -- Chris Evans

-- Thor cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
    (4, 5); -- Chris Hemsworth

-- The Avengers cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
                                                 (5, 1), -- Robert Downey Jr.
                                                 (5, 2), -- Chris Evans
                                                 (5, 3), -- Scarlett Johansson
                                                 (5, 4), -- Mark Ruffalo
                                                 (5, 5), -- Chris Hemsworth
                                                 (5, 6); -- Jeremy Renner

-- Inception cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
    (6, 7); -- Leonardo DiCaprio

-- Forrest Gump cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
    (7, 8); -- Tom Hanks

-- The Devil Wears Prada cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
    (8, 9); -- Meryl Streep

-- Malcolm X cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
    (9, 10); -- Denzel Washington

-- The Lord of the Rings: The Fellowship of the Ring cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
    (10, 11); -- Cate Blanchett

-- The Shawshank Redemption cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
    (11, 12); -- Morgan Freeman

-- Black Widow cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
    (12, 3); -- Scarlett Johansson

-- Thor: Ragnarok cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
                                                 (13, 5), -- Chris Hemsworth
                                                 (13, 11); -- Cate Blanchett

-- Captain America: Civil War cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
                                                 (14, 2), -- Chris Evans
                                                 (14, 1), -- Robert Downey Jr.
                                                 (14, 3); -- Scarlett Johansson

-- Avengers: Infinity War cast
INSERT INTO film_glumac (film_id, glumac_id) VALUES
                                                 (15, 1), -- Robert Downey Jr.
                                                 (15, 2), -- Chris Evans
                                                 (15, 3), -- Scarlett Johansson
                                                 (15, 4), -- Mark Ruffalo
                                                 (15, 5), -- Chris Hemsworth
                                                 (15, 6); -- Jeremy Renner

-- Verify data insertion
-- Check total counts
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