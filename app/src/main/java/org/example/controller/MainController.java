package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.example.model.service.MagasinService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * MainController - Main controller for navigation and view management
 * Part of the Controller layer in MVC architecture
 */
public class MainController implements Initializable {
    
    @FXML private BorderPane mainBorderPane;
    @FXML private StackPane contentArea;
    @FXML private Label lblUtilisateur;
    @FXML private Label lblRole;
    
    private MagasinService magasinService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        magasinService = MagasinService.getInstance();
        
        // Set default user info (TODO: get from session)
        lblUtilisateur.setText("Utilisateur: Admin");
        lblRole.setText("Rôle: Administrateur");
        
        // Load default view (Dashboard or Products)
        chargerVue("produit_view.fxml");
    }
    
    @FXML
    private void handleMenuProduits() {
        chargerVue("produit_view.fxml");
    }
    
    @FXML
    private void handleMenuCaisse() {
        chargerVue("caisse_view.fxml");
    }
    
    @FXML
    private void handleMenuStock() {
        chargerVue("stock_view.fxml");
    }
    
    @FXML
    private void handleMenuClients() {
        chargerVue("client_view.fxml");
    }
    
    @FXML
    private void handleMenuRapports() {
        chargerVue("rapport_view.fxml");
    }
    
    @FXML
    private void handleMenuStatistiques() {
        chargerVue("statistiques_view.fxml");
    }
    
    @FXML
    private void handleDeconnexion() {
        // TODO: Implement logout logic
        System.out.println("Déconnexion...");
    }
    
    /**
     * Load a view into the content area
     */
    private void chargerVue(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/" + fxmlFile)
            );
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
