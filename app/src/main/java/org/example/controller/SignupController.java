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
import org.example.model.entity.Role;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class SignupController implements Initializable {
    
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblError;
    @FXML private Label lblSuccess;
    @FXML private Button btnSignup;
    @FXML private ImageView backgroundImage;
    
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        if (backgroundImage != null && backgroundImage.getParent() instanceof StackPane parent) {
            backgroundImage.fitWidthProperty().bind(parent.widthProperty());
            backgroundImage.fitHeightProperty().bind(parent.heightProperty());
        }
    }
    
    @FXML
    private void handleSignup() {
        
        lblError.setVisible(false);
        lblSuccess.setVisible(false);
        
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || 
            username.isEmpty() || password.isEmpty()) {
            showError("Tous les champs sont obligatoires");
            return;
        }
        
        if (nom.length() < 2) {
            showError("Le nom doit contenir au moins 2 caracteres");
            return;
        }
        
        if (prenom.length() < 2) {
            showError("Le prenom doit contenir au moins 2 caracteres");
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            showError("Entrez une adresse email valide");
            return;
        }
        
        if (username.length() < 3) {
            showError("Le nom d'utilisateur doit contenir au moins 3 caracteres");
            return;
        }
        
        if (password.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caracteres");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Les mots de passe ne correspondent pas");
            return;
        }
        
        if (utilisateurDAO.findByUsername(username).isPresent()) {
            showError("Ce nom d'utilisateur existe deja");
            return;
        }
        
        try {
            
            Utilisateur newUser = new Utilisateur();
            newUser.setUsername(username);
            newUser.setPassword(password); 
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setEmail(email);
            newUser.setRole(Role.CAISSIER); 
            newUser.setDateCreation(LocalDateTime.now());
            newUser.setActif(true);
            
            Utilisateur savedUser = utilisateurDAO.save(newUser);
            
            if (savedUser != null && savedUser.getId() != null) {
                showSuccess("Compte cree avec succes! Redirection en cours...");
                
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::handleBackToLogin);
                    } catch (InterruptedException e) {
                        javafx.application.Platform.runLater(this::handleBackToLogin);
                    }
                }).start();
            } else {
                showError("Erreur lors de la creation du compte");
            }
            
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
            Parent loginView = loader.load();
            
            Stage stage = (Stage) btnSignup.getScene().getWindow();
            Scene scene = new Scene(loginView, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("REB7A - Connexion");
        } catch (IOException e) {
            showError("Erreur lors du chargement de la page de connexion");
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        lblSuccess.setVisible(false);
    }
    
    private void showSuccess(String message) {
        lblSuccess.setText(message);
        lblSuccess.setVisible(true);
        lblError.setVisible(false);
    }
}
