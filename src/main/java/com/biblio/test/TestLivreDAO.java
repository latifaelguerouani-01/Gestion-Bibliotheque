package com.biblio.test;

import com.biblio.dao.LivreDAO;
import com.biblio.entities.Livre;

import java.util.List;

public class TestLivreDAO {

    public static void main(String[] args) {

        LivreDAO livreDAO = new LivreDAO();

        // Ajouter un livre
        Livre livre = new Livre();
        livre.setTitre("Introduction à Java");
        livre.setIsbn("9781234567890");
        livre.setDescription("Un livre pour apprendre Java.");
        livre.setDisponible(true);

        livreDAO.ajouter(livre);

        System.out.println("Livre ajouté avec succès !");

        // Afficher tous les livres
        List<Livre> livres = livreDAO.lister();

        System.out.println("Liste des livres :");

        for (Livre l : livres) {
            System.out.println(
                    l.getId() + " - " +
                            l.getTitre() + " - " +
                            l.getIsbn()
            );
        }
    }
}
