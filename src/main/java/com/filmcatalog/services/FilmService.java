package com.filmcatalog.services;

import com.filmcatalog.dtos.FilmDto;
import com.filmcatalog.entities.Film;
import com.filmcatalog.entities.Glumac;
import com.filmcatalog.exception.ResourceNotFoundException;
import com.filmcatalog.repositories.FilmRepository;
import com.filmcatalog.repositories.GlumacRepository;
import com.filmcatalog.specifications.FilmSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class FilmService {

    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private GlumacRepository glumacRepository;

    /**
     * Kreira novi film
     */
    public FilmDto createFilm(FilmDto filmDto) {
        logger.info("Creating new film: {}", filmDto.getNaziv());

        // Provjeri postoji li film s istim nazivom
        if (filmRepository.existsByNazivIgnoreCase(filmDto.getNaziv())) {
            throw new IllegalArgumentException("Film s nazivom '" + filmDto.getNaziv() + "' već postoji");
        }

        Film film = convertToEntity(filmDto);

        // Dodijeli postojeće glumce
        if (filmDto.getGlumciIds() != null && !filmDto.getGlumciIds().isEmpty()) {
            Set<Glumac> glumci = new HashSet<>(glumacRepository.findAllById(filmDto.getGlumciIds()));

            // Provjeri postoje li svi glumci
            if (glumci.size() != filmDto.getGlumciIds().size()) {
                throw new ResourceNotFoundException("Jedan ili više glumaca nije pronađen");
            }

            film.setGlumci(glumci);
        }

        Film savedFilm = filmRepository.save(film);
        logger.info("Film created successfully with ID: {}", savedFilm.getId());

        return convertToDto(savedFilm);
    }

    /**
     * Dohvaća film po ID-u
     */
    @Transactional(readOnly = true)
    public FilmDto getFilmById(Long id) {
        logger.debug("Fetching film with ID: {}", id);

        Film film = filmRepository.findByIdWithGlumci(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film s ID " + id + " nije pronađen"));

        return convertToDto(film);
    }

    /**
     * Dohvaća sve filmove
     */
    @Transactional(readOnly = true)
    public List<FilmDto> getAllFilmovi() {
        logger.debug("Fetching all films");

        return filmRepository.findAllWithGlumci().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Ažurira postojeći film
     */
    public FilmDto updateFilm(Long id, FilmDto filmDto) {
        logger.info("Updating film with ID: {}", id);

        Film existingFilm = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film s ID " + id + " nije pronađen"));

        // Provjeri postoji li drugi film s istim nazivom
        if (!existingFilm.getNaziv().equalsIgnoreCase(filmDto.getNaziv()) &&
                filmRepository.existsByNazivIgnoreCase(filmDto.getNaziv())) {
            throw new IllegalArgumentException("Film s nazivom '" + filmDto.getNaziv() + "' već postoji");
        }

        existingFilm.setNaziv(filmDto.getNaziv());

        // Ažuriraj glumce
        if (filmDto.getGlumciIds() != null) {
            Set<Glumac> newGlumci = new HashSet<>(glumacRepository.findAllById(filmDto.getGlumciIds()));

            if (newGlumci.size() != filmDto.getGlumciIds().size()) {
                throw new ResourceNotFoundException("Jedan ili više glumaca nije pronađen");
            }

            existingFilm.setGlumci(newGlumci);
        }

        Film updatedFilm = filmRepository.save(existingFilm);
        logger.info("Film updated successfully: {}", updatedFilm.getId());

        return convertToDto(updatedFilm);
    }

    /**
     * Briše film
     */
    public void deleteFilm(Long id) {
        logger.info("Deleting film with ID: {}", id);

        if (!filmRepository.existsById(id)) {
            throw new ResourceNotFoundException("Film s ID " + id + " nije pronađen");
        }

        filmRepository.deleteById(id);
        logger.info("Film deleted successfully: {}", id);
    }

    /**
     * Dinamičko filtriranje filmova - FIXED VERSION
     */
    @Transactional(readOnly = true)
    public List<FilmDto> filterFilmovi(String naziv, List<Long> glumciIds) {
        logger.debug("Filtering films with naziv: {}, glumciIds: {}", naziv, glumciIds);

        // Start with null specification
        Specification<Film> spec = null;

        // Add naziv filter if provided
        if (naziv != null && !naziv.trim().isEmpty()) {
            spec = FilmSpecification.hasNaziv(naziv);
        }

        // Add glumac filters if provided - PROPER NULL HANDLING
        if (glumciIds != null && !glumciIds.isEmpty()) {
            for (Long glumacId : glumciIds) {
                Specification<Film> glumacSpec = FilmSpecification.hasGlumac(glumacId);
                spec = (spec == null) ? glumacSpec : spec.and(glumacSpec);
            }
        }

        // Execute query with proper null check
        List<Film> filmovi = (spec != null) ?
                filmRepository.findAll(spec) :
                filmRepository.findAll();

        return filmovi.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Paginirana lista filmova s filtriranjem - FIXED VERSION
     */
    @Transactional(readOnly = true)
    public Page<FilmDto> getFilmoviPaginated(String naziv, List<Long> glumciIds, Pageable pageable) {
        logger.debug("Fetching paginated films - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        // Start with null specification
        Specification<Film> spec = null;

        // Add naziv filter if provided
        if (naziv != null && !naziv.trim().isEmpty()) {
            spec = FilmSpecification.hasNaziv(naziv);
        }

        // Add glumac filters if provided - PROPER NULL HANDLING
        if (glumciIds != null && !glumciIds.isEmpty()) {
            for (Long glumacId : glumciIds) {
                Specification<Film> glumacSpec = FilmSpecification.hasGlumac(glumacId);
                spec = (spec == null) ? glumacSpec : spec.and(glumacSpec);
            }
        }

        // Execute paginated query with proper null check
        Page<Film> filmoviPage = (spec != null) ?
                filmRepository.findAll(spec, pageable) :
                filmRepository.findAll(pageable);

        return filmoviPage.map(this::convertToDto);
    }

    /**
     * Statistike o filmovima
     */
    @Transactional(readOnly = true)
    public Long getTotalFilmoviCount() {
        return filmRepository.count();
    }

    @Transactional(readOnly = true)
    public List<FilmDto> getLatestFilmovi(int limit) {
        return filmRepository.findLatestFilmovi().stream()
                .limit(limit)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper metode za konverziju
    private FilmDto convertToDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setNaziv(film.getNaziv());
        dto.setCreatedAt(film.getCreatedAt());
        dto.setUpdatedAt(film.getUpdatedAt());

        if (film.getGlumci() != null) {
            dto.setGlumciIds(film.getGlumci().stream()
                    .map(Glumac::getId)
                    .collect(Collectors.toSet()));
            dto.setGlumciOpisi(film.getGlumci().stream()
                    .map(Glumac::getOpis)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    private Film convertToEntity(FilmDto dto) {
        Film film = new Film();
        film.setNaziv(dto.getNaziv());
        return film;
    }
}