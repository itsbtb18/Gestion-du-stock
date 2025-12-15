package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.dao.UtilisateurDAO;
import org.example.model.entity.Utilisateur;
import org.example.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;
    @FXML private CheckBox chkRememberMe;
    @FXML private ImageView backgroundImage;
    
    private UtilisateurDAO utilisateurDAO;
    private SessionManager sessionManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utilisateurDAO = new UtilisateurDAO();
        sessionManager = SessionManager.getInstance();
        lblError.setVisible(false);
        
        if (backgroundImage != null && backgroundImage.getParent() instanceof StackPane parent) {
            backgroundImage.fitWidthProperty().bind(parent.widthProperty());
            backgroundImage.fitHeightProperty().bind(parent.heightProperty());
        }
        
        txtPassword.setOnAction(event -> handleLogin());
    }
    
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs");
            return;
        }
        
        Optional<Utilisateur> userOpt = utilisateurDAO.authenticate(username, password);
        
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            
            sessionManager.startSession(user);
            
            System.out.println("User authenticated: " + user.getUsername() + " - Role: " + user.getRole().getLibelle());
            
            ouvrirMainView();
        } else {
            afficherErreur("Nom d'utilisateur ou mot de passe incorrect");
        }
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
            org.example.util.LoggerUtil.logError(LoginController.class, "Failed to load main view", e);
            afficherErreur("Erreur lors de l'ouverture de l'application");
        }
    }
    
    private void afficherErreur(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> lblError.setVisible(false));
            } catch (InterruptedException e) {
                org.example.util.LoggerUtil.logError(LoginController.class, "Error in error message timer", e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup_view.fxml"));
            Parent signupView = loader.load();
            
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(signupView, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("REB7A - Creer un compte");
        } catch (IOException e) {
            afficherErreur("Erreur lors du chargement de la page d'inscription");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        System.exit(0);
    }
}
