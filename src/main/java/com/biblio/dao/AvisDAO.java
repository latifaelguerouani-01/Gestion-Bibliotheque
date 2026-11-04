package com.biblio.dao;

import com.biblio.entities.Avis;
import com.biblio.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Consumer;

public class AvisDAO {

    public void ajouter(Avis avis) {
        execute(s -> s.persist(avis));
    }

    public List<Avis> lister() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "SELECT a FROM Avis a JOIN FETCH a.utilisateur JOIN FETCH a.livre ORDER BY a.dateCreation DESC",
                    Avis.class
            ).list();
        }
    }

    public List<Avis> listerParLivre(Long livreId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "SELECT a FROM Avis a JOIN FETCH a.utilisateur WHERE a.livre.id = :livreId ORDER BY a.dateCreation DESC",
                    Avis.class
            ).setParameter("livreId", livreId).list();
        }
    }

    public Avis trouverParId(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "SELECT a FROM Avis a JOIN FETCH a.utilisateur JOIN FETCH a.livre WHERE a.id = :id",
                    Avis.class
            ).setParameter("id", id).uniqueResult();
        }
    }

    public boolean existeAvisUtilisateurLivre(Long utilisateurId, Long livreId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Long count = s.createQuery(
                            "SELECT COUNT(a) FROM Avis a WHERE a.utilisateur.id = :uid AND a.livre.id = :lid",
                            Long.class
                    ).setParameter("uid", utilisateurId)
                    .setParameter("lid", livreId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public void modifier(Avis avis) {
        execute(s -> s.merge(avis));
    }

    public void supprimer(Long id) {
        execute(s -> {
            Avis avis = s.get(Avis.class, id);
            if (avis != null) {
                s.remove(avis);
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