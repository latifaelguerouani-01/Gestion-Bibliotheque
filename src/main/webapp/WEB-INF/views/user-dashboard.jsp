<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mon espace | Verdea</title>
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
                <p class="eyebrow">Espace personnel</p>
                <h1 class="greeting">Bonjour, <span class="greeting-name"><c:out value="${sessionScope.userNom}"/></span></h1>
                <p class="muted">Prêt à découvrir votre prochaine lecture ?</p>
            </div>
            <div class="avatar"><c:out value="${empty sessionScope.userNom ? 'U' : sessionScope.userNom.substring(0,1).toUpperCase()}"/></div>
        </header>

        <section class="welcome-panel">
            <div>
                <p class="eyebrow light">Votre bibliothèque, à vous</p>
                <h2>Chaque livre ouvre une nouvelle porte.</h2>
                <p>Explorez le catalogue et construisez votre prochaine aventure.</p>
                <a class="btn light-btn" href="${pageContext.request.contextPath}/livres">Explorer le catalogue</a>
            </div>
            <div class="book-illustration"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z"/><path d="M8 7h8M8 11h8"/></svg></div>
        </section>

        <section class="stat-grid">
            <article class="stat-card">
                <div class="stat-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.8-7.5 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg></div>
                <p>Mes favoris</p><strong>${empty nbFavoris ? 0 : nbFavoris}</strong>
            </article>
            <article class="stat-card">
                <div class="stat-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 3h2l2.4 12.2a2 2 0 0 0 2 1.6h8.8a2 2 0 0 0 2-1.6L22 7H6"/><circle cx="10" cy="21" r="1"/><circle cx="18" cy="21" r="1"/></svg></div>
                <p>Dans mon panier</p><strong>${empty nbEmpruntsActifs ? 0 : nbEmpruntsActifs}</strong>
            </article>
            <article class="stat-card">
                <div class="stat-icon"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z"/></svg></div>
                <p>Livres disponibles</p><strong>${empty nbLivresDisponibles ? 0 : nbLivresDisponibles}</strong>
            </article>
        </section>

        <section class="section-head">
            <div><p class="eyebrow">À découvrir</p><h2>Catalogue de la bibliothèque</h2></div>
            <a href="${pageContext.request.contextPath}/livres">Voir tout</a>
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
            <c:if test="${empty livres}"><p class="muted">Aucun livre n'est encore enregistré.</p></c:if>
        </div>
    </main>
</div>
</body>
</html>
