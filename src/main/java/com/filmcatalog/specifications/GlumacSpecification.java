package com.filmcatalog.specifications;

import com.filmcatalog.entities.*;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.time.LocalDateTime;

public class GlumacSpecification {

    /**
     * Filtriranje po opisu glumca (case-insensitive, partial match)
     */
    public static Specification<Glumac> hasOpis(String opis) {
        return (root, query, criteriaBuilder) -> {
            if (opis == null || opis.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("opis")),
                    "%" + opis.toLowerCase().trim() + "%"
            );
        };
    }

    /**
     * Filtriranje po točnom opisu glumca (case-insensitive)
     */
    public static Specification<Glumac> hasExactOpis(String opis) {
        return (root, query, criteriaBuilder) -> {
            if (opis == null || opis.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("opis")),
                    opis.toLowerCase().trim()
            );
        };
    }

    /**
     *  glumci koji nastupaju u određenom filmu
     */
    public static Specification<Glumac> hasFilm(Long filmId) {
        return (root, query, criteriaBuilder) -> {
            if (filmId == null) {
                return null;
            }
            Join<Glumac, Film> filmoviJoin = root.join("filmovi", JoinType.INNER);
            return criteriaBuilder.equal(filmoviJoin.get("id"), filmId);
        };
    }

    /**
     *  glumci koji nastupaju u filmu s određenim nazivom
     */
    public static Specification<Glumac> hasFilmWithNaziv(String nazivFilma) {
        return (root, query, criteriaBuilder) -> {
            if (nazivFilma == null || nazivFilma.trim().isEmpty()) {
                return null;
            }
            Join<Glumac, Film> filmoviJoin = root.join("filmovi", JoinType.INNER);
            return criteriaBuilder.like(
                    criteriaBuilder.lower(filmoviJoin.get("naziv")),
                    "%" + nazivFilma.toLowerCase().trim() + "%"
            );
        };
    }

    /**
     *  glumci s više od određenog broja filmova
     */
    public static Specification<Glumac> hasMoreThanFilmovi(Integer minFilmovi) {
        return (root, query, criteriaBuilder) -> {
            if (minFilmovi == null || minFilmovi < 0) {
                return null;
            }
            return criteriaBuilder.greaterThan(
                    criteriaBuilder.size(root.get("filmovi")),
                    minFilmovi
            );
        };
    }

    /**
     *  glumci s manje od određenog broja filmova
     */
    public static Specification<Glumac> hasLessThanFilmovi(Integer maxFilmovi) {
        return (root, query, criteriaBuilder) -> {
            if (maxFilmovi == null || maxFilmovi < 0) {
                return null;
            }
            return criteriaBuilder.lessThan(
                    criteriaBuilder.size(root.get("filmovi")),
                    maxFilmovi
            );
        };
    }

    /**
     * Filtriranje po točnom broju filmova
     */
    public static Specification<Glumac> hasExactFilmovi(Integer brojFilmova) {
        return (root, query, criteriaBuilder) -> {
            if (brojFilmova == null || brojFilmova < 0) {
                return null;
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.size(root.get("filmovi")),
                    brojFilmova
            );
        };
    }

    /**
     *  glumci kreirani nakon određenog datuma
     */
    public static Specification<Glumac> createdAfter(LocalDateTime datum) {
        return (root, query, criteriaBuilder) -> {
            if (datum == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), datum);
        };
    }

    /**
     *  glumci kreirani prije određenog datuma
     */
    public static Specification<Glumac> createdBefore(LocalDateTime datum) {
        return (root, query, criteriaBuilder) -> {
            if (datum == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), datum);
        };
    }

    /**
     *  glumci ažurirani nakon određenog datuma
     */
    public static Specification<Glumac> updatedAfter(LocalDateTime datum) {
        return (root, query, criteriaBuilder) -> {
            if (datum == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), datum);
        };
    }

    /**
     * Glumci bez filmova
     */
    public static Specification<Glumac> withoutFilmovi() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.size(root.get("filmovi")), 0);
    }

    /**
     * Glumci s filmovima
     */
    public static Specification<Glumac> withFilmovi() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(criteriaBuilder.size(root.get("filmovi")), 0);
    }

    /**
     * Kombinirani filter za opis i film
     */
    public static Specification<Glumac> hasOpisAndFilm(String opis, Long filmId) {
        return Specification.where(hasOpis(opis)).and(hasFilm(filmId));
    }

    /**
     * Glumci u određenom vremenskom rasponu kreiranja
     */
    public static Specification<Glumac> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
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
     * Filtriranje po ID-u glumca
     */
    public static Specification<Glumac> hasId(Long id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }

    /**
     * Filtriranje po listi ID-ova glumaca
     */
    public static Specification<Glumac> hasIdIn(java.util.List<Long> ids) {
        return (root, query, criteriaBuilder) -> {
            if (ids == null || ids.isEmpty()) {
                return null;
            }
            return root.get("id").in(ids);
        };
    }

    /**
     * Pretraga po ključnim riječima u opisu
     */
    public static Specification<Glumac> searchByKeywords(String keywords) {
        return (root, query, criteriaBuilder) -> {
            if (keywords == null || keywords.trim().isEmpty()) {
                return null;
            }

            String[] words = keywords.toLowerCase().trim().split("\\s+");
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            for (String word : words) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("opis")),
                        "%" + word + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    /**
     * Najaktivniji glumci (sortiranje po broju filmova)
     */
    public static Specification<Glumac> mostActive() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(criteriaBuilder.size(root.get("filmovi"))));
            return null;
        };
    }
}