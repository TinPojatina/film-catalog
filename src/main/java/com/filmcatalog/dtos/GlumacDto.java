package com.filmcatalog.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Schema(description = "DTO za glumac entitet")
public class GlumacDto {

    @Schema(description = "Jedinstveni identifikator glumca", example = "1")
    private Long id;

    @NotBlank(message = "Opis glumca je obavezan")
    @Size(max = 500, message = "Opis ne može biti dulji od 500 znakova")
    @Schema(description = "Opis glumca (ime, prezime, kratka biografija)",
            example = "Robert Downey Jr. - američki glumac poznat po ulozi Iron Mana",
            required = true)
    private String opis;

    @Schema(description = "Lista ID-ova filmova u kojima glumac nastupa", example = "[1, 2, 3]")
    private Set<Long> filmoviIds = new HashSet<>();

    @Schema(description = "Lista naziva filmova u kojima glumac nastupa",
            example = "[\"Iron Man\", \"Avengers\", \"Spider-Man: Homecoming\"]")
    private Set<String> filmoviNazivi = new HashSet<>();

    @Schema(description = "Datum kreiranja glumca")
    private LocalDateTime createdAt;

    @Schema(description = "Datum zadnjeg ažuriranja glumca")
    private LocalDateTime updatedAt;

    // Konstruktori
    public GlumacDto() {}

    public GlumacDto(String opis) {
        this.opis = opis;
    }

    public GlumacDto(Long id, String opis) {
        this.id = id;
        this.opis = opis;
    }

    // Getteri i setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Set<Long> getFilmoviIds() {
        return filmoviIds;
    }

    public void setFilmoviIds(Set<Long> filmoviIds) {
        this.filmoviIds = filmoviIds;
    }

    public Set<String> getFilmoviNazivi() {
        return filmoviNazivi;
    }

    public void setFilmoviNazivi(Set<String> filmoviNazivi) {
        this.filmoviNazivi = filmoviNazivi;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "GlumacDto{" +
                "id=" + id +
                ", opis='" + opis + '\'' +
                ", filmoviIds=" + filmoviIds +
                ", filmoviNazivi=" + filmoviNazivi +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}