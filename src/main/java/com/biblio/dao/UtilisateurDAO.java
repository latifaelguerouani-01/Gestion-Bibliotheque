package com.biblio.dao;

import com.biblio.entities.Utilisateurs;
import com.biblio.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Consumer;

public class UtilisateurDAO {

    public void ajouter(Utilisateurs utilisateur) {
        execute(s -> s.persist(utilisateur));
    }

    public Utilisateurs trouverParIdentifiant(String identifiant) {
        return trouverParEmail(identifiant);
    }

    public Utilisateurs trouverParEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Utilisateurs u WHERE LOWER(u.email) = LOWER(:email)",
                            Utilisateurs.class)
                    .setParameter("email", email.trim())
                    .uniqueResult();
        }
    }

    public boolean emailExiste(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(u) FROM Utilisateurs u WHERE LOWER(u.email) = LOWER(:email)",
                    Long.class).setParameter("email", email.trim()).uniqueResult();
            return count != null && count > 0;
        }
    }

    public List<Utilisateurs> lister() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Utilisateurs u ORDER BY u.nom, u.prenom", Utilisateurs.class).list();
        }
    }

    public Utilisateurs trouverParId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Utilisateurs.class, id);
        }
    }

    public void modifier(Utilisateurs utilisateur) {
        execute(s -> s.merge(utilisateur));
    }

    public void supprimer(Long id) {
        execute(s -> {
            Utilisateurs utilisateur = s.get(Utilisateurs.class, id);
            if (utilisateur != null) {
                s.remove(utilisateur);
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