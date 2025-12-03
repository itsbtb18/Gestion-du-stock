package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.service.MagasinService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * LoginController - Controller for user authentication
 * Part of the Controller layer in MVC architecture
 */
public class LoginController implements Initializable {
    
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;
    @FXML private CheckBox chkRememberMe;
    
    private MagasinService magasinService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        magasinService = MagasinService.getInstance();
        lblError.setVisible(false);
        
        // Set up Enter key to login
        txtPassword.setOnAction(event -> handleLogin());
    }
    
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs");
            return;
        }
        
        // TODO: Implement real authentication when UserDAO is ready
        // For now, simple hardcoded check
        if (authentifier(username, password)) {
            ouvrirMainView();
        } else {
            afficherErreur("Nom d'utilisateur ou mot de passe incorrect");
        }
    }
    
    private boolean authentifier(String username, String password) {
        // TODO: Replace with real authentication
        // userDAO.authenticate(username, password)
        return username.equals("admin") && password.equals("admin");
    }
    
    private void ouvrirMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main_view.fxml")
            );
            Parent root = loader.load();
            
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion de Stock - Application Principale");
            stage.setMaximized(true);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la vue principale: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur lors de l'ouverture de l'application");
        }
    }
    
    private void afficherErreur(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        
        // Hide error after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> lblError.setVisible(false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    @FXML
    private void handleCancel() {
        System.exit(0);
    }
}
