package com.filmcatalog.controllers;

import com.filmcatalog.dtos.GlumacDto;
import com.filmcatalog.services.GlumacService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/glumci")
@CrossOrigin(origins = "*")
@Tag(name = "Glumci", description = "API za upravljanje glumcima")
public class GlumacController {

    private static final Logger logger = LoggerFactory.getLogger(GlumacController.class);

    @Autowired
    private GlumacService glumacService;

    @Operation(
            summary = "Kreiranje novog glumca",
            description = "Kreira novog glumca u sustavu"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Glumac uspješno kreiran",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GlumacDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci ili glumac već postoji"),
            @ApiResponse(responseCode = "500", description = "Greška servera")
    })
    @PostMapping
    public ResponseEntity<GlumacDto> createGlumac(
            @Parameter(description = "Podaci o novom glumcu", required = true)
            @Valid @RequestBody GlumacDto glumacDto) {

        logger.info("Creating new glumac: {}", glumacDto.getOpis());
        GlumacDto createdGlumac = glumacService.createGlumac(glumacDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdGlumac);
    }

    @Operation(
            summary = "Dohvaćanje glumca po ID",
            description = "Vraća detalje glumca uključujući sve povezane filmove"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Glumac pronađen"),
            @ApiResponse(responseCode = "404", description = "Glumac nije pronađen")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GlumacDto> getGlumacById(
            @Parameter(description = "ID glumca", example = "1", required = true)
            @PathVariable Long id) {

        logger.debug("Fetching glumac with ID: {}", id);
        GlumacDto glumac = glumacService.getGlumacById(id);

        return ResponseEntity.ok(glumac);
    }

    @Operation(
            summary = "Lista svih glumaca",
            description = "Vraća kompletnu listu svih glumaca s povezanim filmovima"
    )
    @GetMapping
    public ResponseEntity<List<GlumacDto>> getAllGlumci() {
        logger.debug("Fetching all glumci");
        List<GlumacDto> glumci = glumacService.getAllGlumci();

        return ResponseEntity.ok(glumci);
    }

    @Operation(
            summary = "Filtriranje glumaca",
            description = "Vraća listu glumaca filtriranu po opisu i/ili filmovima"
    )
    @GetMapping("/filter")
    public ResponseEntity<List<GlumacDto>> filterGlumci(
            @Parameter(description = "Filtriranje po opisu glumca (case-insensitive)", example = "Robert")
            @RequestParam(required = false) String opis,
            @Parameter(description = "Lista ID-ova filmova za filtriranje", example = "[1,2,3]")
            @RequestParam(required = false) List<Long> filmoviIds) {

        logger.debug("Filtering glumci with opis: {}, filmoviIds: {}", opis, filmoviIds);
        List<GlumacDto> glumci = glumacService.filterGlumci(opis, filmoviIds);

        return ResponseEntity.ok(glumci);
    }

    @Operation(
            summary = "Paginirana lista glumaca s filtriranjem",
            description = "Vraća paginiranu listu glumaca s mogućnostima filtriranja i sortiranja"
    )
    @GetMapping("/paginated")
    public ResponseEntity<Page<GlumacDto>> getGlumciPaginated(
            @Parameter(description = "Filtriranje po opisu glumca", example = "American")
            @RequestParam(required = false) String opis,
            @Parameter(description = "Lista ID-ova filmova za filtriranje", example = "[1,2,3]")
            @RequestParam(required = false) List<Long> filmoviIds,
            @Parameter(description = "Broj stranice (počinje od 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Broj elemenata po stranici", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sortiranje (npr. 'opis' ili 'createdAt')", example = "opis")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Smjer sortiranja", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        logger.debug("Fetching paginated glumci - page: {}, size: {}, sort: {}", page, size, sortBy);
        Page<GlumacDto> glumci = glumacService.getGlumciPaginated(opis, filmoviIds, pageable);

        return ResponseEntity.ok(glumci);
    }

    @Operation(
            summary = "Ažuriranje glumca",
            description = "Ažurira postojećeg glumca s novim podacima"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Glumac uspješno ažuriran"),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci"),
            @ApiResponse(responseCode = "404", description = "Glumac nije pronađen")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GlumacDto> updateGlumac(
            @Parameter(description = "ID glumca za ažuriranje", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novi podaci glumca", required = true)
            @Valid @RequestBody GlumacDto glumacDto) {

        logger.info("Updating glumac with ID: {}", id);
        GlumacDto updatedGlumac = glumacService.updateGlumac(id, glumacDto);

        return ResponseEntity.ok(updatedGlumac);
    }

    @Operation(
            summary = "Brisanje glumca",
            description = "Briše glumca iz sustava (uklanja sve veze s filmovima)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Glumac uspješno obrisan"),
            @ApiResponse(responseCode = "404", description = "Glumac nije pronađen")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGlumac(
            @Parameter(description = "ID glumca za brisanje", required = true)
            @PathVariable Long id) {

        logger.info("Deleting glumac with ID: {}", id);
        glumacService.deleteGlumac(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Dodijeli glumca filmu",
            description = "Stvara vezu između postojećeg glumca i filma"
    )
    @PostMapping("/{glumacId}/filmovi/{filmId}")
    public ResponseEntity<Void> assignGlumacToFilm(
            @Parameter(description = "ID glumca", required = true)
            @PathVariable Long glumacId,
            @Parameter(description = "ID filma", required = true)
            @PathVariable Long filmId) {

        logger.info("Assigning glumac {} to film {}", glumacId, filmId);
        glumacService.assignGlumacToFilm(glumacId, filmId);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Ukloni glumca iz filma",
            description = "Uklanja vezu između glumca i filma"
    )
    @DeleteMapping("/{glumacId}/filmovi/{filmId}")
    public ResponseEntity<Void> removeGlumacFromFilm(
            @Parameter(description = "ID glumca", required = true)
            @PathVariable Long glumacId,
            @Parameter(description = "ID filma", required = true)
            @PathVariable Long filmId) {

        logger.info("Removing glumac {} from film {}", glumacId, filmId);
        glumacService.removeGlumacFromFilm(glumacId, filmId);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Najaktivniji glumci",
            description = "Vraća listu glumaca sortiranih po broju filmova (silazno)"
    )
    @GetMapping("/most-active")
    public ResponseEntity<List<GlumacDto>> getMostActiveGlumci(
            @Parameter(description = "Broj glumaca za vratiti", example = "10")
            @RequestParam(defaultValue = "10") int limit) {

        logger.debug("Fetching most active glumci, limit: {}", limit);
        List<GlumacDto> activeGlumci = glumacService.getMostActiveGlumci(limit);

        return ResponseEntity.ok(activeGlumci);
    }

    @Operation(
            summary = "Glumci bez filmova",
            description = "Vraća listu glumaca koji nisu povezani ni s jednim filmom"
    )
    @GetMapping("/without-films")
    public ResponseEntity<List<GlumacDto>> getGlumciWithoutFilmovi() {
        logger.debug("Fetching glumci without films");
        List<GlumacDto> glumciWithoutFilms = glumacService.getGlumciWithoutFilmovi();

        return ResponseEntity.ok(glumciWithoutFilms);
    }

    @Operation(
            summary = "Statistike glumaca",
            description = "Vraća osnovne statistike o glumcima"
    )
    @GetMapping("/stats")
    public ResponseEntity<GlumacStatsDto> getGlumacStats() {
        logger.debug("Fetching glumac statistics");

        Long totalCount = glumacService.getTotalGlumciCount();
        List<GlumacDto> mostActive = glumacService.getMostActiveGlumci(5);
        List<GlumacDto> withoutFilms = glumacService.getGlumciWithoutFilmovi();

        GlumacStatsDto stats = new GlumacStatsDto(totalCount, mostActive, withoutFilms.size());
        return ResponseEntity.ok(stats);
    }

    // Inner class za statistike
    public static class GlumacStatsDto {
        private Long totalCount;
        private List<GlumacDto> mostActiveGlumci;
        private Integer glumciWithoutFilmsCount;

        public GlumacStatsDto(Long totalCount, List<GlumacDto> mostActiveGlumci, Integer glumciWithoutFilmsCount) {
            this.totalCount = totalCount;
            this.mostActiveGlumci = mostActiveGlumci;
            this.glumciWithoutFilmsCount = glumciWithoutFilmsCount;
        }

        // Getteri i setteri
        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
        public List<GlumacDto> getMostActiveGlumci() { return mostActiveGlumci; }
        public void setMostActiveGlumci(List<GlumacDto> mostActiveGlumci) { this.mostActiveGlumci = mostActiveGlumci; }
        public Integer getGlumciWithoutFilmsCount() { return glumciWithoutFilmsCount; }
        public void setGlumciWithoutFilmsCount(Integer glumciWithoutFilmsCount) { this.glumciWithoutFilmsCount = glumciWithoutFilmsCount; }
    }
}