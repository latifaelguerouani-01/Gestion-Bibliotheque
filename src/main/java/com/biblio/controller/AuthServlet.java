package com.biblio.controller;

import com.biblio.entities.Utilisateurs;
import com.biblio.service.UtilisateursService;
import com.biblio.util.MessageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentification unique : une seule page de connexion et une seule page d'inscription
 * pour tous les utilisateurs. Le rôle (ADMIN / USER) est décidé par le serveur.
 */
@WebServlet(urlPatterns = {"/login", "/inscription", "/logout"})
public class AuthServlet extends HttpServlet {
    private final UtilisateursService service = new UtilisateursService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        appliquerLangue(req);
        String path = req.getServletPath();
        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login?logout=1");
            return;
        }
        String page = "/login".equals(path) ? "/login.jsp" : "/inscription.jsp";
        req.getRequestDispatcher(page).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        appliquerLangue(req);
        String path = req.getServletPath();
        if ("/login".equals(path)) {
            connecter(req, resp);
        } else if ("/inscription".equals(path)) {
            inscrire(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private void connecter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String identifiant = req.getParameter("identifiant");
        String password = req.getParameter("password");
        Utilisateurs user = service.authentifier(identifiant, password);
        if (user == null) {
            req.setAttribute("identifiant", identifiant == null ? "" : identifiant.trim());
            req.setAttribute("erreur", MessageUtil.get(req.getSession(), "auth.echec"));
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // Nouvelle session après connexion (protection contre la fixation de session),
        // en conservant la langue choisie.
        HttpSession ancienneSession = req.getSession(false);
        Object langue = ancienneSession == null ? null : ancienneSession.getAttribute("langue");
        if (ancienneSession != null) ancienneSession.invalidate();

        HttpSession session = req.getSession(true);
        session.setAttribute("langue", langue == null ? "fr" : langue);
        session.setAttribute("userId", user.getId());
        String prenom = user.getPrenom();
        session.setAttribute("userNom",
                prenom == null || prenom.isBlank() ? user.getNom() : prenom + " " + user.getNom());
        session.setAttribute("role", user.getRole());
        session.setAttribute("photoProfil", user.getPhotoProfil());

        boolean admin = UtilisateursService.ROLE_ADMIN.equalsIgnoreCase(user.getRole());
        resp.sendRedirect(req.getContextPath() + (admin ? "/admin/dashboard" : "/user/dashboard"));
    }

    private void inscrire(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nom = valeur(req, "nom");
        String prenom = valeur(req, "prenom");
        String email = valeur(req, "email");

        // Les champs saisis (hors mots de passe et code) sont réaffichés en cas d'erreur.
        req.setAttribute("nom", nom);
        req.setAttribute("prenom", prenom);
        req.setAttribute("email", email);

        String password = req.getParameter("password");
        String confirmation = req.getParameter("confirmPassword");
        if (password == null || !password.equals(confirmation)) {
            req.setAttribute("erreur", MessageUtil.get(req.getSession(), "auth.password.confirmation"));
            req.getRequestDispatcher("/inscription.jsp").forward(req, resp);
            return;
        }

        Utilisateurs user = new Utilisateurs();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setMotDePasse(password);
        try {
            // Le rôle n'est jamais lu depuis la requête : il est fixé exclusivement par le service.
            service.inscrire(user, req.getParameter("codeActivation"));
            resp.sendRedirect(req.getContextPath() + "/login?inscription=1");
        } catch (IllegalArgumentException e) {
            req.setAttribute("erreur", MessageUtil.get(req.getSession(), e.getMessage()));
            req.getRequestDispatcher("/inscription.jsp").forward(req, resp);
        } catch (IllegalStateException e) {
            req.setAttribute("erreur", MessageUtil.get(req.getSession(), "operation.erreur"));
            req.getRequestDispatcher("/inscription.jsp").forward(req, resp);
        }
    }

    private static String valeur(HttpServletRequest req, String nom) {
        String v = req.getParameter(nom);
        return v == null ? "" : v.trim();
    }

    private static void appliquerLangue(HttpServletRequest req) {
        String langue = req.getParameter("lang");
        if ("fr".equals(langue) || "en".equals(langue) || "ar".equals(langue)) {
            req.getSession(true).setAttribute("langue", langue);
        }
    }
}
