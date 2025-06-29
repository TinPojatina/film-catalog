package com.filmcatalog.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "glumci")
public class Glumac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Opis glumca je obavezan")
    @Size(max = 500, message = "Opis ne može biti dulji od 500 znakova")
    @Column(name = "opis", nullable = false)
    private String opis;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "glumci", fetch = FetchType.LAZY)
    private Set<Film> filmovi = new HashSet<>();

    // Konstruktori
    public Glumac() {}

    public Glumac(String opis) {
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

    public Set<Film> getFilmovi() {
        return filmovi;
    }

    public void setFilmovi(Set<Film> filmovi) {
        this.filmovi = filmovi;
    }

    // Helper metode za upravljanje vezama
    public void addFilm(Film film) {
        this.filmovi.add(film);
        film.getGlumci().add(this);
    }

    public void removeFilm(Film film) {
        this.filmovi.remove(film);
        film.getGlumci().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Glumac glumac = (Glumac) o;
        return Objects.equals(id, glumac.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Glumac{" +
                "id=" + id +
                ", opis='" + opis + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}