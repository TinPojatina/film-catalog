-- Film Catalog Data Verification
-- Flyway migration V3: Verify sample data insertion and add useful statistics

-- Simple verification queries that will show in application logs
SELECT 'DATA VERIFICATION STARTED' as verification_status;

-- Count records in each table
SELECT COUNT(*) as total_filmovi FROM filmovi;
SELECT COUNT(*) as total_glumci FROM glumci;
SELECT COUNT(*) as total_relationships FROM film_glumac;

-- Verify specific sample data exists
SELECT 'VERIFICATION: Key films exist' as check_name,
       EXISTS(SELECT 1 FROM filmovi WHERE naziv = 'Avengers: Endgame') as avengers_endgame_exists,
       EXISTS(SELECT 1 FROM filmovi WHERE naziv = 'Iron Man') as iron_man_exists,
       EXISTS(SELECT 1 FROM filmovi WHERE naziv = 'Inception') as inception_exists;

SELECT 'VERIFICATION: Key actors exist' as check_name,
       EXISTS(SELECT 1 FROM glumci WHERE opis LIKE 'Robert Downey Jr.%') as rdj_exists,
       EXISTS(SELECT 1 FROM glumci WHERE opis LIKE 'Leonardo DiCaprio%') as leo_exists,
       EXISTS(SELECT 1 FROM glumci WHERE opis LIKE 'Tom Hanks%') as hanks_exists;

-- Check relationship integrity
SELECT 'VERIFICATION: Relationships exist' as check_name,
       COUNT(*) as avengers_endgame_cast_count
FROM film_glumac fg
         JOIN filmovi f ON fg.film_id = f.id
WHERE f.naziv = 'Avengers: Endgame';

-- Show films with most actors
SELECT 'TOP FILMS BY CAST SIZE' as report_name;
SELECT f.naziv as film_naziv,
       COUNT(fg.glumac_id) as broj_glumaca
FROM filmovi f
         LEFT JOIN film_glumac fg ON f.id = fg.film_id
GROUP BY f.id, f.naziv
ORDER BY broj_glumaca DESC, f.naziv
    LIMIT 5;

-- Show actors in most films
SELECT 'TOP ACTORS BY FILM COUNT' as report_name;
SELECT g.opis as glumac_opis,
       COUNT(fg.film_id) as broj_filmova
FROM glumci g
         LEFT JOIN film_glumac fg ON g.id = fg.glumac_id
GROUP BY g.id, g.opis
ORDER BY broj_filmova DESC, g.opis
    LIMIT 5;

-- Show actors without films (should be empty after V2)
SELECT 'ACTORS WITHOUT FILMS' as report_name;
SELECT g.opis as glumac_bez_filmova
FROM glumci g
         LEFT JOIN film_glumac fg ON g.id = fg.glumac_id
WHERE fg.film_id IS NULL;

-- Show films without actors (should be empty after V2)
SELECT 'FILMS WITHOUT ACTORS' as report_name;
SELECT f.naziv as film_bez_glumaca
FROM filmovi f
         LEFT JOIN film_glumac fg ON f.id = fg.film_id
WHERE fg.glumac_id IS NULL;

-- Create some useful views for future queries
CREATE OR REPLACE VIEW v_film_stats AS
SELECT
    f.id,
    f.naziv,
    COUNT(fg.glumac_id) as broj_glumaca,
    f.created_at,
    f.updated_at
FROM filmovi f
         LEFT JOIN film_glumac fg ON f.id = fg.film_id
GROUP BY f.id, f.naziv, f.created_at, f.updated_at;

CREATE OR REPLACE VIEW v_glumac_stats AS
SELECT
    g.id,
    g.opis,
    COUNT(fg.film_id) as broj_filmova,
    g.created_at,
    g.updated_at
FROM glumci g
         LEFT JOIN film_glumac fg ON g.id = fg.glumac_id
GROUP BY g.id, g.opis, g.created_at, g.updated_at;

-- Create a comprehensive view showing film-actor relationships
CREATE OR REPLACE VIEW v_film_glumac_details AS
SELECT
    f.id as film_id,
    f.naziv as film_naziv,
    g.id as glumac_id,
    g.opis as glumac_opis,
    fg.created_at as relationship_created_at
FROM filmovi f
         JOIN film_glumac fg ON f.id = fg.film_id
         JOIN glumci g ON fg.glumac_id = g.id
ORDER BY f.naziv, g.opis;

-- Add comments to the views for documentation
COMMENT ON VIEW v_film_stats IS 'Statistike filmova s brojem glumaca';
COMMENT ON VIEW v_glumac_stats IS 'Statistike glumaca s brojem filmova';
COMMENT ON VIEW v_film_glumac_details IS 'Detaljni prikaz veza između filmova i glumaca';

-- Final verification summary
SELECT 'DATA VERIFICATION COMPLETED' as verification_status,
       (SELECT COUNT(*) FROM filmovi) as ukupno_filmova,
       (SELECT COUNT(*) FROM glumci) as ukupno_glumaca,
       (SELECT COUNT(*) FROM film_glumac) as ukupno_veza,
       (SELECT COUNT(*) FROM v_film_stats WHERE broj_glumaca = 0) as filmovi_bez_glumaca,
       (SELECT COUNT(*) FROM v_glumac_stats WHERE broj_filmova = 0) as glumci_bez_filmova;