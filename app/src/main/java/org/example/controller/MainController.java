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

public class MainController implements Initializable {
    
    @FXML private Button btnLogout;
    
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
    @FXML private Button btnSettings; 
    
    @FXML private StackPane contentArea;
    @FXML private VBox welcomeScreen;
    
    @FXML private Label lblTodaySales;
    @FXML private Label lblTodayRevenue;
    @FXML private Label lblTotalProducts;
    
    @FXML private Label lblDate;
    @FXML private Label lblRevenue;
    
    @FXML private Label lblStatus;
    @FXML private Label lblTime;
    
    private final StatistiquesService statistiquesService = StatistiquesService.getInstance();
    
    private Button currentActiveButton = null;
    private Timeline clockTimeline;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        updateDate();
        
        startClock();
        
        loadDashboardStats();
        
        handleDashboardView();
        
        Timeline revenueTimeline = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            loadDashboardStats();
        }));
        revenueTimeline.setCycleCount(Animation.INDEFINITE);
        revenueTimeline.play();
    }
    
    private void updateDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblDate.setText(today.format(formatter));
    }
    
    private void startClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime time = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            lblTime.setText(time.format(formatter));
        }));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }
    
    private void loadDashboardStats() {
        try {
            Map<String, Object> stats = statistiquesService.getDashboardStatistics();
            
            Integer salesCount = (Integer) stats.get("todaySalesCount");
            Double revenue = (Double) stats.get("todayRevenue");
            Integer productCount = (Integer) stats.get("totalProducts");
            
            lblTodaySales.setText(String.valueOf(salesCount != null ? salesCount : 0));
            lblTodayRevenue.setText(String.format("%.2f %s", revenue != null ? revenue : 0.0, org.example.app.AppConfig.CURRENCY_CODE));
            lblTotalProducts.setText(String.valueOf(productCount != null ? productCount : 0));
            
            lblRevenue.setText(String.format("%.2f %s", revenue != null ? revenue : 0.0, org.example.app.AppConfig.CURRENCY_CODE));
            
            lblStatus.setText("Dernière mise à jour: " + LocalTime.now().format(
                DateTimeFormatter.ofPattern("HH:mm:ss")));
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
            lblStatus.setText("Erreur de chargement des statistiques");
        }
    }
    
    @FXML
    private void handleDashboardView() {
        loadView("dashboard_view.fxml", "Tableau de Bord");
        setActiveButton(btnDashboard);
    }
    
    @FXML
    private void handleCaisseView() {
        loadView("caisse_pos_view.fxml", "Caisse");
        setActiveButton(btnCaisse);
    }
    
    @FXML
    private void handleClientsView() {
        loadView("client_view.fxml", "Clients");
        setActiveButton(btnClients);
    }
    
    @FXML
    private void handleProduitsView() {
        loadView("produit_view.fxml", "Produits");
        setActiveButton(btnProduits);
    }
    
    @FXML
    private void handleStockView() {
        loadView("stock_view.fxml", "Stock");
        setActiveButton(btnStock);
    }
    
    @FXML
    private void handleFournisseursView() {
        loadView("fournisseur_view.fxml", "Fournisseurs");
        setActiveButton(btnFournisseurs);
    }
    
    @FXML
    private void handleCommandesView() {
        loadView("bon_commande_view.fxml", "Bons de Commande");
        setActiveButton(btnCommandes);
    }
    
    @FXML
    private void handleRetoursView() {
        loadView("retour_view.fxml", "Retours");
        setActiveButton(btnRetours);
    }
    
    @FXML
    private void handleTransfertsView() {
        loadView("transfert_view.fxml", "Transferts");
        setActiveButton(btnTransferts);
    }
    
    @FXML
    private void handleDepensesView() {
        loadView("depense_view.fxml", "Dépenses");
        setActiveButton(btnDepenses);
    }
    
    @FXML
    private void handleRapportsView() {
        loadView("rapport_view.fxml", "Rapports");
        setActiveButton(btnRapports);
    }
    
    @FXML
    private void handleStatistiquesView() {
        loadView("statistiques_view.fxml", "Statistiques");
        setActiveButton(btnStatistiques);
    }
    
    @FXML
    private void handleSettingsView() {
        loadView("store_settings_view.fxml", "Paramètres du Magasin");
          setActiveButton(btnSettings);
    }
    
    @FXML
    private void handleLogout() {
        
        boolean confirmed = org.example.util.DialogManager.getInstance()
            .showConfirmation(
                "Déconnexion",
                "Êtes-vous sûr de vouloir vous déconnecter ?"
            );
        
        if (confirmed) {
            
            if (clockTimeline != null) {
                clockTimeline.stop();
            }
            
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
    
    private void loadView(String fxmlFile, String viewName) {
        try {
            
            if (welcomeScreen != null && welcomeScreen.isVisible()) {
                welcomeScreen.setVisible(false);
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent view = loader.load();
            
            javafx.scene.Node nodeToAdd;
            if (view instanceof javafx.scene.control.ScrollPane) {
                
                javafx.scene.control.ScrollPane existingScrollPane = (javafx.scene.control.ScrollPane) view;
                existingScrollPane.setFitToWidth(true);
                existingScrollPane.setFitToHeight(true);  
                existingScrollPane.prefWidthProperty().bind(contentArea.widthProperty());
                existingScrollPane.prefHeightProperty().bind(contentArea.heightProperty());
                nodeToAdd = existingScrollPane;
            } else {
                
                javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(view);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);  
                scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPane.setStyle("-fx-background-color: transparent;");
                scrollPane.getStyleClass().add("main-scroll");
                
                scrollPane.prefWidthProperty().bind(contentArea.widthProperty());
                scrollPane.prefHeightProperty().bind(contentArea.heightProperty());
                
                if (view instanceof javafx.scene.layout.Region) {
                    javafx.scene.layout.Region region = (javafx.scene.layout.Region) view;
                    region.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20)); 
                }
                
                nodeToAdd = scrollPane;
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(nodeToAdd);
            
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
    
    private void setActiveButton(Button button) {
        
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("nav-button-active");
        }
        
        if (!button.getStyleClass().contains("nav-button-active")) {
            button.getStyleClass().add("nav-button-active");
        }
        
        currentActiveButton = button;
    }
}
