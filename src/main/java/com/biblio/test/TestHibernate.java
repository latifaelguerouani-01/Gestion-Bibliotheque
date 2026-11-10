package com.biblio.test;

import com.biblio.util.HibernateUtil;
import org.hibernate.Session;

public class TestHibernate {
    public static void main(String[] args) {
        System.out.println("=== Test de connexion Hibernate ===");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println(" SUCCÈS : Connexion Hibernate établie et tables synchronisées !");
        } catch (Throwable e) {
            System.err.println(" Échec de l'initialisation Hibernate :");
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}