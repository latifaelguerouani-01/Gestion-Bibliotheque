
package com.biblio.dao;

import com.biblio.entities.Emprunt;
import com.biblio.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EmpruntDAO {

    public void ajouter(Emprunt emprunt) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(emprunt);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Emprunt> lister() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT e FROM Emprunt e JOIN FETCH e.livre JOIN FETCH e.utilisateur ORDER BY e.dateEmprunt DESC", Emprunt.class
            ).list();
        }
    }

    public List<Emprunt> listerParUtilisateur(Long utilisateurId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT e FROM Emprunt e JOIN FETCH e.livre JOIN FETCH e.utilisateur WHERE e.utilisateur.id = :uid ORDER BY e.dateEmprunt DESC",
                    Emprunt.class
            ).setParameter("uid", utilisateurId).list();
        }
    }

    public Emprunt trouverParId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Emprunt.class, id);
        }
    }

    public void modifier(Emprunt emprunt) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(emprunt);
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

            Emprunt emprunt = session.get(Emprunt.class, id);

            if (emprunt != null) {
                session.remove(emprunt);
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
