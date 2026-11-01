<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inscription | Verdea</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body class="auth-page">
<section class="auth-brand">
    <img class="logo-symbol" src="${pageContext.request.contextPath}/assets/verdea-logo.png" alt="Verdea">
    <div class="tagline"><span>Lire</span><span>Apprendre</span><span>Grandir</span></div>
    <h2>Rejoignez une bibliothèque qui grandit avec vous.</h2>
    <p>Créez votre compte en quelques secondes et retrouvez vos lectures, vos emprunts et votre catalogue au même endroit.</p>
    <div class="brand-decoration"></div>
</section>

<main class="auth-card">
    <div class="logo-lockup"><img class="logo-symbol" src="${pageContext.request.contextPath}/assets/verdea-logo.png" alt="Verdea"></div>
    <p class="eyebrow">Rejoignez Verdea</p>
    <h1>Créer un compte</h1>
    <p class="muted">Votre espace de lecture commence ici.</p>

    <c:if test="${not empty erreur}"><div class="alert error" role="alert"><c:out value="${erreur}"/></div></c:if>

    <form method="post" action="${pageContext.request.contextPath}/inscription" id="registrationForm">
        <div class="name-row">
            <div>
                <label for="nom">Nom</label>
                <input id="nom" name="nom" required maxlength="100" autocomplete="family-name"
                       value="<c:out value='${nom}'/>" placeholder="Votre nom">
            </div>
            <div>
                <label for="prenom">Prénom</label>
                <input id="prenom" name="prenom" required maxlength="100" autocomplete="given-name"
                       value="<c:out value='${prenom}'/>" placeholder="Votre prénom">
            </div>
        </div>

        <label for="email">Adresse e-mail</label>
        <input id="email" type="text" name="email" required maxlength="150" inputmode="email" autocomplete="email"
               value="<c:out value='${email}'/>" placeholder="nom@exemple.com">

        <label for="password">Mot de passe (8 caractères minimum)</label>
        <input id="password" type="password" name="password" minlength="8" required autocomplete="new-password" placeholder="Créer un mot de passe">

        <label for="confirmPassword">Confirmer le mot de passe</label>
        <input id="confirmPassword" type="password" name="confirmPassword" minlength="8" required autocomplete="new-password" placeholder="Confirmer le mot de passe">

        <div id="adminActivation" class="activation-field" hidden>
            <label for="codeActivation">Code d’activation administrateur</label>
            <input id="codeActivation" type="password" name="codeActivation" autocomplete="off" placeholder="Votre code d’activation personnel">
            <p class="muted activation-help">Ce code est demandé uniquement pour la première inscription des comptes administrateur Verdea.</p>
        </div>

        <button class="btn primary full" type="submit">Créer mon compte</button>
    </form>
    <p class="auth-foot">Déjà inscrit ? <a href="${pageContext.request.contextPath}/login">Se connecter</a></p>
</main>

<script>
(function () {
    var email = document.getElementById('email');
    var activation = document.getElementById('adminActivation');
    var code = document.getElementById('codeActivation');
    var password = document.getElementById('password');
    var confirmation = document.getElementById('confirmPassword');
    var adminEmails = ['latifa@verdea', 'anas@verdea'];

    // Affichage du champ de code uniquement pour les deux adresses administrateur.
    // Le rôle réel est toujours décidé par le serveur, jamais par ce script.
    function updateActivationField() {
        var isAdmin = adminEmails.indexOf(email.value.trim().toLowerCase()) !== -1;
        activation.hidden = !isAdmin;
        code.required = isAdmin;
        if (!isAdmin) code.value = '';
    }

    function checkConfirmation() {
        confirmation.setCustomValidity(
            confirmation.value && confirmation.value !== password.value
                ? 'Les deux mots de passe ne correspondent pas.' : '');
    }

    email.addEventListener('input', updateActivationField);
    password.addEventListener('input', checkConfirmation);
    confirmation.addEventListener('input', checkConfirmation);
    updateActivationField();
})();
</script>
</body>
</html>
