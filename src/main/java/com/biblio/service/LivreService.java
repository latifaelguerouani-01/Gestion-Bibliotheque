package com.biblio.service;
import com.biblio.dao.LivreDAO;
import com.biblio.dao.AuteurDAO;
import com.biblio.entities.Auteur;
import com.biblio.entities.Livre;
import org.hibernate.exception.ConstraintViolationException;
import java.util.*;

public class LivreService {
    private final LivreDAO dao = new LivreDAO();
    private final AuteurDAO auteurDAO = new AuteurDAO();

    public void ajouterLivre(Livre livre, String[] auteurIds) {
        valider(livre);
        String isbn = normaliser(livre.getIsbn());
        if (isbn != null && dao.existeParIsbn(isbn)) throw new IllegalArgumentException("livre.isbn.existe");
        livre.setIsbn(isbn); livre.setAuteurs(chargerAuteurs(auteurIds));
        try { dao.ajouter(livre); } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("livre.isbn.existe");
        }
    }
    public void modifierLivre(Livre livre, String[] auteurIds) {
        if (livre == null || livre.getId() == null || dao.trouverParId(livre.getId()) == null)
            throw new IllegalArgumentException("livre.invalide");
        valider(livre);
        String isbn = normaliser(livre.getIsbn());
        if (isbn != null && dao.existeParIsbnPourAutreLivre(isbn, livre.getId()))
            throw new IllegalArgumentException("livre.isbn.existe");
        livre.setIsbn(isbn); livre.setAuteurs(chargerAuteurs(auteurIds));
        try { dao.modifier(livre); } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("livre.isbn.existe");
        }
    }
    private void valider(Livre l) {
        if (l == null) throw new IllegalArgumentException("livre.obligatoire");
        if (l.getTitre() == null || l.getTitre().isBlank()) throw new IllegalArgumentException("livre.titre.obligatoire");
        l.setTitre(l.getTitre().trim());
    }
    private String normaliser(String s) { return s == null || s.isBlank() ? null : s.trim(); }
    private Set<Auteur> chargerAuteurs(String[] ids) {
        Set<Auteur> set = new HashSet<>();
        if (ids != null) for (String id : ids) try {
            Auteur a = auteurDAO.trouverParId(Long.parseLong(id)); if (a != null) set.add(a);
        } catch (NumberFormatException ignored) { }
        return set;
    }
    public List<Livre> afficherLivres() { return dao.lister(); }
    public List<Livre> rechercherLivres(String q) { return q == null || q.isBlank() ? dao.lister() : dao.rechercher(q.trim()); }
    public Livre rechercherLivre(Long id) { return dao.trouverParId(id); }
    public void supprimerLivre(Long id) { dao.supprimer(id); }
}
