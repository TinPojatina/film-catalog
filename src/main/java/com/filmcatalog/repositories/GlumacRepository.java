package com.filmcatalog.repositories;

import com.filmcatalog.entities.Glumac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GlumacRepository extends JpaRepository<Glumac, Long>, JpaSpecificationExecutor<Glumac> {

    /**
     * Pronalazi glumce po opisu (case-insensitive)
     */
    @Query("SELECT g FROM Glumac g WHERE LOWER(g.opis) LIKE LOWER(CONCAT('%', :opis, '%'))")
    List<Glumac> findByOpisContainingIgnoreCase(@Param("opis") String opis);

    /**
     * Pronalazi glumce koji nastupaju u određenom filmu
     */
    @Query("SELECT g FROM Glumac g JOIN g.filmovi f WHERE f.id = :filmId")
    List<Glumac> findByFilmId(@Param("filmId") Long filmId);

    /**
     * Pronalazi glumca s učitanim filmovima
     */
    @Query("SELECT g FROM Glumac g LEFT JOIN FETCH g.filmovi WHERE g.id = :id")
    Optional<Glumac> findByIdWithFilmovi(@Param("id") Long id);

    /**
     * Pronalazi sve glumce s učitanim filmovima
     */
    @Query("SELECT DISTINCT g FROM Glumac g LEFT JOIN FETCH g.filmovi")
    List<Glumac> findAllWithFilmovi();

    /**
     * Broji koliko glumaca sadrži određenu riječ u opisu
     */
    @Query("SELECT COUNT(g) FROM Glumac g WHERE LOWER(g.opis) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Long countByOpisContaining(@Param("keyword") String keyword);

    /**
     * Pronalazi glumce koji nastupaju u više od određenog broja filmova
     */
    @Query("SELECT g FROM Glumac g WHERE SIZE(g.filmovi) > :minFilmovi")
    List<Glumac> findGlumciWithMoreThanFilmovi(@Param("minFilmovi") int minFilmovi);

    /**
     * Pronalazi najaktivnije glumce (oni s najviše filmova)
     */
    @Query("SELECT g FROM Glumac g ORDER BY SIZE(g.filmovi) DESC")
    List<Glumac> findMostActiveGlumci();

    /**
     * Pronalazi glumce koji nisu povezani ni s jednim filmom
     */
    @Query("SELECT g FROM Glumac g WHERE SIZE(g.filmovi) = 0")
    List<Glumac> findGlumciWithoutFilmovi();

    /**
     * Provjera postojanja glumca po opisu
     */
    boolean existsByOpisIgnoreCase(@Param("opis") String opis);

    /**
     * Pronalazi glumce kreirane nakon određenog datuma
     */
    @Query("SELECT g FROM Glumac g WHERE g.createdAt > :fromDate ORDER BY g.createdAt DESC")
    List<Glumac> findRecentGlumci(@Param("fromDate") java.time.LocalDateTime fromDate);
}