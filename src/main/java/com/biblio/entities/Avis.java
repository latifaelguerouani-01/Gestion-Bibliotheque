package com.biblio.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "avis",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_avis_utilisateur_livre",
                columnNames = {"utilisateur_id", "livre_id"}
        )
)
public class Avis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateurs utilisateur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livre_id", nullable = false)
    private Livre livre;

    @Column(nullable = false)
    private int note;

    @Column(length = 2000)
    private String commentaire;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    public Avis() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateurs getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateurs utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Livre getLivre() { return livre; }
    public void setLivre(Livre livre) { this.livre = livre; }

    public int getNote() { return note; }
    public void setNote(int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException(
                    "La note doit être comprise entre 1 et 5."
            );
        }
        this.note = note;
    }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
