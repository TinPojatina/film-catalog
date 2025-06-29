package com.filmcatalog.services;

import com.filmcatalog.dtos.GlumacDto;
import com.filmcatalog.entities.Film;
import com.filmcatalog.entities.Glumac;
import com.filmcatalog.exception.ResourceNotFoundException;
import com.filmcatalog.repositories.FilmRepository;
import com.filmcatalog.repositories.GlumacRepository;
import com.filmcatalog.specifications.GlumacSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GlumacService {

    private static final Logger logger = LoggerFactory.getLogger(GlumacService.class);

    @Autowired
    private GlumacRepository glumacRepository;

    @Autowired
    private FilmRepository filmRepository;

    /**
     * Kreira novog glumca
     */
    public GlumacDto createGlumac(GlumacDto glumacDto) {
        logger.info("Creating new glumac: {}", glumacDto.getOpis());

        // Provjeri postoji li glumac s istim opisom
        if (glumacRepository.existsByOpisIgnoreCase(glumacDto.getOpis())) {
            throw new IllegalArgumentException("Glumac s opisom '" + glumacDto.getOpis() + "' već postoji");
        }

        Glumac glumac = convertToEntity(glumacDto);
        Glumac savedGlumac = glumacRepository.save(glumac);

        logger.info("Glumac created successfully with ID: {}", savedGlumac.getId());
        return convertToDto(savedGlumac);
    }

    /**
     * Dohvaća glumca po ID-u
     */
    @Transactional(readOnly = true)
    public GlumacDto getGlumacById(Long id) {
        logger.debug("Fetching glumac with ID: {}", id);

        Glumac glumac = glumacRepository.findByIdWithFilmovi(id)
                .orElseThrow(() -> new ResourceNotFoundException("Glumac s ID " + id + " nije pronađen"));

        return convertToDto(glumac);
    }

    /**
     * Dohvaća sve glumce
     */
    @Transactional(readOnly = true)
    public List<GlumacDto> getAllGlumci() {
        logger.debug("Fetching all glumci");

        return glumacRepository.findAllWithFilmovi().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Ažurira postojećeg glumca
     */
    public GlumacDto updateGlumac(Long id, GlumacDto glumacDto) {
        logger.info("Updating glumac with ID: {}", id);

        Glumac existingGlumac = glumacRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Glumac s ID " + id + " nije pronađen"));

        // Provjeri postoji li drugi glumac s istim opisom
        if (!existingGlumac.getOpis().equalsIgnoreCase(glumacDto.getOpis()) &&
                glumacRepository.existsByOpisIgnoreCase(glumacDto.getOpis())) {
            throw new IllegalArgumentException("Glumac s opisom '" + glumacDto.getOpis() + "' već postoji");
        }

        existingGlumac.setOpis(glumacDto.getOpis());
        Glumac updatedGlumac = glumacRepository.save(existingGlumac);

        logger.info("Glumac updated successfully: {}", updatedGlumac.getId());
        return convertToDto(updatedGlumac);
    }

    /**
     * Briše glumca
     */
    public void deleteGlumac(Long id) {
        logger.info("Deleting glumac with ID: {}", id);

        Glumac glumac = glumacRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Glumac s ID " + id + " nije pronađen"));

        // Provjeri ima li glumac povezane filmove
        if (!glumac.getFilmovi().isEmpty()) {
            logger.warn("Attempting to delete glumac {} who has {} associated films", id, glumac.getFilmovi().size());
            // Ukloni vse veze s filmovima prije brisanja
            glumac.getFilmovi().clear();
            glumacRepository.save(glumac);
        }

        glumacRepository.deleteById(id);
        logger.info("Glumac deleted successfully: {}", id);
    }

    /**
     * Dinamičko filtriranje glumaca
     */
    @Transactional(readOnly = true)
    public List<GlumacDto> filterGlumci(String opis, List<Long> filmoviIds) {
        logger.debug("Filtering glumci with opis: {}, filmoviIds: {}", opis, filmoviIds);

        Specification<Glumac> spec = Specification.where(null);

        if (opis != null && !opis.trim().isEmpty()) {
            spec = spec.and(GlumacSpecification.hasOpis(opis));
        }

        if (filmoviIds != null && !filmoviIds.isEmpty()) {
            for (Long filmId : filmoviIds) {
                spec = spec.and(GlumacSpecification.hasFilm(filmId));
            }
        }

        return glumacRepository.findAll(spec).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Paginirana lista glumaca s filtriranjem
     */
    @Transactional(readOnly = true)
    public Page<GlumacDto> getGlumciPaginated(String opis, List<Long> filmoviIds, Pageable pageable) {
        logger.debug("Fetching paginated glumci - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Specification<Glumac> spec = Specification.where(null);

        if (opis != null && !opis.trim().isEmpty()) {
            spec = spec.and(GlumacSpecification.hasOpis(opis));
        }

        if (filmoviIds != null && !filmoviIds.isEmpty()) {
            for (Long filmId : filmoviIds) {
                spec = spec.and(GlumacSpecification.hasFilm(filmId));
            }
        }

        Page<Glumac> glumciPage = glumacRepository.findAll(spec, pageable);
        return glumciPage.map(this::convertToDto);
    }

    /**
     * Dodijeli glumca filmu
     */
    public void assignGlumacToFilm(Long glumacId, Long filmId) {
        logger.info("Assigning glumac {} to film {}", glumacId, filmId);

        Glumac glumac = glumacRepository.findById(glumacId)
                .orElseThrow(() -> new ResourceNotFoundException("Glumac s ID " + glumacId + " nije pronađen"));

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Film s ID " + filmId + " nije pronađen"));

        glumac.addFilm(film);
        glumacRepository.save(glumac);

        logger.info("Glumac {} successfully assigned to film {}", glumacId, filmId);
    }

    /**
     * Ukloni glumca iz filma
     */
    public void removeGlumacFromFilm(Long glumacId, Long filmId) {
        logger.info("Removing glumac {} from film {}", glumacId, filmId);

        Glumac glumac = glumacRepository.findById(glumacId)
                .orElseThrow(() -> new ResourceNotFoundException("Glumac s ID " + glumacId + " nije pronađen"));

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Film s ID " + filmId + " nije pronađen"));

        glumac.removeFilm(film);
        glumacRepository.save(glumac);

        logger.info("Glumac {} successfully removed from film {}", glumacId, filmId);
    }

    /**
     * Statistike o glumcima
     */
    @Transactional(readOnly = true)
    public Long getTotalGlumciCount() {
        return glumacRepository.count();
    }

    @Transactional(readOnly = true)
    public List<GlumacDto> getMostActiveGlumci(int limit) {
        return glumacRepository.findMostActiveGlumci().stream()
                .limit(limit)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GlumacDto> getGlumciWithoutFilmovi() {
        return glumacRepository.findGlumciWithoutFilmovi().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper metode za konverziju
    private GlumacDto convertToDto(Glumac glumac) {
        GlumacDto dto = new GlumacDto();
        dto.setId(glumac.getId());
        dto.setOpis(glumac.getOpis());
        dto.setCreatedAt(glumac.getCreatedAt());
        dto.setUpdatedAt(glumac.getUpdatedAt());

        if (glumac.getFilmovi() != null) {
            dto.setFilmoviIds(glumac.getFilmovi().stream()
                    .map(Film::getId)
                    .collect(Collectors.toSet()));
            dto.setFilmoviNazivi(glumac.getFilmovi().stream()
                    .map(Film::getNaziv)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    private Glumac convertToEntity(GlumacDto dto) {
        Glumac glumac = new Glumac();
        glumac.setOpis(dto.getOpis());
        return glumac;
    }
}