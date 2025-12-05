package org.example.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.model.service.StatistiquesService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * MainController - Main controller for navigation and view management
 * Part of the Controller layer in MVC architecture
 */
public class MainController implements Initializable {
    
    // FXML Components - Header
    @FXML private Label lblUsername;
    @FXML private Label lblRole;
    @FXML private Button btnLogout;
    
    // FXML Components - Navigation Buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnCaisse;
    @FXML private Button btnClients;
    @FXML private Button btnProduits;
    @FXML private Button btnStock;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnCommandes;
    @FXML private Button btnRetours;
    @FXML private Button btnTransferts;
    @FXML private Button btnDepenses;
    @FXML private Button btnRapports;
    @FXML private Button btnStatistiques;
    
    // FXML Components - Content Area
    @FXML private StackPane contentArea;
    @FXML private VBox welcomeScreen;
    
    // FXML Components - Dashboard Stats
    @FXML private Label lblTodaySales;
    @FXML private Label lblTodayRevenue;
    @FXML private Label lblTotalProducts;
    
    // FXML Components - Sidebar Footer
    @FXML private Label lblDate;
    @FXML private Label lblRevenue;
    
    // FXML Components - Status Bar
    @FXML private Label lblStatus;
    @FXML private Label lblTime;
    
    // Services
    private final StatistiquesService statistiquesService = StatistiquesService.getInstance();
    
    // State
    private Button currentActiveButton = null;
    private Timeline clockTimeline;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set user info - in production, get from authenticated session
        lblUsername.setText("Admin User");
        lblRole.setText("Administrateur");
        
        // Set date
        updateDate();
        
        // Start clock
        startClock();
        
        // Load dashboard statistics
        loadDashboardStats();
        
        // Load dashboard view by default
        handleDashboardView();
        
