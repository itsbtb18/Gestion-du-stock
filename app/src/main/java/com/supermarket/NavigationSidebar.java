package com.supermarket;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Navigation Sidebar Component with gradient background and animated menu items
 */
public class NavigationSidebar {
    
    private ScrollPane sidebarScrollPane;
    private VBox sidebar;
    private Main mainApp;
    private String activeView = "dashboard";
    
    private static final String[] MENU_ITEMS = {
        "Tableau de bord|dashboard|📊",
        "Produits|products|📦",
        "Ventes|sales|💰",
        "Crédit|credit|💳",
        "Fournisseurs|fournisseurs|🚚",
        "Mouvements|mouvements|📋",
        "Rapport|reports|📈",
        "Paramètres|settings|⚙️"
    };
    
    public NavigationSidebar(Main mainApp) {
        this.mainApp = mainApp;
        createSidebar();
    }
    
    private void createSidebar() {
        sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setMaxWidth(250);
        sidebar.setPadding(new Insets(20, 0, 20, 0));
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #1E3C72, #2A5298);"
        );
        sidebar.getStyleClass().add("sidebar");
        
        populateSidebarContent();
        
        sidebarScrollPane = new ScrollPane(sidebar);
        sidebarScrollPane.setMinWidth(250);
        sidebarScrollPane.setPrefWidth(250);
        sidebarScrollPane.setMaxWidth(250);
        sidebarScrollPane.setFitToWidth(true);
        sidebarScrollPane.setFitToHeight(true);
        sidebarScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sidebarScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sidebarScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        sidebarScrollPane.setPannable(true);
    }
    
    private void populateSidebarContent() {
        sidebar.getChildren().clear();
        
        VBox logoSection = createLogoSection();
        sidebar.getChildren().add(logoSection);
        
        Region spacer = new Region();
        spacer.setPrefHeight(30);
        sidebar.getChildren().add(spacer);
        
        for (String menuItem : MENU_ITEMS) {
            String[] parts = menuItem.split("\\|");
            String label = parts[0];
            String viewName = parts[1];
            String icon = parts[2];
            
            HBox menuButton = createMenuButton(icon, label, viewName);
            sidebar.getChildren().add(menuButton);
        }
        
        Region filler = new Region();
        VBox.setVgrow(filler, Priority.ALWAYS);
        sidebar.getChildren().add(filler);
    }
    
    private VBox createLogoSection() {
        VBox logoBox = new VBox(10);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(20, 0, 20, 0));
        
        // Logo circle with shopping cart icon
        Circle logoCircle = new Circle(35);
        logoCircle.setFill(Color.web("#FFB74D"));
        logoCircle.setEffect(createGlow());
        
        StackPane logoContainer = new StackPane();
        logoContainer.getChildren().add(logoCircle);
        
        Label logoIcon = new Label("🛒");
        logoIcon.setStyle("-fx-font-size: 32; -fx-text-fill: white;");
        logoContainer.getChildren().add(logoIcon);
        
        Label appName = new Label("SuperMarket");
        appName.setFont(Font.font("System", FontWeight.BOLD, 18));
        appName.setTextFill(Color.WHITE);
        appName.setStyle("-fx-text-fill: white;");
        
        logoBox.getChildren().addAll(logoContainer, appName);
        
        return logoBox;
    }
    
    private HBox createMenuButton(String icon, String label, String viewName) {
        HBox button = new HBox(15);
        button.setPadding(new Insets(12, 20, 12, 30));
        button.setAlignment(Pos.CENTER_LEFT);
        button.setCursor(Cursor.HAND);
        button.getStyleClass().add("menu-item");
        
        if (viewName.equals(activeView)) {
            button.getStyleClass().add("menu-item-active");
        }
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20; -fx-text-fill: white;");
        iconLabel.setMinWidth(30);
        iconLabel.setTextFill(Color.WHITE);
        
        // Label
        Label textLabel = new Label(label);
        textLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        textLabel.setTextFill(Color.WHITE);
        textLabel.setStyle("-fx-text-fill: white;");
        
        button.getChildren().addAll(iconLabel, textLabel);
        
        // Hover animation
        button.setOnMouseEntered(e -> {
            if (!viewName.equals(activeView)) {
                button.setStyle(button.getStyle() + "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 10;");
                animateScale(button, 1.0, 1.03);
            }
        });
        
        button.setOnMouseExited(e -> {
            if (!viewName.equals(activeView)) {
                button.setStyle("-fx-background-color: transparent;");
                animateScale(button, 1.03, 1.0);
            }
        });
        
        // Click handler
        button.setOnMouseClicked(e -> {
            setActiveView(viewName);
            mainApp.switchView(viewName);
        });
        
        return button;
    }
    
    private void setActiveView(String viewName) {
        activeView = viewName;
        populateSidebarContent();
    }
    
    private void animateScale(HBox node, double from, double to) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setFromX(from);
        st.setFromY(from);
        st.setToX(to);
        st.setToY(to);
        st.play();
    }
    
    private DropShadow createGlow() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#FFB74D", 0.6));
        glow.setRadius(20);
        glow.setSpread(0.4);
        return glow;
    }
    
    public Region getView() {
        // Ensure sidebar fills available space
        VBox.setVgrow(sidebar, Priority.ALWAYS);
        
        // Wrap in ScrollPane for scrolling
        ScrollPane scrollPane = new ScrollPane(sidebar);
        scrollPane.setMinWidth(250);
        scrollPane.setPrefWidth(250);
        scrollPane.setMaxWidth(250);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPannable(true);
        
        return scrollPane;
    }
}
