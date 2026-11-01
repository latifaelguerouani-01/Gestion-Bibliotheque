
package com.biblio.service;

import com.biblio.dao.EmpruntDAO;
import com.biblio.entities.Emprunt;

import java.util.List;

public class EmpruntService {

    private final EmpruntDAO empruntDAO = new EmpruntDAO();

    public void ajouterEmprunt(Emprunt emprunt) {
        if (emprunt == null) {
            throw new IllegalArgumentException("Emprunt obligatoire.");
        }
        empruntDAO.ajouter(emprunt);
    }

    public List<Emprunt> afficherEmprunts() {
        return empruntDAO.lister();
    }

    public List<Emprunt> afficherEmpruntsUtilisateur(Long utilisateurId) {
        if (utilisateurId == null || utilisateurId <= 0) {
            throw new IllegalArgumentException("Identifiant utilisateur invalide.");
        }
        return empruntDAO.listerParUtilisateur(utilisateurId);
    }

    public Emprunt rechercherEmprunt(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identifiant invalide.");
        }
        return empruntDAO.trouverParId(id);
    }

    public void modifierEmprunt(Emprunt emprunt) {
        if (emprunt == null || emprunt.getId() == null) {
            throw new IllegalArgumentException("Emprunt invalide.");
        }
        empruntDAO.modifier(emprunt);
    }

    public void supprimerEmprunt(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identifiant invalide.");
        }
        empruntDAO.supprimer(id);
    }
}
