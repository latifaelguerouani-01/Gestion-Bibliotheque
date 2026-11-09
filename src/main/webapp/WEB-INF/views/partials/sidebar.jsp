<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="isAdmin" value="${sessionScope.role eq 'ADMIN'}"/>
<c:set var="homeUrl" value="${pageContext.request.contextPath}${isAdmin ? '/admin/dashboard' : '/user/dashboard'}"/>
<aside class="sidebar">
  <a class="brand" href="${homeUrl}" aria-label="Verdea - accueil"><img class="brand-logo" src="${pageContext.request.contextPath}/assets/verdea-logo.png" alt="Verdea"></a>
  <nav>
    <a class="${activePage eq 'dashboard' ? 'active' : ''}" href="${homeUrl}">
      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m3 10 9-7 9 7v10a1 1 0 0 1-1 1h-5v-7H9v7H4a1 1 0 0 1-1-1z"/></svg><span>Dashboard</span></a>
    <a class="${activePage eq 'livres' ? 'active' : ''}" href="${pageContext.request.contextPath}/livres">
      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z"/></svg><span>Catalogue</span></a>
    <c:if test="${isAdmin}">
      <a class="${activePage eq 'auteurs' ? 'active' : ''}" href="${pageContext.request.contextPath}/auteurs">
        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 20h9M16.5 3.5a2.1 2.1 0 0 1 3 3L8 18l-4 1 1-4Z"/></svg><span>Auteurs</span></a>
    </c:if>
    <c:if test="${not isAdmin}">
      <a class="${activePage eq 'favoris' ? 'active' : ''}" href="${pageContext.request.contextPath}/favoris">
        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.8-7.5 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"/></svg><span>Mes favoris</span></a>
    </c:if>
    <a class="${activePage eq 'emprunts' ? 'active' : ''}" href="${pageContext.request.contextPath}/emprunts">
      <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg><span>${isAdmin ? 'Emprunts' : 'Mon panier'}</span></a>
  </nav>
  <div class="sidebar-bottom">
    <div class="user-chip ${isAdmin ? 'is-admin' : ''}">
      <div class="user-avatar"><c:out value="${empty sessionScope.userNom ? 'U' : sessionScope.userNom.substring(0,1).toUpperCase()}"/></div>
      <div class="user-meta"><strong><c:out value="${sessionScope.userNom}"/></strong><span>${isAdmin ? 'Administrateur' : 'Lecteur'}</span></div>
    </div>
    <a class="logout" href="${pageContext.request.contextPath}/logout">
      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M10 17l5-5-5-5M15 12H3M21 19V5a2 2 0 0 0-2-2h-6"/></svg><span>Déconnexion</span></a>
  </div>
</aside>
