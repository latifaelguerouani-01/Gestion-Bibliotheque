package com.biblio.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/user/*", "/admin/*", "/livres", "/auteurs", "/emprunts", "/avis", "/favoris"})
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Force l'encodage des paramètres en UTF-8
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        Object userId = (session != null) ? session.getAttribute("userId") : null;

        // Redirection vers login si non authentifié
        if (session == null || userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Restriction ADMIN : seul ADMIN peut entrer dans /admin/*
        if (path.startsWith("/admin") && !"ADMIN".equalsIgnoreCase(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé à l'administrateur.");
            return;
        }

        // Si l'utilisateur standard essaie d'accéder à une ressource restreinte
        // (Un ADMIN a le droit d'accéder aux routes /user/*)
        if (path.startsWith("/user") && !"USER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès non autorisé.");
            return;
        }

        chain.doFilter(request, response);
    }
}