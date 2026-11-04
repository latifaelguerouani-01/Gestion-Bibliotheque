package com.biblio.dao;

import com.biblio.entities.Livre;
import com.biblio.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Consumer;

public class LivreDAO {

    public void ajouter(Livre livre) {
        execute(s -> s.persist(livre));
    }

    public List<Livre> lister() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("SELECT DISTINCT l FROM Livre l LEFT JOIN FETCH l.auteurs ORDER BY l.titre", Livre.class).list();
        }
    }

    public List<Livre> rechercher(String q) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                            "SELECT DISTINCT l FROM Livre l LEFT JOIN FETCH l.auteurs " +
                                    "WHERE LOWER(l.titre) LIKE :q OR LOWER(COALESCE(l.isbn,'')) LIKE :q OR LOWER(COALESCE(l.description,'')) LIKE :q " +
                                    "ORDER BY l.titre", Livre.class)
                    .setParameter("q", "%" + (q != null ? q.toLowerCase() : "") + "%")
                    .list();
        }
    }

    public Livre trouverParId(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("SELECT DISTINCT l FROM Livre l LEFT JOIN FETCH l.auteurs WHERE l.id = :id", Livre.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    public boolean existeParIsbn(String isbn) {
        return countIsbn(isbn, null);
    }

    public boolean existeParIsbnPourAutreLivre(String isbn, Long id) {
        return countIsbn(isbn, id);
    }

    private boolean countIsbn(String isbn, Long id) {
        if (isbn == null || isbn.isBlank()) return false;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(l) FROM Livre l WHERE l.isbn = :isbn" + (id == null ? "" : " AND l.id <> :id");
            var query = s.createQuery(hql, Long.class).setParameter("isbn", isbn.trim());
            if (id != null) query.setParameter("id", id);
            Long count = query.uniqueResult();
            return count != null && count > 0;
        }
    }

    public void modifier(Livre livre) {
        execute(s -> s.merge(livre));
    }

    public void supprimer(Long id) {
        execute(s -> {
            Livre l = s.get(Livre.class, id);
            if (l != null) s.remove(l);
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