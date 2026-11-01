# Gestion-Bibliothèque (Verdea)

Application Java 17 / Maven WAR / JSP-Servlets / Hibernate 6 / MySQL.

## Lancement
1. Vérifier les paramètres MySQL dans `src/main/resources/hibernate.cfg.xml` (port 3305 et identifiants locaux).
2. Importer le projet Maven dans IntelliJ IDEA.
3. Définir les deux codes d'activation administrateur (voir ci-dessous).
4. Lancer avec Tomcat 10.1, puis ouvrir `/login` ou `/inscription`.

## Authentification : une seule inscription, une seule connexion

**Inscription (`/inscription`)** : le même formulaire pour tout le monde (nom, prénom, e-mail, mot de passe, confirmation).
Le rôle n'existe pas dans le formulaire ; il est décidé côté serveur (`UtilisateursService.inscrire`) :

| E-mail saisi | Rôle |
|---|---|
| `Latifa@verdea` | ADMIN (après code d'activation de Latifa) |
| `Anas@verdea` | ADMIN (après code d'activation d'Anas) |
| tout autre e-mail | USER |

- Chacun choisit son propre mot de passe pendant l'inscription (haché avec BCrypt, coût 12).
- Un rôle envoyé dans la requête est ignoré. Une adresse déjà inscrite est refusée (même en cas de double clic).
- Les deux adresses `@verdea` ne sont pas de vraies boîtes mail : leur première inscription exige un code d'activation propre à chacun, pour que personne ne puisse s'inscrire à leur place et obtenir le rôle ADMIN.
- `AdminSetupServlet` et `admin-setup.jsp` ont été supprimés définitivement.

**Connexion (`/login`)** : une seule page, identifiant = adresse e-mail + mot de passe. Le rôle enregistré en base
dirige vers `/admin/dashboard` (ADMIN) ou `/user/dashboard` (USER).

## Codes d'activation administrateur
Chaque code est lu côté serveur, jamais stocké dans le dépôt. Deux options au choix :

**Variables d'environnement** (Windows PowerShell, puis redémarrer IntelliJ / Tomcat) :
```powershell
setx VERDEA_LATIFA_ADMIN_CODE "code-secret-de-latifa"
setx VERDEA_ANAS_ADMIN_CODE "code-secret-d-anas"
```

**Ou propriétés JVM** (IntelliJ > Run/Debug Configuration > Tomcat > VM options) :
```
-Dverdea.latifa.admin.code=code-secret-de-latifa -Dverdea.anas.admin.code=code-secret-d-anas
```

Utiliser deux codes différents et confidentiels. Si le code n'est pas configuré, l'inscription administrateur est refusée.

## Interface
- Une seule feuille de style : `src/main/webapp/assets/app.css` (palette Verdea centralisée dans `:root`).
- Même Sidebar, mêmes boutons et mêmes cartes sur Login, Inscription, Dashboards, Catalogue, Auteurs et Emprunts.
- Aucun emoji : icônes SVG de trait uniformes.
- Logo : `src/main/webapp/assets/verdea-logo.png` (fond transparent). Favicon : `favicon.ico`.

## État du développement
Connexion, inscription, dashboards, catalogue, auteurs et emprunts sont en place. Les modules Favoris, Panier, Reader PDF,
quiz, rapports et la traduction complète de toutes les pages restent à intégrer et tester.
