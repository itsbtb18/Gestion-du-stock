package org.example.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_FR_PATTERN = Pattern.compile(
        "^(\\+33|0)[1-9](\\d{2}){4}$"
    );
    
    private static final Pattern PHONE_INTERNATIONAL_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );
    
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)$"
    );
    
    private static final Pattern POSTAL_CODE_FR_PATTERN = Pattern.compile("^\\d{5}$");
    
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static boolean estNonVide(String valeur) {
        return valeur != null && !valeur.trim().isEmpty();
    }
    
    public static boolean estEmailValide(String email) {
        if (!estNonVide(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean estTelephoneValide(String telephone) {
        if (!estNonVide(telephone)) {
            return false;
        }
        String tel = telephone.replaceAll("[\\s.-]", "");
        return PHONE_FR_PATTERN.matcher(tel).matches();
    }
    
    public static boolean estTelephoneInternationalValide(String telephone) {
        if (!estNonVide(telephone)) {
            return false;
        }
        String tel = telephone.replaceAll("[\\s.-]", "");
        return PHONE_INTERNATIONAL_PATTERN.matcher(tel).matches();
    }
    
    public static boolean estMotDePasseSecurise(String password) {
        if (!estNonVide(password)) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }
    
    public static boolean estURLValide(String url) {
        if (!estNonVide(url)) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }
    
    public static boolean estCodePostalValide(String codePostal) {
        if (!estNonVide(codePostal)) {
            return false;
        }
        return POSTAL_CODE_FR_PATTERN.matcher(codePostal).matches();
    }
    
    public static boolean estPositif(double valeur) {
        return valeur > 0;
    }
    
    public static boolean estNonNegatif(double valeur) {
        return valeur >= 0;
    }
    
    public static boolean estDansIntervalle(double valeur, double min, double max) {
        return valeur >= min && valeur <= max;
    }
    
    public static boolean longueurValide(String valeur, int min, int max) {
        if (!estNonVide(valeur)) {
            return false;
        }
        int longueur = valeur.trim().length();
        return longueur >= min && longueur <= max;
    }
    
    public static boolean estAlphanumerique(String valeur) {
        if (!estNonVide(valeur)) {
            return false;
        }
        return valeur.matches("^[a-zA-Z0-9]+$");
    }
    
    public static boolean estCodeValide(String code) {
        return estNonVide(code) && estAlphanumerique(code) && longueurValide(code, 3, 20);
    }
    
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
    
    public static boolean estBarcodeValide(String barcode) {
        if (!estNonVide(barcode) || barcode.length() != 13) {
            return false;
        }
        return estNumerique(barcode);
    }
    
    public static String getPasswordErrorMessage() {
        return "Le mot de passe doit contenir au moins 8 caractères, " +
               "une majuscule, une minuscule, un chiffre et un caractère spécial (@$!%*?&)";
    }
    
    public static String getEmailErrorMessage() {
        return "Format d'email invalide. Exemple: exemple@domaine.com";
    }
    
    public static String getPhoneErrorMessage() {
        return "Format de téléphone invalide. Exemple: 0612345678 ou +33612345678";
    }
    
    public static String nettoyer(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur.trim().replaceAll("[<>\"'&]", "");
    }
}
