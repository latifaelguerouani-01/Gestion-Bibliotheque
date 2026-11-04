package com.biblio.controller;

import com.biblio.entities.Livre;
import com.biblio.service.AuteurService;
import com.biblio.service.LivreService;
import com.biblio.util.MessageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/livres")
public class LivreServlet extends HttpServlet {

    private final LivreService livres = new LivreService();
    private final AuteurService auteurs = new AuteurService();

    private boolean admin(HttpServletRequest r) {
        HttpSession s = r.getSession(false);
        return s != null && "ADMIN".equalsIgnoreCase(String.valueOf(s.getAttribute("role")));
    }

    @Override
    protected void doGet(HttpServletRequest r, HttpServletResponse p) throws ServletException, IOException {
        r.setCharacterEncoding("UTF-8");
        String q = r.getParameter("q");
        r.setAttribute("q", (q == null) ? "" : q);
        r.setAttribute("livres", livres.rechercherLivres(q));
        r.setAttribute("auteurs", auteurs.afficherAuteurs());

        if (r.getParameter("edit") != null && admin(r)) {
            try {
                r.setAttribute("livreEdition", livres.rechercherLivre(Long.parseLong(r.getParameter("edit"))));
            } catch (NumberFormatException ignored) {}
        }

        r.getRequestDispatcher("/Livres/livres.jsp").forward(r, p);
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
                livres.supprimerLivre(Long.parseLong(r.getParameter("id")));
            } else {
                Livre l = new Livre();
                String id = r.getParameter("id");
                if (id != null && !id.isBlank()) {
                    l.setId(Long.parseLong(id));
                }
                l.setTitre(r.getParameter("titre"));
                l.setIsbn(r.getParameter("isbn"));
                l.setDescription(r.getParameter("description"));
                l.setCouverture(r.getParameter("couverture"));
                l.setPdfPath(r.getParameter("pdfPath"));
                l.setDisponible("true".equalsIgnoreCase(r.getParameter("disponible")));

                if ("update".equals(action)) {
                    livres.modifierLivre(l, r.getParameterValues("auteurIds"));
                } else {
                    livres.ajouterLivre(l, r.getParameterValues("auteurIds"));
                }
            }
            p.sendRedirect(r.getContextPath() + "/livres?success=" + ("delete".equals(action) ? "deleted" : "saved"));
        } catch (IllegalArgumentException e) {
            r.setAttribute("erreur", MessageUtil.get(r.getSession(), e.getMessage() == null ? "operation.erreur" : e.getMessage()));
            r.setAttribute("livres", livres.afficherLivres());
            r.setAttribute("auteurs", auteurs.afficherAuteurs());
            r.getRequestDispatcher("/Livres/livres.jsp").forward(r, p);
        }
    }
}