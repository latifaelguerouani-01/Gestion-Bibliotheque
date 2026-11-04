package com.biblio.controller;

import com.biblio.entities.Auteur;
import com.biblio.service.AuteurService;
import com.biblio.util.MessageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/auteurs")
public class AuteurServlet extends HttpServlet {

    private final AuteurService service = new AuteurService();

    private boolean admin(HttpServletRequest r) {
        HttpSession s = r.getSession(false);
        return s != null && "ADMIN".equalsIgnoreCase(String.valueOf(s.getAttribute("role")));
    }

    @Override
    protected void doGet(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
        r.setAttribute("auteurs", service.afficherAuteurs());
        if (r.getParameter("edit") != null && admin(r)) {
            try {
                r.setAttribute("auteurEdition", service.rechercherAuteur(Long.parseLong(r.getParameter("edit"))));
            } catch (NumberFormatException ignored) {}
        }
        r.getRequestDispatcher("/Auteurs/liste.jsp").forward(r, p);
    }

    @Override
    protected void doPost(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
        r.setCharacterEncoding("UTF-8");
        if (!admin(r)) {
            p.sendError(HttpServletResponse.SC_FORBIDDEN, "Action réservée à l'administrateur.");
            return;
        }

        String action = r.getParameter("action");
        try {
            if ("delete".equals(action)) {
                service.supprimerAuteur(Long.parseLong(r.getParameter("id")));
            } else {
                Auteur a = new Auteur();
                String id = r.getParameter("id");
                if (id != null && !id.isBlank()) {
                    a.setId(Long.parseLong(id));
                }
                a.setNom(r.getParameter("nom"));
                a.setPrenom(r.getParameter("prenom"));
                a.setBiographie(r.getParameter("biographie"));

                if ("update".equals(action)) {
                    service.modifierAuteur(a);
                } else {
                    service.ajouterAuteur(a);
                }
            }
            p.sendRedirect(r.getContextPath() + "/auteurs?success=1");
        } catch (IllegalArgumentException e) {
            r.setAttribute("erreur", MessageUtil.get(r.getSession(), e.getMessage() == null ? "operation.erreur" : e.getMessage()));
            r.setAttribute("auteurs", service.afficherAuteurs());
            r.getRequestDispatcher("/Auteurs/liste.jsp").forward(r, p);
        }
    }
}