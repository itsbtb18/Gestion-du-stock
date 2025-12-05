package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.dao.UtilisateurDAO;
import org.example.model.entity.Utilisateur;
import org.example.util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * LoginController - Controller for user authentication
 * Now uses UtilisateurDAO with BCrypt password hashing
 * Part of the Controller layer in MVC architecture
 */
public class LoginController implements Initializable {
    
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;
    @FXML private CheckBox chkRememberMe;
    
    private UtilisateurDAO utilisateurDAO;
    private SessionManager sessionManager;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utilisateurDAO = new UtilisateurDAO();
        sessionManager = SessionManager.getInstance();
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
        
        // Authenticate with BCrypt password verification
        Optional<Utilisateur> userOpt = utilisateurDAO.authenticate(username, password);
        
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            
            // Start session
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
        
        // Hide error after 3 seconds
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
            // Create registration dialog
            Dialog<Utilisateur> dialog = new Dialog<>();
            dialog.setTitle("Créer un compte");
            dialog.setHeaderText("Inscription - Nouveau utilisateur");
            
            // Set dialog buttons
            ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
            
            // Create form fields
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
            
            TextField usernameField = new TextField();
            usernameField.setPromptText("Nom d'utilisateur");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Mot de passe");
            PasswordField confirmPasswordField = new PasswordField();
            confirmPasswordField.setPromptText("Confirmer mot de passe");
            TextField nomField = new TextField();
            nomField.setPromptText("Nom");
            TextField prenomField = new TextField();
            prenomField.setPromptText("Prénom");
            TextField emailField = new TextField();
            emailField.setPromptText("Email (optionnel)");
            
            grid.add(new Label("Nom d'utilisateur:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Mot de passe:"), 0, 1);
            grid.add(passwordField, 1, 1);
            grid.add(new Label("Confirmer mot de passe:"), 0, 2);
            grid.add(confirmPasswordField, 1, 2);
            grid.add(new Label("Nom:"), 0, 3);
            grid.add(nomField, 1, 3);
            grid.add(new Label("Prénom:"), 0, 4);
            grid.add(prenomField, 1, 4);
            grid.add(new Label("Email:"), 0, 5);
            grid.add(emailField, 1, 5);
            
            dialog.getDialogPane().setContent(grid);
            
            // Request focus on username field by default
            javafx.application.Platform.runLater(() -> usernameField.requestFocus());
            
            // Convert result when Create button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == createButtonType) {
                    // Validate inputs
                    if (usernameField.getText().trim().isEmpty() || 
                        passwordField.getText().isEmpty() ||
                        nomField.getText().trim().isEmpty() ||
                        prenomField.getText().trim().isEmpty()) {
                        afficherErreur("Veuillez remplir tous les champs obligatoires");
                        return null;
                    }
                    
                    if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                        afficherErreur("Les mots de passe ne correspondent pas");
                        return null;
                    }
                    
                    if (passwordField.getText().length() < 6) {
                        afficherErreur("Le mot de passe doit contenir au moins 6 caractères");
                        return null;
                    }
                    
                    // Create new user
                    Utilisateur newUser = new Utilisateur();
                    newUser.setUsername(usernameField.getText().trim());
                    newUser.setPassword(passwordField.getText()); // Will be hashed by DAO
                    newUser.setNom(nomField.getText().trim());
                    newUser.setPrenom(prenomField.getText().trim());
                    newUser.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
                    newUser.setRole(org.example.model.entity.Role.VENDEUR); // Default role
                    newUser.setActif(true);
                    
                    return newUser;
                }
                return null;
            });
            
            // Show dialog and handle result
            Optional<Utilisateur> result = dialog.showAndWait();
            
            result.ifPresent(user -> {
                try {
                    Utilisateur createdUser = utilisateurDAO.save(user);
                    if (createdUser != null) {
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Succès");
                        success.setHeaderText(null);
                        success.setContentText("Compte créé avec succès! Vous pouvez maintenant vous connecter.");
                        success.showAndWait();
                        
                        // Pre-fill username
                        txtUsername.setText(createdUser.getUsername());
                        txtPassword.clear();
                        txtPassword.requestFocus();
                    }
                } catch (Exception e) {
                    String errorMessage = "Erreur lors de la création du compte";
                    
                    // Check for specific errors
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("Unique index") || e.getMessage().contains("USERNAME")) {
                            errorMessage = "Ce nom d'utilisateur existe déjà. Veuillez en choisir un autre.";
                        } else if (e.getMessage().contains("email")) {
                            errorMessage = "Cette adresse email est déjà utilisée.";
                        } else {
                            errorMessage = "Erreur: " + e.getMessage();
                        }
                    }
                    
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Erreur");
                    error.setHeaderText("Création du compte échouée");
                    error.setContentText(errorMessage);
                    error.showAndWait();
                    
                    org.example.util.LoggerUtil.logError(LoginController.class, "Error creating user account", e);
                }
            });
            
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ouverture du formulaire d'inscription");
            org.example.util.LoggerUtil.logError(LoginController.class, "Error opening registration dialog", e);
        }
    }
    
    @FXML
    private void handleCancel() {
        System.exit(0);
    }
}
