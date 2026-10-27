package com.biblio.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateurs")
public class Utilisateurs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(nullable = false, length = 20)
    private String role = "USER";

    @Column(name = "photo_profil")
    private String photoProfil;

    @Column(nullable = false)
    private boolean actif = true;

    public Utilisateurs() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) {
        this.photoProfil = photoProfil;
    }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
