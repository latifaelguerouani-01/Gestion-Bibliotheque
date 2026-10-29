package com.biblio.entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "auteurs")
public class Auteur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 2000)
    private String biographie;

    @OneToMany(mappedBy = "auteur", fetch = FetchType.LAZY)
    private List<Livre> listeLivres;

    public Auteur() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getBiographie() { return biographie; }
    public void setBiographie(String biographie) { this.biographie = biographie; }
    public List<Livre> getListeLivres() { return listeLivres; }
    public void setListeLivres(List<Livre> listeLivres) { this.listeLivres = listeLivres; }
}