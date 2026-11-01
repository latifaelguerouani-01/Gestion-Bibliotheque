<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Administration | Verdea</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<div class="app-shell">
    <c:set var="activePage" value="dashboard"/>
    <%@ include file="partials/sidebar.jsp" %>
    <main class="main-content">
        <header class="topbar">
            <div>
                <p class="eyebrow">Centre d'administration</p>
                <h1 class="greeting">Bonjour, <span class="greeting-name"><c:out value="${sessionScope.userNom}"/></span></h1>
                <p class="muted">Voici un aperçu de votre bibliothèque.</p>
            </div>
            <div class="avatar admin-avatar"><c:out value="${empty sessionScope.userNom ? 'V' : sessionScope.userNom.substring(0,1).toUpperCase()}"/></div>
        </header>

        <section class="stat-grid admin-stats">
            <article class="stat-card">
                <div class="stat-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z"/></svg></div>
                <p>Total des livres</p><strong>${livres.size()}</strong>
            </article>
            <article class="stat-card">
                <div class="stat-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg></div>
                <p>Emprunts en cours</p><strong>${empty nbEmpruntsEnCours ? '—' : nbEmpruntsEnCours}</strong>
            </article>
            <article class="stat-card">
                <div class="stat-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="m12 3 2.8 5.7 6.2.9-4.5 4.4 1.1 6.2-5.6-3-5.6 3 1.1-6.2L3 9.6l6.2-.9Z"/></svg></div>
                <p>Avis reçus</p><strong>${empty nbAvis ? '—' : nbAvis}</strong>
            </article>
            <article class="stat-card">
                <div class="stat-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8ZM20 8v6M23 11h-6"/></svg></div>
                <p>Utilisateurs</p><strong>${empty nbUtilisateurs ? '—' : nbUtilisateurs}</strong>
            </article>
        </section>

        <section class="section-head">
            <div><p class="eyebrow">Catalogue</p><h2>Livres récemment enregistrés</h2></div>
            <a href="${pageContext.request.contextPath}/livres">Gérer les livres</a>
        </section>
        <div class="book-grid">
            <c:forEach items="${livres}" var="livre">
                <article class="book-card">
                    <div class="cover-placeholder"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z"/></svg></div>
                    <h3><c:out value="${livre.titre}"/></h3>
                    <p><c:out value="${livre.isbn}"/></p>
                    <c:choose>
                        <c:when test="${livre.disponible}"><span class="availability">Disponible</span></c:when>
                        <c:otherwise><span class="status-pill is-off">Indisponible</span></c:otherwise>
                    </c:choose>
                </article>
            </c:forEach>
            <c:if test="${empty livres}"><p class="muted">Aucun livre enregistré pour le moment.</p></c:if>
        </div>
    </main>
</div>
</body>
</html>
