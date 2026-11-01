<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Connexion | Verdea</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body class="auth-page">
<section class="auth-brand">
    <img class="logo-symbol" src="${pageContext.request.contextPath}/assets/verdea-logo.png" alt="Verdea">
    <div class="tagline"><span>Lire</span><span>Apprendre</span><span>Grandir</span></div>
    <h2>La connaissance prend racine, une page à la fois.</h2>
    <p>Explorez une bibliothèque numérique qui associe le plaisir de lire à la simplicité des outils modernes.</p>
    <div class="brand-decoration"></div>
</section>

<main class="auth-card">
    <div class="logo-lockup"><img class="logo-symbol" src="${pageContext.request.contextPath}/assets/verdea-logo.png" alt="Verdea"></div>
    <p class="eyebrow">Votre bibliothèque numérique</p>
    <h1>Connexion</h1>
    <p class="muted">Accédez à votre espace de lecture.</p>

    <c:if test="${not empty erreur}"><div class="alert error" role="alert"><c:out value="${erreur}"/></div></c:if>
    <c:if test="${param.logout ne null}"><div class="alert success">Vous êtes déconnecté.</div></c:if>
    <c:if test="${param.inscription ne null}"><div class="alert success">Compte créé. Vous pouvez vous connecter.</div></c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <label for="lang">Langue</label>
        <select id="lang" name="lang" class="language-select">
            <option value="fr" ${sessionScope.langue eq 'fr' or empty sessionScope.langue ? 'selected' : ''}>Français</option>
            <option value="en" ${sessionScope.langue eq 'en' ? 'selected' : ''}>English</option>
            <option value="ar" ${sessionScope.langue eq 'ar' ? 'selected' : ''}>العربية</option>
        </select>

        <label for="identifiant">Adresse e-mail</label>
        <input id="identifiant" type="text" name="identifiant" required autocomplete="username" inputmode="email"
               value="<c:out value='${identifiant}'/>" placeholder="Votre adresse e-mail">

        <label for="password">Mot de passe</label>
        <input id="password" type="password" name="password" required autocomplete="current-password" placeholder="Votre mot de passe">

        <button class="btn primary full" type="submit">Se connecter</button>
    </form>
    <p class="auth-foot">Nouveau sur Verdea ? <a href="${pageContext.request.contextPath}/inscription">Créer un compte</a></p>
</main>
</body>
</html>
