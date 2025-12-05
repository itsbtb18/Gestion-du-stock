package org.example.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

/**
 * AlertUtil - Utility class for displaying alerts and dialogs
 */
public class AlertUtil {
    
    private AlertUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Show information alert
     */
    public static void afficherInfo(String titre, String message) {
        afficher(Alert.AlertType.INFORMATION, titre, message);
    }
    
    /**
     * Show success alert
     */
    public static void afficherSucces(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText("✅ Succès");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show warning alert
     */
    public static void afficherAvertissement(String titre, String message) {
        afficher(Alert.AlertType.WARNING, titre, message);
    }
    
    /**
     * Show error alert
     */
    public static void afficherErreur(String titre, String message) {
        afficher(Alert.AlertType.ERROR, titre, message);
    }
    
    /**
     * Show confirmation dialog
     */
    public static boolean afficherConfirmation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Show input dialog
     */
    public static Optional<String> afficherSaisie(String titre, String message, String valeurDefaut) {
        TextInputDialog dialog = new TextInputDialog(valeurDefaut);
        dialog.setTitle(titre);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        
        return dialog.showAndWait();
    }
    
    /**
     * Generic alert display
     */
    private static void afficher(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show error with exception details
     */
    public static void afficherErreurException(String titre, String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(message);
        alert.setContentText("Détails: " + e.getMessage());
        alert.showAndWait();
    }
    
    /**
     * Show quick notification (auto-close)
     */
    public static void afficherNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // English aliases for consistency
    
    public static void showInfo(String titre, String message) {
        afficherInfo(titre, message);
    }
    
    public static void showSuccess(String titre, String message) {
        afficherSucces(titre, message);
    }
    
    public static void showWarning(String titre, String message) {
        afficherAvertissement(titre, message);
    }
    
    public static void showError(String titre, String message) {
        afficherErreur(titre, message);
    }
    
    public static boolean showConfirmation(String titre, String message) {
        return afficherConfirmation(titre, message);
    }
    
    public static Optional<String> showInput(String titre, String message, String valeurDefaut) {
        return afficherSaisie(titre, message, valeurDefaut);
    }
}
