package com.biblio.dao;
import com.biblio.entities.Auteur;
import com.biblio.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class AuteurDAO {
    public void ajouter(Auteur a) { execute(s -> s.persist(a)); }
    public List<Auteur> lister() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Auteur a ORDER BY a.nom, a.prenom", Auteur.class).list();
        }
    }
    public Auteur trouverParId(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) { return s.get(Auteur.class, id); }
    }
    public void modifier(Auteur a) { execute(s -> s.merge(a)); }
    public void supprimer(Long id) {
        execute(s -> {
            Auteur a = s.get(Auteur.class, id);
            if (a != null) {
                s.createNativeMutationQuery("DELETE FROM livre_auteur WHERE auteur_id=:id").setParameter("id", id).executeUpdate();
                s.remove(a);
            }
        });
    }
    private void execute(java.util.function.Consumer<Session> action) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction(); action.accept(s); tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
