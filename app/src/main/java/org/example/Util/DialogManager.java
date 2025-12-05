package org.example.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;

/**
 * DialogManager - Centralized utility for creating and managing JavaFX dialogs
 * Provides reusable methods for common dialog patterns (confirm, error, info, custom modals)
 */
public class DialogManager {
    
    private static DialogManager instance;
    
    private DialogManager() {}
    
    public static synchronized DialogManager getInstance() {
        if (instance == null) {
            instance = new DialogManager();
        }
        return instance;
    }
    
    /**
     * Show confirmation dialog
     * @param title Dialog title
     * @param message Confirmation message
     * @return true if user clicked OK/Yes, false otherwise
     */
    public boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Show confirmation dialog with custom buttons
     * @param title Dialog title
     * @param header Header text (can be null)
     * @param message Confirmation message
     * @param buttons Custom button types
     * @return Selected button type
     */
    public Optional<ButtonType> showConfirmation(String title, String header, String message, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(buttons);
        
        return alert.showAndWait();
    }
    
    /**
     * Show error dialog
     * @param title Dialog title
     * @param message Error message
     */
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show error dialog with exception details
     * @param title Dialog title
     * @param message Error message
     * @param exception Exception to display
     */
    public void showError(String title, String message, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(exception.getMessage());
        
        // Log the full stack trace
        LoggerUtil.logError(DialogManager.class, message, exception);
        
        alert.showAndWait();
    }
    
    /**
     * Show information dialog
     * @param title Dialog title
     * @param message Information message
     */
    public void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show warning dialog
     * @param title Dialog title
     * @param message Warning message
     */
    public void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show custom modal dialog from FXML
     * @param fxmlPath Path to FXML file (relative to resources)
     * @param title Dialog title
     * @param width Dialog width
     * @param height Dialog height
     * @param modal Whether dialog should be modal
     * @return FXMLLoader for accessing controller
     */
    public FXMLLoader showCustomDialog(String fxmlPath, String title, double width, double height, boolean modal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initStyle(StageStyle.DECORATED);
            
            if (modal) {
                dialogStage.initModality(Modality.APPLICATION_MODAL);
            }
            
            Scene scene = new Scene(root, width, height);
            // TODO: Add stylesheet if needed
            // scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
            return loader;
            
        } catch (IOException e) {
            LoggerUtil.logError(DialogManager.class, "Failed to load custom dialog: " + fxmlPath, e);
            showError("Erreur", "Impossible de charger la fenêtre: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Show custom modal dialog and return controller
     * @param fxmlPath Path to FXML file
     * @param title Dialog title
     * @param width Dialog width
     * @param height Dialog height
     * @param controllerClass Expected controller class
     * @param <T> Controller type
     * @return Controller instance or null if failed
     */
    public <T> T showCustomDialog(String fxmlPath, String title, double width, double height, Class<T> controllerClass) {
        FXMLLoader loader = showCustomDialog(fxmlPath, title, width, height, true);
        return loader != null ? loader.getController() : null;
    }
    
    /**
     * Show non-modal window
     * @param fxmlPath Path to FXML file
     * @param title Window title
     * @param width Window width
     * @param height Window height
     * @return Stage instance
     */
    public Stage showWindow(String fxmlPath, String title, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initStyle(StageStyle.DECORATED);
            
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
            
            return stage;
            
        } catch (IOException e) {
            LoggerUtil.logError(DialogManager.class, "Failed to load window: " + fxmlPath, e);
            showError("Erreur", "Impossible de charger la fenêtre: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Close all open dialogs
     * TODO: Implement dialog tracking if needed
     */
    public void closeAllDialogs() {
        // Placeholder for future implementation
    }
}
