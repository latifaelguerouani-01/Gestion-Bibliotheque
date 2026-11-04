package com.biblio.dao;

import com.biblio.entities.Emprunt;
import com.biblio.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Consumer;

public class EmpruntDAO {

    public void ajouter(Emprunt emprunt) {
        execute(s -> s.persist(emprunt));
    }

    public List<Emprunt> lister() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "SELECT e FROM Emprunt e JOIN FETCH e.livre JOIN FETCH e.utilisateur ORDER BY e.dateEmprunt DESC",
                    Emprunt.class
            ).list();
        }
    }

    public List<Emprunt> listerParUtilisateur(Long utilisateurId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "SELECT e FROM Emprunt e JOIN FETCH e.livre JOIN FETCH e.utilisateur WHERE e.utilisateur.id = :uid ORDER BY e.dateEmprunt DESC",
                    Emprunt.class
            ).setParameter("uid", utilisateurId).list();
        }
    }

    public Emprunt trouverParId(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "SELECT e FROM Emprunt e JOIN FETCH e.livre JOIN FETCH e.utilisateur WHERE e.id = :id",
                    Emprunt.class
            ).setParameter("id", id).uniqueResult();
        }
    }

    public void modifier(Emprunt emprunt) {
        execute(s -> s.merge(emprunt));
    }

    public void supprimer(Long id) {
        execute(s -> {
            Emprunt emprunt = s.get(Emprunt.class, id);
            if (emprunt != null) {
                s.remove(emprunt);
            }
        });
    }

    private void execute(Consumer<Session> action) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            action.accept(s);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}