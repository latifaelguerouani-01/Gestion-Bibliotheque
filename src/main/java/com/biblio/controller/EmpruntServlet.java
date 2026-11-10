package com.biblio.controller;

import com.biblio.service.EmpruntService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/emprunts")
public class EmpruntServlet extends HttpServlet {

    private final EmpruntService emprunts = new EmpruntService();

    private Long getUserId(HttpSession session) {
        if (session == null || session.getAttribute("userId") == null) return null;
        try {
            return Long.valueOf(String.valueOf(session.getAttribute("userId")));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isUser(HttpSession session) {
        return session != null && "USER".equalsIgnoreCase(String.valueOf(session.getAttribute("role")));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long utilisateurId = getUserId(session);
        if (utilisateurId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Sécurité supplémentaire : traite les échéances dépassées avant d'afficher la liste.
        emprunts.retournerEmpruntsExpires();

        boolean admin = "ADMIN".equalsIgnoreCase(String.valueOf(session.getAttribute("role")));

        request.setAttribute("emprunts", admin
                ? emprunts.afficherEmprunts()
                : emprunts.afficherEmpruntsUtilisateur(utilisateurId));
        request.setAttribute("activePage", "emprunts");
        request.getRequestDispatcher("/Emprunts/liste.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Long utilisateurId = getUserId(session);

        if (utilisateurId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!isUser(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");
        String retour = request.getParameter("retour");
        String destination = "retourner".equals(action) ? "/emprunts"
                : ("favoris".equals(retour) ? "/favoris" : "/livres");

        try {
            if ("emprunter".equals(action)) {
                Long livreId = Long.valueOf(request.getParameter("livreId"));
                int duree = Integer.parseInt(request.getParameter("duree"));
                emprunts.emprunter(utilisateurId, livreId, duree);
                response.sendRedirect(request.getContextPath() + "/emprunts?success=borrowed");
                return;
            }

            if ("retourner".equals(action)) {
                Long empruntId = Long.valueOf(request.getParameter("empruntId"));
                emprunts.retourner(empruntId, utilisateurId);
                response.sendRedirect(request.getContextPath() + "/emprunts?success=returned");
                return;
            }

            response.sendRedirect(request.getContextPath() + destination + "?error=invalid");
        } catch (IllegalArgumentException e) {
            String code = (e.getMessage() != null && e.getMessage().startsWith("emprunt."))
                    ? e.getMessage() : "emprunt.operation.erreur";
            response.sendRedirect(request.getContextPath() + destination + "?error=" + code);
        }
    }
}
