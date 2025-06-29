-- Film Catalog Database Schema
-- Flyway migration V1: Create initial tables

-- Create filmovi table
CREATE TABLE filmovi (
                         id BIGSERIAL PRIMARY KEY,
                         naziv VARCHAR(255) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT uk_filmovi_naziv UNIQUE (naziv)
);

-- Create glumci table
CREATE TABLE glumci (
                        id BIGSERIAL PRIMARY KEY,
                        opis VARCHAR(500) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT uk_glumci_opis UNIQUE (opis)
);

-- Create many-to-many relationship table
CREATE TABLE film_glumac (
                             film_id BIGINT NOT NULL,
                             glumac_id BIGINT NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                             PRIMARY KEY (film_id, glumac_id),

                             CONSTRAINT fk_film_glumac_film
                                 FOREIGN KEY (film_id) REFERENCES filmovi(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_film_glumac_glumac
                                 FOREIGN KEY (glumac_id) REFERENCES glumci(id)
                                     ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_filmovi_naziv ON filmovi(naziv);
CREATE INDEX idx_filmovi_created_at ON filmovi(created_at);
CREATE INDEX idx_glumci_opis ON glumci(opis);
CREATE INDEX idx_glumci_created_at ON glumci(created_at);
CREATE INDEX idx_film_glumac_film_id ON film_glumac(film_id);
CREATE INDEX idx_film_glumac_glumac_id ON film_glumac(glumac_id);

-- Add function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for automatic updated_at handling
CREATE TRIGGER trigger_filmovi_updated_at
    BEFORE UPDATE ON filmovi
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_glumci_updated_at
    BEFORE UPDATE ON glumci
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE filmovi IS 'Tablica filmova - sadrži osnovne informacije o filmovima';
COMMENT ON TABLE glumci IS 'Tablica glumaca - sadrži osnovne informacije o glumcima';
COMMENT ON TABLE film_glumac IS 'Many-to-many tablica koja povezuje filmove i glumce';

COMMENT ON COLUMN filmovi.id IS 'Jedinstveni identifikator filma';
COMMENT ON COLUMN filmovi.naziv IS 'Naziv filma - mora biti jedinstven';
COMMENT ON COLUMN filmovi.created_at IS 'Timestamp kreiranja zapisa';
COMMENT ON COLUMN filmovi.updated_at IS 'Timestamp zadnjeg ažuriranja zapisa';

COMMENT ON COLUMN glumci.id IS 'Jedinstveni identifikator glumca';
COMMENT ON COLUMN glumci.opis IS 'Opis glumca (ime, prezime, kratka biografija) - mora biti jedinstven';
COMMENT ON COLUMN glumci.created_at IS 'Timestamp kreiranja zapisa';
COMMENT ON COLUMN glumci.updated_at IS 'Timestamp zadnjeg ažuriranja zapisa';

COMMENT ON COLUMN film_glumac.film_id IS 'Foreign key reference na filmovi.id';
COMMENT ON COLUMN film_glumac.glumac_id IS 'Foreign key reference na glumci.id';
COMMENT ON COLUMN film_glumac.created_at IS 'Timestamp kreiranja veze između filma i glumca';