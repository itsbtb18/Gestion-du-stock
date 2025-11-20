package com.supermarket;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Main Application Class for SuperMarket Management System
 * This is a complete UI-only JavaFX application with modern design
 */
public class Main extends Application {
    
    private Stage primaryStage;
    private BorderPane mainLayout;
    private StackPane contentArea;
    private NavigationSidebar sidebar;
    private HeaderBar header;
    
    // View instances
    private DashboardView dashboardView;
    private ProductsView productsView;
    private SalesView salesView;
    private CreditView creditView;
    private SuppliersView suppliersView;
    private StockMovementView stockMovementView;
    private ReportsView reportsView;
    private SettingsView settingsView;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Configure stage
        primaryStage.setTitle("SuperMarket - Gestion de Stock & Vente");
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);
        
        // Show splash screen first
        showSplashScreen();
    }
    
    private void showSplashScreen() {
        SplashView splashView = new SplashView(primaryStage, () -> {
            // After splash, show login
            showLoginScreen();
        });
        
        splashView.show();
    }
    
    private void showLoginScreen() {
        LoginView loginView = new LoginView(primaryStage, () -> {
            // After successful login, show main application
            showMainApplication();
        });
        
        loginView.show();
    }
    
    private void showMainApplication() {
        // Initialize main layout
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #F5F7FB;");
        
        // Initialize content area with StackPane for transitions
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        
        // Wrap content area in ScrollPane
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: #F5F7FB; -fx-background: #F5F7FB;");
        scrollPane.setPannable(true);
        
        // Initialize views
        initializeViews();
        
        // Create sidebar navigation
        sidebar = new NavigationSidebar(this);
        Region sidebarView = sidebar.getView();
        sidebarView.setMinWidth(250);
        sidebarView.setMaxWidth(250);
        mainLayout.setLeft(sidebarView);
        
        // Create header
        header = new HeaderBar();
        mainLayout.setTop(header.getView());
        
        // Set initial view
        mainLayout.setCenter(scrollPane);
        switchView("dashboard");
        
        // Create scene
        Scene scene = new Scene(mainLayout, 1400, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setScene(scene);
    }
    
    private void initializeViews() {
        dashboardView = new DashboardView();
        productsView = new ProductsView();
        salesView = new SalesView();
        creditView = new CreditView();
        suppliersView = new SuppliersView();
        stockMovementView = new StockMovementView();
        reportsView = new ReportsView();
        settingsView = new SettingsView();
    }
    
    /**
     * Switch between different views with fade transition
     */
    public void switchView(String viewName) {
        Pane newView = null;
        
        switch (viewName.toLowerCase()) {
            case "dashboard":
                newView = dashboardView.getView();
                header.updateTitle("Tableau de Bord");
                break;
            case "products":
                newView = productsView.getView();
                header.updateTitle("Gestion des Produits");
                break;
            case "sales":
                newView = salesView.getView();
                header.updateTitle("Point de Vente");
                break;
            case "credit":
                newView = creditView.getView();
                header.updateTitle("Gestion des Crédits");
                break;
            case "fournisseurs":
                newView = suppliersView.getView();
                header.updateTitle("Gestion des Fournisseurs");
                break;
            case "mouvements":
                newView = stockMovementView.getView();
                header.updateTitle("Mouvement de Stock");
                break;
            case "reports":
                newView = reportsView.getView();
                header.updateTitle("Rapports et Statistiques");
                break;
            case "settings":
                newView = settingsView.getView();
                header.updateTitle("Paramètres");
                break;
            default:
                newView = dashboardView.getView();
                header.updateTitle("Tableau de Bord");
        }
        
        if (newView != null) {
            ViewTransitions.fadeTransition(contentArea, newView);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