        // Update revenue periodically
        Timeline revenueTimeline = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            loadDashboardStats();
        }));
        revenueTimeline.setCycleCount(Animation.INDEFINITE);
        revenueTimeline.play();
    }
    
    /**
     * Update date display
     */
    private void updateDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblDate.setText(today.format(formatter));
    }
    
    /**
     * Start real-time clock
     */
    private void startClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime time = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            lblTime.setText(time.format(formatter));
        }));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }
    
    /**
     * Load dashboard statistics
     */
    private void loadDashboardStats() {
        try {
            Map<String, Object> stats = statistiquesService.getDashboardStatistics();
            
            // Update dashboard
            Integer salesCount = (Integer) stats.get("todaySalesCount");
            Double revenue = (Double) stats.get("todayRevenue");
            Integer productCount = (Integer) stats.get("totalProducts");
            
            lblTodaySales.setText(String.valueOf(salesCount != null ? salesCount : 0));
            lblTodayRevenue.setText(String.format("%.2f DH", revenue != null ? revenue : 0.0));
            lblTotalProducts.setText(String.valueOf(productCount != null ? productCount : 0));
            
            // Update sidebar footer
            lblRevenue.setText(String.format("%.2f DH", revenue != null ? revenue : 0.0));
            
            lblStatus.setText("Dernière mise à jour: " + LocalTime.now().format(
                DateTimeFormatter.ofPattern("HH:mm:ss")));
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
            lblStatus.setText("Erreur de chargement des statistiques");
        }
    }
    
    /**
     * Handle Dashboard view
     */
    @FXML
    private void handleDashboardView() {
        loadView("dashboard_view.fxml", "Tableau de Bord");
        setActiveButton(btnDashboard);
    }
    
    /**
     * Handle Caisse view
     */
    @FXML
    private void handleCaisseView() {
        loadView("caisse_view.fxml", "Caisse");
        setActiveButton(btnCaisse);
    }
    
    /**
     * Handle Clients view
     */
    @FXML
    private void handleClientsView() {
        loadView("client_view.fxml", "Clients");
        setActiveButton(btnClients);
    }
    
    /**
     * Handle Produits view
     */
    @FXML
    private void handleProduitsView() {
        loadView("produit_view.fxml", "Produits");
        setActiveButton(btnProduits);
    }
    
    /**
     * Handle Stock view
     */
    @FXML
    private void handleStockView() {
        loadView("stock_view.fxml", "Stock");
        setActiveButton(btnStock);
    }
    
    /**
     * Handle Fournisseurs view
     */
    @FXML
    private void handleFournisseursView() {
        loadView("fournisseur_view.fxml", "Fournisseurs");
        setActiveButton(btnFournisseurs);
    }
    
    /**
     * Handle Commandes view
     */
    @FXML
    private void handleCommandesView() {
        loadView("bon_commande_view.fxml", "Bons de Commande");
        setActiveButton(btnCommandes);
    }
    
    /**
     * Handle Retours view
     */
    @FXML
    private void handleRetoursView() {
        loadView("retour_view.fxml", "Retours");
        setActiveButton(btnRetours);
    }
    
    /**
     * Handle Transferts view
     */
    @FXML
    private void handleTransfertsView() {
        loadView("transfert_view.fxml", "Transferts");
        setActiveButton(btnTransferts);
    }
    
    /**
     * Handle Dépenses view
     */
    @FXML
    private void handleDepensesView() {
        loadView("depense_view.fxml", "Dépenses");
        setActiveButton(btnDepenses);
    }
    
    /**
     * Handle Rapports view
     */
    @FXML
    private void handleRapportsView() {
        loadView("rapport_view.fxml", "Rapports");
        setActiveButton(btnRapports);
    }
    
    /**
     * Handle Statistiques view
     */
    @FXML
    private void handleStatistiquesView() {
        loadView("statistiques_view.fxml", "Statistiques");
        setActiveButton(btnStatistiques);
    }
    
    /**
     * Handle Settings view
     */
    @FXML
    private void handleSettingsView() {
        loadView("store_settings_view.fxml", "Paramètres du Magasin");
    }
    
    /**
     * Handle logout
     */
    @FXML
    private void handleLogout() {
        // Use DialogManager for confirmation
        boolean confirmed = org.example.util.DialogManager.getInstance()
            .showConfirmation(
                "Déconnexion",
                "Êtes-vous sûr de vouloir vous déconnecter ?"
            );
        
        if (confirmed) {
            // Stop timelines
            if (clockTimeline != null) {
                clockTimeline.stop();
            }
            
            // Return to login screen
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login_view.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) btnLogout.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("REB7A - Connexion");
                stage.centerOnScreen();
                stage.show();
                
            } catch (IOException e) {
                org.example.util.LoggerUtil.logError(MainController.class, "Error loading login view", e);
                org.example.util.DialogManager.getInstance()
                    .showError(
                        "Erreur",
                        "Impossible de charger l'écran de connexion",
                        e
                    );
            }
        }
    }
    
    /**
     * Load a view into content area
     */
    private void loadView(String fxmlFile, String viewName) {
        try {
            // Hide welcome screen
            if (welcomeScreen != null && welcomeScreen.isVisible()) {
                welcomeScreen.setVisible(false);
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent view = loader.load();
            
            // Wrap view in ScrollPane for scrolling capability
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(view);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setStyle("-fx-background-color: transparent;");
            
            // Make the view take full available space
            if (view instanceof javafx.scene.layout.Region) {
                javafx.scene.layout.Region region = (javafx.scene.layout.Region) view;
                region.prefWidthProperty().bind(contentArea.widthProperty());
                region.prefHeightProperty().bind(contentArea.heightProperty());
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(scrollPane);
            
            lblStatus.setText(viewName + " chargé avec succès");
            
        } catch (IOException e) {
            org.example.util.LoggerUtil.logError(MainController.class, "Error loading view: " + fxmlFile, e);
            lblStatus.setText("Erreur: Impossible de charger " + viewName);
            
            org.example.util.DialogManager.getInstance()
                .showError(
                    "Erreur de chargement",
                    "Impossible de charger la vue " + viewName,
                    e
                );
        }
    }
    
    /**
     * Set active navigation button
     */
    private void setActiveButton(Button button) {
        // Remove active style from previous button
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("nav-button-active");
        }
        
        // Add active style to new button
        if (!button.getStyleClass().contains("nav-button-active")) {
            button.getStyleClass().add("nav-button-active");
        }
        
        currentActiveButton = button;
    }
}
