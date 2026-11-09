<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mon panier de lecture | Verdea</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<div class="app-shell">
    <c:set var="activePage" value="emprunts"/>
    <%@ include file="/WEB-INF/views/partials/sidebar.jsp" %>
    <main class="main-content">
        <header class="page-intro">
            <p class="eyebrow">VOTRE ESPACE PERSONNEL</p>
            <h1><c:choose>
                <c:when test="${sessionScope.role eq 'ADMIN'}">Gestion des emprunts</c:when>
                <c:otherwise>Mon panier de lecture</c:otherwise>
            </c:choose></h1>
            <p><c:choose>
                <c:when test="${sessionScope.role eq 'ADMIN'}">Consultez les emprunts et suivez les dates de retour.</c:when>
                <c:otherwise>Retrouvez les livres que vous avez empruntés, poursuivez votre lecture et gérez vos retours.</c:otherwise>
            </c:choose></p>
        </header>

        <c:if test="${param.success eq 'borrowed'}"><div class="alert success">Le livre a été ajouté à votre panier de lecture.</div></c:if>
        <c:if test="${param.success eq 'returned'}"><div class="alert success">Le livre a été retourné. Il est de nouveau disponible dans le catalogue.</div></c:if>
        <c:if test="${param.error eq 'invalid'}"><div class="alert error">Impossible d'effectuer cette opération. Veuillez réessayer.</div></c:if>
        <c:if test="${param.error eq 'emprunt.deja.retourne'}"><div class="alert error">Ce livre a déjà été retourné.</div></c:if>
        <c:if test="${param.error eq 'emprunt.introuvable'}"><div class="alert error">Cet emprunt est introuvable ou ne vous appartient pas.</div></c:if>
        <c:if test="${param.error eq 'emprunt.operation.erreur'}"><div class="alert error">Une erreur est survenue. Veuillez réessayer.</div></c:if>

        <section class="panel">
            <div class="section-head">
                <div>
                    <p class="eyebrow"><c:choose><c:when test="${sessionScope.role eq 'ADMIN'}">SUIVI GLOBAL</c:when><c:otherwise>MA BIBLIOTHÈQUE</c:otherwise></c:choose></p>
                    <h2><c:choose><c:when test="${sessionScope.role eq 'ADMIN'}">Tous les emprunts</c:when><c:otherwise>Mes livres empruntés</c:otherwise></c:choose></h2>
                </div>
                <span class="status-pill"><c:out value="${emprunts.size()}"/> emprunt(s)</span>
            </div>

            <c:choose>
                <c:when test="${not empty emprunts}">
                    <div class="loan-list">
                        <c:forEach items="${emprunts}" var="e">
                            <article class="loan-card">
                                <div class="loan-cover">
                                    <c:choose>
                                        <c:when test="${not empty e.livre.couverture}">
                                            <c:choose>
                                                <c:when test="${e.livre.couvertureLocale}">
                                                    <img src="${pageContext.request.contextPath}/<c:out value='${e.livre.couverture}'/>" alt="Couverture de <c:out value='${e.livre.titre}'/>">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="<c:out value='${e.livre.couverture}'/>" alt="Couverture de <c:out value='${e.livre.titre}'/>">
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z"/></svg>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="loan-details">
                                    <c:if test="${sessionScope.role eq 'ADMIN'}">
                                        <p class="loan-reader"><c:out value="${e.utilisateur.prenom}"/> <c:out value="${e.utilisateur.nom}"/></p>
                                    </c:if>
                                    <h3><c:out value="${e.livre.titre}"/></h3>
                                    <p class="loan-meta">Emprunté le <strong><c:out value="${e.dateEmprunt}"/></strong></p>
                                    <p class="loan-meta">Retour prévu le <strong><c:out value="${e.dateRetourPrevue}"/></strong></p>
                                    <c:if test="${not empty e.dateRetourEffective}">
                                        <p class="loan-meta">Retour effectué le <strong><c:out value="${e.dateRetourEffective}"/></strong></p>
                                    </c:if>
                                    <div class="loan-bottom">
                                        <c:choose>
                                            <c:when test="${e.statut eq 'EN_COURS'}"><span class="status-pill">En cours</span></c:when>
                                            <c:when test="${e.statut eq 'RETOURNE'}"><span class="availability">Retourné</span></c:when>
                                            <c:otherwise><span class="status-pill is-off"><c:out value="${e.statut}"/></span></c:otherwise>
                                        </c:choose>
                                        <c:if test="${sessionScope.role eq 'USER' and e.statut eq 'EN_COURS'}">
                                            <div class="loan-actions">
                                                <c:if test="${not empty e.livre.pdfPath}">
                                                    <a class="btn primary" href="${pageContext.request.contextPath}/lecture?id=${e.id}">Lire le PDF</a>
                                                </c:if>
                                                <form method="post" action="${pageContext.request.contextPath}/emprunts" onsubmit="return confirm('Confirmer le retour de ce livre ?');">
                                                    <input type="hidden" name="action" value="retourner">
                                                    <input type="hidden" name="empruntId" value="${e.id}">
                                                    <button class="btn btn-secondary" type="submit">Retourner le livre</button>
                                                </form>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </article>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-favorites">
                        <div class="empty-favorites-icon">
                            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z"/></svg>
                        </div>
                        <c:choose>
                            <c:when test="${sessionScope.role eq 'ADMIN'}">
                                <h2>Aucun emprunt pour le moment</h2>
                                <p>Les emprunts de tous les utilisateurs apparaîtront ici dès qu'un livre sera emprunté.</p>
                                <a class="btn primary" href="${pageContext.request.contextPath}/livres">Gérer le catalogue</a>
                            </c:when>
                            <c:otherwise>
                                <h2>Votre panier est vide</h2>
                                <p>Parcourez le catalogue, choisissez une durée d'emprunt et ajoutez votre premier livre à votre espace de lecture.</p>
                                <a class="btn primary" href="${pageContext.request.contextPath}/livres">Explorer le catalogue</a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</div>
</body>
</html>
