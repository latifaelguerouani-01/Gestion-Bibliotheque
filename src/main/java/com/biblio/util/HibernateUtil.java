package com.biblio.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.net.InetSocketAddress;
import java.net.Socket;

public class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
            String currentUrl = cfg.getProperty("hibernate.connection.url");

            // Si 3306 n'est pas ouvert mais 3305 l'est (chez votre binôme)
            if (currentUrl != null && currentUrl.contains(":3306/")) {
                if (!isPortOpen("127.0.0.1", 3306, 250) && isPortOpen("127.0.0.1", 3305, 250)) {
                    cfg.setProperty("hibernate.connection.url", currentUrl.replace(":3306/", ":3305/"));
                }
            }

            return cfg.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Erreur d'initialisation Hibernate : " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static boolean isPortOpen(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }
}