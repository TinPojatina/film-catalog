package com.filmcatalog.specifications;

import com.filmcatalog.entities.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * JPA Specifications za dinamičko filtriranje Film entiteta
 */
public class FilmSpecification {

    /**
     * Filtriranje po nazivu filma (case-insensitive, partial match)
     */
    public static Specification<Film> hasNaziv(String naziv) {
        return (root, query, criteriaBuilder) -> {
            if (naziv == null || naziv.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("naziv")),
                    "%" + naziv.toLowerCase().trim() + "%"
            );
        };
    }

    /**
     * Filtriranje po točnom nazivu filma (case-insensitive)
     */
    public static Specification<Film> hasExactNaziv(String naziv) {
        return (root, query, criteriaBuilder) -> {
            if (naziv == null || naziv.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("naziv")),
                    naziv.toLowerCase().trim()
            );
        };
    }

    /**
     * Filtriranje po glumcu - filmovi u kojima nastupa određeni glumac
     */
    public static Specification<Film> hasGlumac(Long glumacId) {
        return (root, query, criteriaBuilder) -> {
            if (glumacId == null) {
                return null;
            }
            Join<Film, Glumac> glumciJoin = root.join("glumci", JoinType.INNER);
            return criteriaBuilder.equal(glumciJoin.get("id"), glumacId);
        };
    }

    /**
     * Filtriranje po opisu glumca - filmovi u kojima nastupa glumac s određenim opisom
     */
    public static Specification<Film> hasGlumacWithOpis(String opisGlumca) {
        return (root, query, criteriaBuilder) -> {
            if (opisGlumca == null || opisGlumca.trim().isEmpty()) {
                return null;
            }
            Join<Film, Glumac> glumciJoin = root.join("glumci", JoinType.INNER);
            return criteriaBuilder.like(
                    criteriaBuilder.lower(glumciJoin.get("opis")),
                    "%" + opisGlumca.toLowerCase().trim() + "%"
            );
        };
    }

    /**
     * Filtriranje po broju glumaca - filmovi s više od određenog broja glumaca
     */
    public static Specification<Film> hasMoreThanGlumci(Integer minGlumci) {
        return (root, query, criteriaBuilder) -> {
            if (minGlumci == null || minGlumci < 0) {
                return null;
            }
            return criteriaBuilder.greaterThan(
                    criteriaBuilder.size(root.get("glumci")),
                    minGlumci
            );
        };
    }

    /**
     * Filtriranje po broju glumaca - filmovi s manje od određenog broja glumaca
     */
    public static Specification<Film> hasLessThanGlumci(Integer maxGlumci) {
        return (root, query, criteriaBuilder) -> {
            if (maxGlumci == null || maxGlumci < 0) {
                return null;
            }
            return criteriaBuilder.lessThan(
                    criteriaBuilder.size(root.get("glumci")),
                    maxGlumci
            );
        };
    }

    /**
     * Filtriranje po datumu kreiranja - filmovi kreirani nakon određenog datuma
     */
    public static Specification<Film> createdAfter(LocalDateTime datum) {
        return (root, query, criteriaBuilder) -> {
            if (datum == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), datum);
        };
    }

    /**
     * Filtriranje po datumu kreiranja - filmovi kreirani prije određenog datuma
     */
    public static Specification<Film> createdBefore(LocalDateTime datum) {
        return (root, query, criteriaBuilder) -> {
            if (datum == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), datum);
        };
    }

    /**
     * Filtriranje po datumu ažuriranja - filmovi ažurirani nakon određenog datuma
     */
    public static Specification<Film> updatedAfter(LocalDateTime datum) {
        return (root, query, criteriaBuilder) -> {
            if (datum == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), datum);
        };
    }

    /**
     * Filmovi bez glumaca
     */
    public static Specification<Film> withoutGlumci() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.size(root.get("glumci")), 0);
    }

    /**
     * Filmovi s glumcima
     */
    public static Specification<Film> withGlumci() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(criteriaBuilder.size(root.get("glumci")), 0);
    }

    /**
     * Kombinirani filter za naziv i glumca
     */
    public static Specification<Film> hasNazivAndGlumac(String naziv, Long glumacId) {
        return Specification.where(hasNaziv(naziv)).and(hasGlumac(glumacId));
    }

    /**
     * Filmovi u određenom vremenskom rasponu
     */
    public static Specification<Film> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return null;
            }
            if (startDate == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate);
            }
            if (endDate == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            }
            return criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
        };
    }

    /**
     * Filtriranje po ID-u filma
     */
    public static Specification<Film> hasId(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }

    /**
     * Filtriranje po listi ID-ova filmova
     */
    public static Specification<Film> hasIdIn(java.util.List<Long> ids) {
        return (root, query, criteriaBuilder) -> {
            if (ids == null || ids.isEmpty()) {
                return null;
            }
            return root.get("id").in(ids);
        };
    }
}