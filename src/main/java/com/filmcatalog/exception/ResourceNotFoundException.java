package com.filmcatalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception za slučajeve kada traženi resurs nije pronađen u bazi podataka.
 * Automatski mapira na HTTP 404 status kod.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    /**
     * Konstruktor s osnovnom porukom
     *
     * @param message Poruka greške
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Konstruktor s porukom i uzrokom greške
     *
     * @param message Poruka greške
     * @param cause Uzrok greške
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Konstruktor s detaljnim informacijama o resursu
     *
     * @param resourceName Naziv resursa (npr. "Film", "Glumac")
     * @param fieldName Naziv polja po kojem se pretražuje (npr. "id", "naziv")
     * @param fieldValue Vrijednost polja koja nije pronađena
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s s %s '%s' nije pronađen", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Statička metoda za kreiranje exception-a za Film entitet
     *
     * @param id ID filma koji nije pronađen
     * @return ResourceNotFoundException s formatiranom porukom
     */
    public static ResourceNotFoundException forFilm(Long id) {
        return new ResourceNotFoundException("Film", "ID", id);
    }

    /**
     * Statička metoda za kreiranje exception-a za Film entitet po nazivu
     *
     * @param naziv Naziv filma koji nije pronađen
     * @return ResourceNotFoundException s formatiranom porukom
     */
    public static ResourceNotFoundException forFilmByNaziv(String naziv) {
        return new ResourceNotFoundException("Film", "naziv", naziv);
    }

    /**
     * Statička metoda za kreiranje exception-a za Glumac entitet
     *
     * @param id ID glumca koji nije pronađen
     * @return ResourceNotFoundException s formatiranom porukom
     */
    public static ResourceNotFoundException forGlumac(Long id) {
        return new ResourceNotFoundException("Glumac", "ID", id);
    }

    /**
     * Statička metoda za kreiranje exception-a za Glumac entitet po opisu
     *
     * @param opis Opis glumca koji nije pronađen
     * @return ResourceNotFoundException s formatiranom porukom
     */
    public static ResourceNotFoundException forGlumacByOpis(String opis) {
        return new ResourceNotFoundException("Glumac", "opis", opis);
    }

    /**
     * Statička metoda za kreiranje exception-a kada više resursa nije pronađeno
     *
     * @param resourceName Naziv resursa
     * @param count Broj resursa koji nije pronađen
     * @return ResourceNotFoundException s formatiranom porukom
     */
    public static ResourceNotFoundException forMultipleResources(String resourceName, int count) {
        return new ResourceNotFoundException(
                String.format("%d %s nije pronađeno", count,
                        count == 1 ? resourceName.toLowerCase() :
                                resourceName.toLowerCase() + (resourceName.endsWith("c") ? "a" : "ova"))
        );
    }

    /**
     * Statička metoda za kreiranje exception-a kada veza između entiteta nije pronađena
     *
     * @param entity1 Prvi entitet
     * @param id1 ID prvog entiteta
     * @param entity2 Drugi entitet
     * @param id2 ID drugog entiteta
     * @return ResourceNotFoundException s formatiranom porukom
     */
    public static ResourceNotFoundException forRelationship(String entity1, Long id1, String entity2, Long id2) {
        return new ResourceNotFoundException(
                String.format("Veza između %s (ID: %d) i %s (ID: %d) nije pronađena",
                        entity1, id1, entity2, id2)
        );
    }

    // Getteri za dodatne informacije
    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    @Override
    public String toString() {
        if (resourceName != null && fieldName != null && fieldValue != null) {
            return String.format("ResourceNotFoundException{resourceName='%s', fieldName='%s', fieldValue='%s', message='%s'}",
                    resourceName, fieldName, fieldValue, getMessage());
        }
        return String.format("ResourceNotFoundException{message='%s'}", getMessage());
    }
}