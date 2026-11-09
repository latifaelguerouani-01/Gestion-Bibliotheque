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
Connexion, inscription, dashboards, catalogue, auteurs et emprunts sont en place. Le module Favoris vient d'être intégré : ajout depuis le catalogue, affichage des favoris propres à chaque utilisateur et retrait d'un favori. Le panier de lecture et le lecteur PDF sont maintenant intégrés dans cette version. Les annotations, quiz, rapports et la traduction complète de toutes les pages restent à intégrer et tester.

## Module Favoris
- Accès utilisateur via `/favoris` et le lien « Mes favoris » dans la sidebar.
- Ajout d'un livre depuis le catalogue et retrait depuis la page Favoris.
- Chaque favori est associé à un utilisateur et à un livre ; un même livre ne peut être enregistré deux fois dans les favoris du même utilisateur.
- La table `favoris` est créée/mise à jour par Hibernate grâce à `hibernate.hbm2ddl.auto=update` et au mapping `Favori`.
- Les administrateurs ne peuvent pas accéder à cette fonctionnalité utilisateur.


## Panier de lecture et emprunts
- L'utilisateur peut emprunter un livre depuis le Catalogue ou depuis ses Favoris.
- La durée est choisie au moment de l'emprunt, entre 1 et 90 jours.
- Le livre est marqué indisponible pendant l'emprunt ; un livre indisponible ne peut pas être emprunté.
- La page « Mon panier » (`/emprunts`) affiche les emprunts de l'utilisateur, les dates, le statut, le lien de lecture PDF et le retour anticipé.
- Le retour anticipé enregistre la date effective et rend le livre disponible.
- Un traitement serveur (`EmpruntExpirationListener`) vérifie chaque heure les emprunts dont la date prévue est dépassée et les clôture automatiquement. La vérification est également exécutée à l'ouverture du panier et du lecteur.
- Ajout d'un livre (ADMIN) : dans le catalogue, le formulaire permet de choisir le PDF et l'image de couverture directement depuis l'ordinateur (« Choisir un fichier »). Le chemin du fichier est enregistré en base de données (`pdf_path`, `couverture`).
- Les fichiers téléversés sont stockés hors de l'application, dans `${user.home}/verdea-uploads/` (sous-dossiers `pdf/` et `covers/`). Ce dossier peut être changé avec `-Dverdea.upload.dir=...` ou la variable d'environnement `VERDEA_UPLOAD_DIR`. Limites : PDF 50 Mo, couverture 5 Mo.
- Les anciens livres avec un chemin `/uploads/mon-livre.pdf` ou une URL HTTP(S) continuent de fonctionner.
- L'accès au lecteur est réservé au propriétaire d'un emprunt actif.

## Aperçu, lecture et reprise

- **Aperçu** : depuis « Mes favoris », le bouton « Aperçu (10 pages) » ouvre uniquement les 10 premières pages du PDF (le serveur n'envoie pas le reste). Nécessite la dépendance PDFBox (déjà dans `pom.xml`).
- **Lecture** : champ « Ma page » + « Enregistrer ma page » (colonne `derniere_page` de la table `emprunts`, créée automatiquement par Hibernate) et bouton « Retourner le livre » en bas de la page.
