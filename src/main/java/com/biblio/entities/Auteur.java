package com.biblio.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auteurs")
public class Auteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String prenom;

    @Column(length = 2000)
    private String biographie;

    @ManyToMany(mappedBy = "auteurs")
    private Set<Livre> livres = new HashSet<>();

    public Auteur() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getBiographie() { return biographie; }
    public void setBiographie(String biographie) {
        this.biographie = biographie;
    }

    public Set<Livre> getLivres() { return livres; }
    public void setLivres(Set<Livre> livres) {
        this.livres = livres;
    }
}
