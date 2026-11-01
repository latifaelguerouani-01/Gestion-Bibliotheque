package com.biblio.service;

import com.biblio.dao.AvisDAO;
import com.biblio.entities.Avis;

import java.util.List;

public class AvisService {

    private final AvisDAO avisDAO = new AvisDAO();

    public void ajouterAvis(Avis avis) {
        if (avis == null) {
            throw new IllegalArgumentException("Avis obligatoire.");
        }
        avisDAO.ajouter(avis);
    }

    public List<Avis> afficherAvis() {
        return avisDAO.lister();
    }

    public Avis rechercherAvis(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identifiant invalide.");
        }
        return avisDAO.trouverParId(id);
    }

    public void modifierAvis(Avis avis) {
        if (avis == null || avis.getId() == null) {
            throw new IllegalArgumentException("Avis invalide.");
        }
        avisDAO.modifier(avis);
    }

    public void supprimerAvis(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identifiant invalide.");
        }
        avisDAO.supprimer(id);
    }
}
