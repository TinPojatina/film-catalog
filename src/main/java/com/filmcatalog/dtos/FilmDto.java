package com.filmcatalog.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Schema(description = "DTO za film entitet")
public class FilmDto {

    @Schema(description = "Jedinstveni identifikator filma", example = "1")
    private Long id;

    @NotBlank(message = "Naziv filma je obavezan")
    @Size(max = 255, message = "Naziv ne može biti dulji od 255 znakova")
    @Schema(description = "Naziv filma", example = "Avengers: Endgame", required = true)
    private String naziv;

    @Schema(description = "Lista ID-ova glumaca koji nastupaju u filmu", example = "[1, 2, 3]")
    private Set<Long> glumciIds = new HashSet<>();

    @Schema(description = "Lista opisa glumaca koji nastupaju u filmu",
            example = "[\"Robert Downey Jr.\", \"Chris Evans\", \"Scarlett Johansson\"]")
    private Set<String> glumciOpisi = new HashSet<>();

    @Schema(description = "Datum kreiranja filma")
    private LocalDateTime createdAt;

    @Schema(description = "Datum zadnjeg ažuriranja filma")
    private LocalDateTime updatedAt;

    // Konstruktori
    public FilmDto() {}

    public FilmDto(String naziv) {
        this.naziv = naziv;
    }

    public FilmDto(Long id, String naziv) {
        this.id = id;
        this.naziv = naziv;
    }

    // Getteri i setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Set<Long> getGlumciIds() {
        return glumciIds;
    }

    public void setGlumciIds(Set<Long> glumciIds) {
        this.glumciIds = glumciIds;
    }

    public Set<String> getGlumciOpisi() {
        return glumciOpisi;
    }

    public void setGlumciOpisi(Set<String> glumciOpisi) {
        this.glumciOpisi = glumciOpisi;
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
        return "FilmDto{" +
                "id=" + id +
                ", naziv='" + naziv + '\'' +
                ", glumciIds=" + glumciIds +
                ", glumciOpisi=" + glumciOpisi +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}