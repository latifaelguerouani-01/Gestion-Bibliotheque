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
        Object userIdObj = (session != null) ? session.getAttribute("userId") : null;

        if (userIdObj == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long utilisateurId = Long.parseLong(String.valueOf(userIdObj));
        boolean admin = "ADMIN".equalsIgnoreCase(String.valueOf(session.getAttribute("role")));

        request.setAttribute("emprunts", admin
                ? emprunts.afficherEmprunts()
                : emprunts.afficherEmpruntsUtilisateur(utilisateurId));

        request.getRequestDispatcher("/Emprunts/liste.jsp").forward(request, response);
    }
}