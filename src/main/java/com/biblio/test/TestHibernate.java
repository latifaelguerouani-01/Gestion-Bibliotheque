
package com.biblio.test;

import com.biblio.util.HibernateUtil;

public class TestHibernate {
    public static void main(String[] args) {
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("Connexion Hibernate réussie !");
        } catch (Throwable e) {
            System.err.println("Échec de l'initialisation Hibernate :");
            e.printStackTrace();
        }
    }
}
