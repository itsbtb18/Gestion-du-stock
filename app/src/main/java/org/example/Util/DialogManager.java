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

public class DialogManager {
    
    private static DialogManager instance;
    
    private DialogManager() {}
    
    public static synchronized DialogManager getInstance() {
        if (instance == null) {
            instance = new DialogManager();
        }
        return instance;
    }
    
    public boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    public Optional<ButtonType> showConfirmation(String title, String header, String message, ButtonType... buttons) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(buttons);
        
        return alert.showAndWait();
    }
    
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void showError(String title, String message, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(exception.getMessage());
        
        LoggerUtil.logError(DialogManager.class, message, exception);
        
        alert.showAndWait();
    }
    
    public void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
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
            
            try {
                java.net.URL cssUrl = getClass().getResource("/css/styles.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                }
            } catch (Exception e) {
                LoggerUtil.logWarning(DialogManager.class, "Could not load stylesheet for dialog");
            }
            
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
            
            return loader;
            
        } catch (IOException e) {
            LoggerUtil.logError(DialogManager.class, "Failed to load custom dialog: " + fxmlPath, e);
            showError("Erreur", "Impossible de charger la fenêtre: " + e.getMessage());
            return null;
        }
    }
    
    public <T> T showCustomDialog(String fxmlPath, String title, double width, double height, Class<T> controllerClass) {
        FXMLLoader loader = showCustomDialog(fxmlPath, title, width, height, true);
        return loader != null ? loader.getController() : null;
    }
    
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
    
    public void closeAllDialogs() {
        
        LoggerUtil.logDebug(DialogManager.class, "closeAllDialogs called - modal dialogs close automatically");
    }
}
