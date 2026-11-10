package com.biblio.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "emprunts")
public class Emprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateurs utilisateur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livre_id", nullable = false)
    private Livre livre;

    @Column(name = "date_emprunt", nullable = false)
    private LocalDate dateEmprunt;

    @Column(name = "date_retour_prevue", nullable = false)
    private LocalDate dateRetourPrevue;

    @Column(name = "date_retour_effective")
    private LocalDate dateRetourEffective;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEmprunt statut = StatutEmprunt.EN_COURS;


    @Column(name = "derniere_page")
    private Integer dernierePage;

    public Emprunt() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateurs getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateurs utilisateur) { this.utilisateur = utilisateur; }

    public Livre getLivre() { return livre; }
    public void setLivre(Livre livre) { this.livre = livre; }

    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(LocalDate dateEmprunt) { this.dateEmprunt = dateEmprunt; }

    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }

    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public void setDateRetourEffective(LocalDate dateRetourEffective) { this.dateRetourEffective = dateRetourEffective; }

    public Integer getDernierePage() { return dernierePage; }
    public void setDernierePage(Integer dernierePage) { this.dernierePage = dernierePage; }

    public StatutEmprunt getStatut() { return statut; }
    public void setStatut(StatutEmprunt statut) { this.statut = statut; }
}