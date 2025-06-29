package com.filmcatalog.repositories;

import com.filmcatalog.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film> {

    /**
     * Pronalazi filmove po nazivu (case-insensitive)
     */
    @Query("SELECT f FROM Film f WHERE LOWER(f.naziv) LIKE LOWER(CONCAT('%', :naziv, '%'))")
    List<Film> findByNazivContainingIgnoreCase(@Param("naziv") String naziv);

    /**
     * Pronalazi filmove u kojima nastupa određeni glumac
     */
    @Query("SELECT f FROM Film f JOIN f.glumci g WHERE g.id = :glumacId")
    List<Film> findByGlumacId(@Param("glumacId") Long glumacId);

    /**
     * Pronalazi film s učitanim glumcima (JOIN FETCH za izbjegavanje N+1 problema)
     */
    @Query("SELECT f FROM Film f LEFT JOIN FETCH f.glumci WHERE f.id = :id")
    Optional<Film> findByIdWithGlumci(@Param("id") Long id);

    /**
     * Pronalazi sve filmove s učitanim glumcima
     */
    @Query("SELECT DISTINCT f FROM Film f LEFT JOIN FETCH f.glumci")
    List<Film> findAllWithGlumci();

    /**
     * Broji koliko filmova sadrži određenu riječ u nazivu
     */
    @Query("SELECT COUNT(f) FROM Film f WHERE LOWER(f.naziv) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Long countByNazivContaining(@Param("keyword") String keyword);

    /**
     * Pronalazi filmove koji imaju više od određenog broja glumaca
     */
    @Query("SELECT f FROM Film f WHERE SIZE(f.glumci) > :minGlumci")
    List<Film> findFilmoviWithMoreThanGlumci(@Param("minGlumci") int minGlumci);

    /**
     * Pronalazi najnovije filmove (po datumu kreiranja)
     */
    @Query("SELECT f FROM Film f ORDER BY f.createdAt DESC")
    List<Film> findLatestFilmovi();

    /**
     * Provjera postojanja filma po nazivu
     */
    boolean existsByNazivIgnoreCase(String naziv);

    /**
     * Brisanje filmova starijih od određenog datuma
     */
    @Query("DELETE FROM Film f WHERE f.createdAt < :cutoffDate")
    void deleteOldFilmovi(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}