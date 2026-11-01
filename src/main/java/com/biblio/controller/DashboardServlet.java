package com.biblio.controller;

import com.biblio.service.AvisService;
import com.biblio.service.EmpruntService;
import com.biblio.service.LivreService;
import com.biblio.service.UtilisateursService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/user/dashboard", "/admin/dashboard"})
public class DashboardServlet extends HttpServlet {
    private final LivreService livres = new LivreService();
    private final UtilisateursService utilisateurs = new UtilisateursService();
    private final EmpruntService emprunts = new EmpruntService();
    private final AvisService avis = new AvisService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("livres", livres.afficherLivres());
        boolean admin = "/admin/dashboard".equals(req.getServletPath());
        if (admin) {
            // Statistiques : une valeur absente s'affiche "—" au lieu de bloquer le tableau de bord.
            try {
                req.setAttribute("nbUtilisateurs", utilisateurs.afficherUtilisateurs().size());
            } catch (RuntimeException ignored) { }
            try {
                long enCours = emprunts.afficherEmprunts().stream()
                        .filter(e -> "EN_COURS".equals(e.getStatut())).count();
                req.setAttribute("nbEmpruntsEnCours", enCours);
            } catch (RuntimeException ignored) { }
            try {
                req.setAttribute("nbAvis", avis.afficherAvis().size());
            } catch (RuntimeException ignored) { }
        } else {
            try {
                Object id = req.getSession().getAttribute("userId");
                long actifs = emprunts.afficherEmpruntsUtilisateur(Long.valueOf(String.valueOf(id))).stream()
                        .filter(e -> "EN_COURS".equals(e.getStatut())).count();
                req.setAttribute("nbEmpruntsActifs", actifs);
            } catch (RuntimeException ignored) { }
        }
        req.getRequestDispatcher(admin ? "/WEB-INF/views/admin-dashboard.jsp"
                : "/WEB-INF/views/user-dashboard.jsp").forward(req, resp);
    }
}
