package com.biblio.service;

import com.biblio.dao.UtilisateurDAO;
import com.biblio.entities.Utilisateurs;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

public class UtilisateursService {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    private static final String EMAIL_LATIFA = "latifa@verdea";
    private static final String EMAIL_ANAS = "anas@verdea";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UtilisateurDAO utilisateursDAO = new UtilisateurDAO();

    public void ajouterUtilisateur(Utilisateurs utilisateur) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur obligatoire.");
        }
        utilisateursDAO.ajouter(utilisateur);
    }

    /** Connexion unique pour tous les rôles : le rôle est lu uniquement en base. */
    public Utilisateurs authentifier(String identifiant, String motDePasse) {
        if (identifiant == null || motDePasse == null || identifiant.trim().isEmpty() || motDePasse.isEmpty()) {
            return null;
        }
        Utilisateurs utilisateur = utilisateursDAO.trouverParIdentifiant(identifiant.trim());
        if (utilisateur == null || !utilisateur.isActif()) return null;
        String hash = utilisateur.getMotDePasse();
        try {
            if (hash != null && hash.startsWith("$2") && BCrypt.checkpw(motDePasse, hash)) {
                return utilisateur;
            }
        } catch (IllegalArgumentException ignored) {
            return null;
        }
        return null;
    }

    /**
     * Inscription unique pour tous les utilisateurs.
     * Le rôle est déterminé exclusivement ici, côté serveur, à partir de l'e-mail :
     * les deux adresses Verdea réservées deviennent ADMIN (après code d'activation),
     * toutes les autres deviennent USER. Aucun rôle venant de la requête n'est lu.
     */
    public void inscrire(Utilisateurs utilisateur, String codeActivation) {
        if (utilisateur == null || estVide(utilisateur.getNom()) || estVide(utilisateur.getPrenom())) {
            throw new IllegalArgumentException("auth.nom.requis");
        }

        String nom = utilisateur.getNom().trim();
        String prenom = utilisateur.getPrenom().trim();
        if (nom.length() > 100 || prenom.length() > 100) {
            throw new IllegalArgumentException("auth.champ.long");
        }

        String email = utilisateur.getEmail() == null
                ? "" : utilisateur.getEmail().trim().toLowerCase(Locale.ROOT);
        boolean latifaAdmin = EMAIL_LATIFA.equals(email);
        boolean anasAdmin = EMAIL_ANAS.equals(email);
        boolean adminCible = latifaAdmin || anasAdmin;

        if (!adminCible && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("auth.email.invalide");
        }
        if (email.length() > 150) {
            throw new IllegalArgumentException("auth.champ.long");
        }

        String motDePasse = utilisateur.getMotDePasse();
        if (motDePasse == null || motDePasse.length() < 8) {
            throw new IllegalArgumentException("auth.password.min");
        }
        if (utilisateursDAO.emailExiste(email)) {
            throw new IllegalArgumentException("auth.email.existe");
        }

        String role = ROLE_USER;
        if (adminCible) {
            String attendu = codeAttendu(latifaAdmin ? "VERDEA_LATIFA_ADMIN_CODE" : "VERDEA_ANAS_ADMIN_CODE",
                    latifaAdmin ? "verdea.latifa.admin.code" : "verdea.anas.admin.code");
            if (attendu == null) {
                throw new IllegalArgumentException("admin.code.non.configure");
            }
            if (codeActivation == null || !MessageDigest.isEqual(
                    attendu.getBytes(StandardCharsets.UTF_8),
                    codeActivation.trim().getBytes(StandardCharsets.UTF_8))) {
                throw new IllegalArgumentException("admin.code.invalide");
            }
            role = ROLE_ADMIN;
        } else if (codeActivation != null && !codeActivation.isBlank()) {
            throw new IllegalArgumentException("admin.code.inutile");
        }

        utilisateur.setId(null);
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(BCrypt.hashpw(motDePasse, BCrypt.gensalt(12)));
        utilisateur.setRole(role);
        utilisateur.setActif(true);

        try {
            utilisateursDAO.ajouter(utilisateur);
        } catch (IllegalStateException e) {
            // Deux inscriptions simultanées avec le même e-mail : la contrainte UNIQUE a bloqué la seconde.
            if (utilisateursDAO.emailExiste(email)) {
                throw new IllegalArgumentException("auth.email.existe");
            }
            throw e;
        }
    }

    /**
     * Code d'activation attendu pour un compte administrateur.
     * Source : variable d'environnement, sinon propriété JVM (-Dverdea.latifa.admin.code=...).
     */
    private static String codeAttendu(String variableEnvironnement, String proprieteJvm) {
        String code = System.getenv(variableEnvironnement);
        if (code == null || code.isBlank()) {
            code = System.getProperty(proprieteJvm);
        }
        return code == null || code.isBlank() ? null : code.trim();
    }

    private static boolean estVide(String valeur) {
        return valeur == null || valeur.trim().isEmpty();
    }

    public List<Utilisateurs> afficherUtilisateurs() {
        return utilisateursDAO.lister();
    }

    public Utilisateurs rechercherUtilisateur(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identifiant invalide.");
        }
        return utilisateursDAO.trouverParId(id);
    }

    public void modifierUtilisateur(Utilisateurs utilisateur) {
        if (utilisateur == null || utilisateur.getId() == null) {
            throw new IllegalArgumentException("Utilisateur invalide.");
        }
        utilisateursDAO.modifier(utilisateur);
    }

    public void supprimerUtilisateur(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Identifiant invalide.");
        }
        utilisateursDAO.supprimer(id);
    }
}
