package com.filmcatalog.controllers;

import com.filmcatalog.dtos.FilmDto;
import com.filmcatalog.services.FilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filmovi")
@CrossOrigin(origins = "*")
@Tag(name = "Filmovi", description = "API za upravljanje filmovima")
public class FilmController {

    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    private FilmService filmService;

    @Operation(
            summary = "Kreiranje novog filma",
            description = "Kreira novi film s mogućnošću dodjeljivanja postojećih glumaca preko njihovih ID-ova"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Film uspješno kreiran",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FilmDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci ili film već postoji"),
            @ApiResponse(responseCode = "404", description = "Jedan ili više glumaca nije pronađen"),
            @ApiResponse(responseCode = "500", description = "Greška servera")
    })
    @PostMapping
    public ResponseEntity<FilmDto> createFilm(
            @Parameter(description = "Podaci o novom filmu", required = true)
            @Valid @RequestBody FilmDto filmDto) {

        logger.info("Creating new film: {}", filmDto.getNaziv());
        FilmDto createdFilm = filmService.createFilm(filmDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @Operation(
            summary = "Dohvaćanje filma po ID",
            description = "Vraća detalje filma uključujući sve povezane glumce"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film pronađen"),
            @ApiResponse(responseCode = "404", description = "Film nije pronađen")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FilmDto> getFilmById(
            @Parameter(description = "ID filma", example = "1", required = true)
            @PathVariable Long id) {

        logger.debug("Fetching film with ID: {}", id);
        FilmDto film = filmService.getFilmById(id);

        return ResponseEntity.ok(film);
    }

    @Operation(
            summary = "Lista svih filmova",
            description = "Vraća kompletnu listu svih filmova s povezanim glumcima"
    )
    @GetMapping
    public ResponseEntity<List<FilmDto>> getAllFilmovi() {
        logger.debug("Fetching all films");
        List<FilmDto> filmovi = filmService.getAllFilmovi();

        return ResponseEntity.ok(filmovi);
    }

    @Operation(
            summary = "Filtriranje filmova",
            description = "Vraća listu filmova filtriranu po nazivu i/ili glumcima"
    )
    @GetMapping("/filter")
    public ResponseEntity<List<FilmDto>> filterFilmovi(
            @Parameter(description = "Filtriranje po nazivu filma (case-insensitive)", example = "Avengers")
            @RequestParam(required = false) String naziv,
            @Parameter(description = "Lista ID-ova glumaca za filtriranje", example = "[1,2,3]")
            @RequestParam(required = false) List<Long> glumciIds) {

        logger.debug("Filtering films with naziv: {}, glumciIds: {}", naziv, glumciIds);
        List<FilmDto> filmovi = filmService.filterFilmovi(naziv, glumciIds);

        return ResponseEntity.ok(filmovi);
    }

    @Operation(
            summary = "Paginirana lista filmova s filtriranjem",
            description = "Vraća paginiranu listu filmova s mogućnostima filtriranja i sortiranja"
    )
    @GetMapping("/paginated")
    public ResponseEntity<Page<FilmDto>> getFilmoviPaginated(
            @Parameter(description = "Filtriranje po nazivu filma", example = "Avengers")
            @RequestParam(required = false) String naziv,
            @Parameter(description = "Lista ID-ova glumaca za filtriranje", example = "[1,2,3]")
            @RequestParam(required = false) List<Long> glumciIds,
            @Parameter(description = "Broj stranice (počinje od 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Broj elemenata po stranici", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sortiranje (npr. 'naziv' ili 'createdAt')", example = "naziv")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Smjer sortiranja", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        logger.debug("Fetching paginated films - page: {}, size: {}, sort: {}", page, size, sortBy);
        Page<FilmDto> filmovi = filmService.getFilmoviPaginated(naziv, glumciIds, pageable);

        return ResponseEntity.ok(filmovi);
    }

    @Operation(
            summary = "Ažuriranje filma",
            description = "Ažurira postojeći film s novim podacima"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Film uspješno ažuriran"),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci"),
            @ApiResponse(responseCode = "404", description = "Film ili glumac nije pronađen")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FilmDto> updateFilm(
            @Parameter(description = "ID filma za ažuriranje", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novi podaci filma", required = true)
            @Valid @RequestBody FilmDto filmDto) {

        logger.info("Updating film with ID: {}", id);
        FilmDto updatedFilm = filmService.updateFilm(id, filmDto);

        return ResponseEntity.ok(updatedFilm);
    }

    @Operation(
            summary = "Brisanje filma",
            description = "Briše film iz sustava (uklanja sve veze s glumcima)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Film uspješno obrisan"),
            @ApiResponse(responseCode = "404", description = "Film nije pronađen")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(
            @Parameter(description = "ID filma za brisanje", required = true)
            @PathVariable Long id) {

        logger.info("Deleting film with ID: {}", id);
        filmService.deleteFilm(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Statistike filmova",
            description = "Vraća osnovne statistike o filmovima"
    )
    @GetMapping("/stats")
    public ResponseEntity<FilmStatsDto> getFilmStats() {
        logger.debug("Fetching film statistics");

        Long totalCount = filmService.getTotalFilmoviCount();
        List<FilmDto> latestFilmovi = filmService.getLatestFilmovi(5);

        FilmStatsDto stats = new FilmStatsDto(totalCount, latestFilmovi);
        return ResponseEntity.ok(stats);
    }

    // Inner class za statistike
    public static class FilmStatsDto {
        private Long totalCount;
        private List<FilmDto> latestFilmovi;

        public FilmStatsDto(Long totalCount, List<FilmDto> latestFilmovi) {
            this.totalCount = totalCount;
            this.latestFilmovi = latestFilmovi;
        }

        // Getteri i setteri
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public List<FilmDto> getLatestFilmovi() { return latestFilmovi; }
        public void setLatestFilmovi(List<FilmDto> latestFilmovi) { this.latestFilmovi = latestFilmovi; }
    }
}