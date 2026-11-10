package com.biblio.dao;

import com.biblio.entities.Emprunt;
import com.biblio.entities.Livre;
import com.biblio.entities.StatutEmprunt;
import com.biblio.entities.Utilisateurs;
import com.biblio.util.HibernateUtil;
import jakarta.persistence.LockModeType;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class EmpruntDAO {


    public void emprunter(Long utilisateurId, Long livreId, int dureeJours) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Livre livre = session.find(Livre.class, livreId, LockModeType.PESSIMISTIC_WRITE);
            Utilisateurs utilisateur = session.get(Utilisateurs.class, utilisateurId);

            if (utilisateur == null || !utilisateur.isActif()) {
                throw new IllegalArgumentException("emprunt.utilisateur.introuvable");
            }
            if (livre == null) {
                throw new IllegalArgumentException("emprunt.livre.introuvable");
            }
            Long dejaEmprunte = session.createQuery(
                    "SELECT COUNT(e) FROM Emprunt e WHERE e.utilisateur.id = :uid " +
                    "AND e.livre.id = :lid AND e.statut = :statut", Long.class)
                    .setParameter("uid", utilisateurId)
                    .setParameter("lid", livreId)
                    .setParameter("statut", StatutEmprunt.EN_COURS)
                    .uniqueResult();

            if (dejaEmprunte != null && dejaEmprunte > 0) {
                throw new IllegalArgumentException("emprunt.deja.en.cours");
            }
            if (!livre.isDisponible()) {
                throw new IllegalArgumentException("emprunt.livre.indisponible");
            }
            if (livre.getPdfPath() == null || livre.getPdfPath().isBlank()) {
                throw new IllegalArgumentException("emprunt.pdf.indisponible");
            }

            LocalDate debut = LocalDate.now();
            Emprunt emprunt = new Emprunt();
            emprunt.setUtilisateur(utilisateur);
            emprunt.setLivre(livre);
            emprunt.setDateEmprunt(debut);
            emprunt.setDateRetourPrevue(debut.plusDays(dureeJours));
            emprunt.setStatut(StatutEmprunt.EN_COURS);

            livre.setDisponible(false);
            session.persist(emprunt);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void retourner(Long empruntId, Long utilisateurId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Emprunt emprunt = session.createQuery(
                    "SELECT e FROM Emprunt e JOIN FETCH e.livre JOIN FETCH e.utilisateur WHERE e.id = :id",
                    Emprunt.class)
                    .setParameter("id", empruntId)
                    .uniqueResult();

            if (emprunt == null || !emprunt.getUtilisateur().getId().equals(utilisateurId)) {
                throw new IllegalArgumentException("emprunt.introuvable");
            }
            if (emprunt.getStatut() != StatutEmprunt.EN_COURS) {
                throw new IllegalArgumentException("emprunt.deja.retourne");
            }

            emprunt.setStatut(StatutEmprunt.RETOURNE);
            emprunt.setDateRetourEffective(LocalDate.now());
            emprunt.getLivre().setDisponible(true);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    /** Enregistre la dernière page lue d'un emprunt en cours appartenant à l'utilisateur. */
    public void enregistrerPage(Long empruntId, Long utilisateurId, int page) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Emprunt emprunt = session.createQuery(
                    "SELECT e FROM Emprunt e WHERE e.id = :id AND e.utilisateur.id = :uid AND e.statut = :statut",
                    Emprunt.class)
                    .setParameter("id", empruntId)
                    .setParameter("uid", utilisateurId)
                    .setParameter("statut", StatutEmprunt.EN_COURS)
                    .uniqueResult();
            if (emprunt == null) {
                throw new IllegalArgumentException("emprunt.introuvable");
            }
            emprunt.setDernierePage(page);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }


    public int retournerEmpruntsExpires() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<Emprunt> expires = session.createQuery(
                    "SELECT e FROM Emprunt e JOIN FETCH e.livre " +
                    "WHERE e.statut = :statut AND e.dateRetourPrevue < :aujourdHui",
                    Emprunt.class)
                    .setParameter("statut", StatutEmprunt.EN_COURS)
                    .setParameter("aujourdHui", LocalDate.now())
                    .list();

            for (Emprunt emprunt : expires) {
                emprunt.setStatut(StatutEmprunt.RETOURNE);
                emprunt.setDateRetourEffective(emprunt.getDateRetourPrevue());
                emprunt.getLivre().setDisponible(true);
            }

            tx.commit();
            return expires.size();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
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
                    "SELECT e FROM Emprunt e JOIN FETCH e.livre JOIN FETCH e.utilisateur " +
                    "WHERE e.utilisateur.id = :uid ORDER BY e.dateEmprunt DESC",
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

    public void ajouter(Emprunt emprunt) {
        execute(s -> s.persist(emprunt));
    }

    public void modifier(Emprunt emprunt) {
        execute(s -> s.merge(emprunt));
    }

    public void supprimer(Long id) {
        execute(s -> {
            Emprunt emprunt = s.get(Emprunt.class, id);
            if (emprunt != null) s.remove(emprunt);
        });
    }

    private void execute(Consumer<Session> action) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            action.accept(s);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }
}