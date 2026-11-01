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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Long utilisateurId = Long.valueOf(String.valueOf(session.getAttribute("userId")));
        boolean admin = "ADMIN".equalsIgnoreCase(String.valueOf(session.getAttribute("role")));
        request.setAttribute("emprunts", admin
                ? emprunts.afficherEmprunts()
                : emprunts.afficherEmpruntsUtilisateur(utilisateurId));
        request.getRequestDispatcher("/Emprunts/liste.jsp").forward(request, response);
    }
}
