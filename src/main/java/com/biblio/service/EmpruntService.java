package com.biblio.service;

import com.biblio.dao.EmpruntDAO;
import com.biblio.entities.Emprunt;

import java.util.List;

public class EmpruntService {

    private static final int DUREE_MIN = 1;
    private static final int DUREE_MAX = 90;

    private final EmpruntDAO empruntDAO = new EmpruntDAO();

    public void emprunter(Long utilisateurId, Long livreId, int dureeJours) {
        if (utilisateurId == null || utilisateurId <= 0 || livreId == null || livreId <= 0) {
            throw new IllegalArgumentException("emprunt.parametres.invalides");
        }
        if (dureeJours < DUREE_MIN || dureeJours > DUREE_MAX) {
            throw new IllegalArgumentException("emprunt.duree.invalide");
        }
        empruntDAO.emprunter(utilisateurId, livreId, dureeJours);
    }

    public void retourner(Long empruntId, Long utilisateurId) {
        if (empruntId == null || empruntId <= 0 || utilisateurId == null || utilisateurId <= 0) {
            throw new IllegalArgumentException("emprunt.parametres.invalides");
        }
        empruntDAO.retourner(empruntId, utilisateurId);
    }

    public void enregistrerPage(Long empruntId, Long utilisateurId, int page) {
        if (empruntId == null || empruntId <= 0 || utilisateurId == null || utilisateurId <= 0) {
            throw new IllegalArgumentException("emprunt.parametres.invalides");
        }
        if (page < 1 || page > 100000) {
            throw new IllegalArgumentException("emprunt.page.invalide");
        }
        empruntDAO.enregistrerPage(empruntId, utilisateurId, page);
    }

    public int retournerEmpruntsExpires() {
        return empruntDAO.retournerEmpruntsExpires();
    }

    public void ajouterEmprunt(Emprunt emprunt) {
        if (emprunt == null) throw new IllegalArgumentException("Emprunt obligatoire.");
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
        if (id == null || id <= 0) throw new IllegalArgumentException("Identifiant invalide.");
        return empruntDAO.trouverParId(id);
    }

    public void modifierEmprunt(Emprunt emprunt) {
        if (emprunt == null || emprunt.getId() == null) throw new IllegalArgumentException("Emprunt invalide.");
        empruntDAO.modifier(emprunt);
    }

    public void supprimerEmprunt(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Identifiant invalide.");
        empruntDAO.supprimer(id);
    }
}
