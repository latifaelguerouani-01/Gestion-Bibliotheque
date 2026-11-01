package com.biblio.dao;

import com.biblio.entities.Utilisateurs;
import com.biblio.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UtilisateurDAO {

    public void ajouter(Utilisateurs utilisateur) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(utilisateur);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new IllegalStateException("Impossible d'enregistrer le compte.", e);
        }
    }

    /**
     * Retrouve un compte pour la connexion. L'adresse e-mail est l'identifiant unique :
     * le nom n'est pas unique et ne doit jamais servir à s'authentifier.
     */
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
            return session.createQuery(
                    "FROM Utilisateurs", Utilisateurs.class
            ).list();
        }
    }

    public Utilisateurs trouverParId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Utilisateurs.class, id);
        }
    }

    public void modifier(Utilisateurs utilisateur) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(utilisateur);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void supprimer(Long id) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Utilisateurs utilisateur = session.get(Utilisateurs.class, id);

            if (utilisateur != null) {
                session.remove(utilisateur);
            }

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
