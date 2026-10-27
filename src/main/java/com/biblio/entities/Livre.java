package com.biblio.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "livres")
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(unique = true, length = 30)
    private String isbn;

    @Column(length = 3000)
    private String description;

    @Column
    private String couverture;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(nullable = false)
    private boolean disponible = true;

    @ManyToMany
    @JoinTable(
            name = "livre_auteur",
            joinColumns = @JoinColumn(name = "livre_id"),
            inverseJoinColumns = @JoinColumn(name = "auteur_id")
    )
    private Set<Auteur> auteurs = new HashSet<>();

    public Livre() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCouverture() { return couverture; }
    public void setCouverture(String couverture) {
        this.couverture = couverture;
    }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Set<Auteur> getAuteurs() { return auteurs; }
    public void setAuteurs(Set<Auteur> auteurs) {
        this.auteurs = auteurs;
    }
}
