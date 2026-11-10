package com.biblio.controller;

import com.biblio.entities.Livre;
import com.biblio.service.AuteurService;
import com.biblio.service.LivreService;
import com.biblio.util.MessageUtil;
import com.biblio.util.UploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/livres")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,        // au-delà de 1 Mo, écrit sur disque temporaire
        maxFileSize = 50L * 1024 * 1024,        // 50 Mo par fichier
        maxRequestSize = 60L * 1024 * 1024      // 60 Mo par requête
)
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

        String action = null;
        String nouveauPdf = null;
        String nouvelleCouverture = null;
        boolean succes = false;
        try {
            action = r.getParameter("action");
            if ("delete".equals(action)) {
                Long id = Long.parseLong(r.getParameter("id"));
                Livre existant = livres.rechercherLivre(id);
                livres.supprimerLivre(id);
                if (existant != null) {
                    UploadUtil.supprimer(existant.getPdfPath());
                    UploadUtil.supprimer(existant.getCouverture());
                }
            } else {
                Livre l = new Livre();
                Livre existant = null;
                String id = r.getParameter("id");
                if (id != null && !id.isBlank()) {
                    l.setId(Long.parseLong(id));
                    existant = livres.rechercherLivre(l.getId());
                }
                l.setTitre(r.getParameter("titre"));
                l.setIsbn(r.getParameter("isbn"));
                l.setDescription(r.getParameter("description"));
                l.setDisponible("true".equalsIgnoreCase(r.getParameter("disponible")));

                // Fichiers choisis depuis l'ordinateur (Choisir un fichier)
                nouveauPdf = UploadUtil.enregistrerPdf(r.getPart("pdfFile"));
                nouvelleCouverture = UploadUtil.enregistrerCouverture(r.getPart("couvertureFile"));

                // PDF : nouveau fichier > ancien fichier conservé
                String pdf = (nouveauPdf != null) ? nouveauPdf : (existant != null ? existant.getPdfPath() : null);
                if (pdf == null || pdf.isBlank()) throw new IllegalArgumentException("livre.pdf.obligatoire");
                l.setPdfPath(pdf);

                // Couverture : nouveau fichier > ancienne couverture conservée
                l.setCouverture(nouvelleCouverture != null ? nouvelleCouverture
                        : (existant != null ? existant.getCouverture() : null));

                if ("update".equals(action)) {
                    livres.modifierLivre(l, r.getParameterValues("auteurIds"));
                } else {
                    livres.ajouterLivre(l, r.getParameterValues("auteurIds"));
                }

                // Enregistrement OK : on supprime les anciens fichiers remplacés
                if (existant != null) {
                    if (nouveauPdf != null) UploadUtil.supprimer(existant.getPdfPath());
                    if (!java.util.Objects.equals(existant.getCouverture(), l.getCouverture())) {
                        UploadUtil.supprimer(existant.getCouverture());
                    }
                }
            }
            succes = true;
            p.sendRedirect(r.getContextPath() + "/livres?success=" + ("delete".equals(action) ? "deleted" : "saved"));
        } catch (IllegalStateException e) {
            // Fichier plus gros que la limite @MultipartConfig : la requête ne peut plus être relue, on redirige.
            p.sendRedirect(r.getContextPath() + "/livres?error=livre.fichier.trop.gros");
        } catch (IllegalArgumentException e) {
            String cle = e.getMessage();
            r.setAttribute("erreur", MessageUtil.get(r.getSession(), cle == null ? "operation.erreur" : cle));
            r.setAttribute("livres", livres.afficherLivres());
            r.setAttribute("auteurs", auteurs.afficherAuteurs());
            r.getRequestDispatcher("/Livres/livres.jsp").forward(r, p);
        } finally {
            if (!succes) nettoyer(nouveauPdf, nouvelleCouverture);
        }
    }

    private void nettoyer(String pdf, String couverture) {
        UploadUtil.supprimer(pdf);
        UploadUtil.supprimer(couverture);
    }
}

