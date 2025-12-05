package org.example.util;

import java.util.regex.Pattern;

/**
 * ValidationUtil - Enhanced utility class for input validation
 */
public class ValidationUtil {
    
    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Phone regex patterns (French and international)
    private static final Pattern PHONE_FR_PATTERN = Pattern.compile(
        "^(\\+33|0)[1-9](\\d{2}){4}$"
    );
    
    private static final Pattern PHONE_INTERNATIONAL_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );
    
    // Strong password pattern (min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char)
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    // URL pattern
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)$"
    );
    
    // Postal code pattern (French)
    private static final Pattern POSTAL_CODE_FR_PATTERN = Pattern.compile("^\\d{5}$");
    
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Validate if string is not null or empty
     */
    public static boolean estNonVide(String valeur) {
        return valeur != null && !valeur.trim().isEmpty();
    }
    
    /**
     * Validate email format
     */
    public static boolean estEmailValide(String email) {
        if (!estNonVide(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate French phone number format
     */
    public static boolean estTelephoneValide(String telephone) {
        if (!estNonVide(telephone)) {
            return false;
        }
        String tel = telephone.replaceAll("[\\s.-]", "");
        return PHONE_FR_PATTERN.matcher(tel).matches();
    }
    
    /**
     * Validate international phone number format
     */
    public static boolean estTelephoneInternationalValide(String telephone) {
        if (!estNonVide(telephone)) {
            return false;
        }
        String tel = telephone.replaceAll("[\\s.-]", "");
        return PHONE_INTERNATIONAL_PATTERN.matcher(tel).matches();
    }
    
    /**
     * Validate strong password
     */
    public static boolean estMotDePasseSecurise(String password) {
        if (!estNonVide(password)) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Validate URL format
     */
    public static boolean estURLValide(String url) {
        if (!estNonVide(url)) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }
    
    /**
     * Validate French postal code
     */
    public static boolean estCodePostalValide(String codePostal) {
        if (!estNonVide(codePostal)) {
            return false;
        }
        return POSTAL_CODE_FR_PATTERN.matcher(codePostal).matches();
    }
    
    /**
     * Validate positive number
     */
    public static boolean estPositif(double valeur) {
        return valeur > 0;
    }
    
    /**
     * Validate non-negative number
     */
    public static boolean estNonNegatif(double valeur) {
        return valeur >= 0;
    }
    
    /**
     * Validate number in range
     */
    public static boolean estDansIntervalle(double valeur, double min, double max) {
        return valeur >= min && valeur <= max;
    }
    
    /**
     * Validate string length
     */
    public static boolean longueurValide(String valeur, int min, int max) {
        if (!estNonVide(valeur)) {
            return false;
        }
        int longueur = valeur.trim().length();
        return longueur >= min && longueur <= max;
    }
    
    /**
     * Validate alphanumeric string
     */
    public static boolean estAlphanumerique(String valeur) {
        if (!estNonVide(valeur)) {
            return false;
        }
        return valeur.matches("^[a-zA-Z0-9]+$");
    }
    
    /**
     * Validate code format (letters and numbers, no spaces)
     */
    public static boolean estCodeValide(String code) {
        return estNonVide(code) && estAlphanumerique(code) && longueurValide(code, 3, 20);
    }
    
    /**
     * Validate numeric string
     */
    public static boolean estNumerique(String valeur) {
        if (!estNonVide(valeur)) {
            return false;
        }
        try {
            Double.parseDouble(valeur);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate integer string
     */
    public static boolean estEntier(String valeur) {
        if (!estNonVide(valeur)) {
            return false;
        }
        try {
            Integer.parseInt(valeur);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate barcode format (EAN-13)
     */
    public static boolean estBarcodeValide(String barcode) {
        if (!estNonVide(barcode) || barcode.length() != 13) {
            return false;
        }
        return estNumerique(barcode);
    }
    
    /**
     * Get validation error message for password
     */
    public static String getPasswordErrorMessage() {
        return "Le mot de passe doit contenir au moins 8 caractères, " +
               "une majuscule, une minuscule, un chiffre et un caractère spécial (@$!%*?&)";
    }
    
    /**
     * Get validation error message for email
     */
    public static String getEmailErrorMessage() {
        return "Format d'email invalide. Exemple: exemple@domaine.com";
    }
    
    /**
     * Get validation error message for phone
     */
    public static String getPhoneErrorMessage() {
        return "Format de téléphone invalide. Exemple: 0612345678 ou +33612345678";
    }
    
    /**
     * Sanitize string (remove special characters)
     */
    public static String nettoyer(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur.trim().replaceAll("[<>\"'&]", "");
    }
}
