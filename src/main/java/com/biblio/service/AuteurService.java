package com.biblio.service;
import com.biblio.dao.AuteurDAO;
import com.biblio.entities.Auteur;
import java.util.List;

public class AuteurService {
    private final AuteurDAO dao = new AuteurDAO();
    public void ajouterAuteur(Auteur a) { valider(a); dao.ajouter(a); }
    public void modifierAuteur(Auteur a) {
        if (a == null || a.getId() == null || dao.trouverParId(a.getId()) == null) throw new IllegalArgumentException("auteur.invalide");
        valider(a); dao.modifier(a);
    }
    private void valider(Auteur a) {
        if (a == null || a.getNom() == null || a.getNom().isBlank()) throw new IllegalArgumentException("auteur.nom.obligatoire");
        a.setNom(a.getNom().trim()); if (a.getPrenom() != null) a.setPrenom(a.getPrenom().trim());
    }
    public List<Auteur> afficherAuteurs() { return dao.lister(); }
    public Auteur rechercherAuteur(Long id) { return dao.trouverParId(id); }
    public void supprimerAuteur(Long id) { dao.supprimer(id); }
}
