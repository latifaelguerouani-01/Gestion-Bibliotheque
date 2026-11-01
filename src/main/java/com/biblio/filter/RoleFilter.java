package com.biblio.filter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns={"/user/*","/admin/*","/livres","/auteurs","/emprunts","/avis"})
public class RoleFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req=(HttpServletRequest)request; HttpServletResponse resp=(HttpServletResponse)response;
        HttpSession session=req.getSession(false); String path=req.getServletPath();
        if(session==null || session.getAttribute("userId")==null){resp.sendRedirect(req.getContextPath()+"/login");return;}
        String role=String.valueOf(session.getAttribute("role"));
        if(path.startsWith("/admin/") && !"ADMIN".equalsIgnoreCase(role)){resp.sendError(403,"Accès réservé à l'administrateur.");return;}
        if(path.startsWith("/user/") && !"USER".equalsIgnoreCase(role)){resp.sendError(403,"Accès réservé aux utilisateurs.");return;}
        chain.doFilter(request,response);
    }
}
