package com.supermarket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;

/**
 * Stock Movement View - Mouvement de stock tracking
 */
public class StockMovementView {
    
    private VBox mainView;
    private TableView<StockMovement> movementsTable;
    
    public StockMovementView() {
        createStockMovementView();
    }
    
    private void createStockMovementView() {
        mainView = new VBox(20);
        mainView.setPadding(new Insets(0));
        
        // Title and filters
        VBox headerSection = createHeaderSection();
        
        // Movements table
        VBox tableContainer = createMovementsTable();
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        
        mainView.getChildren().addAll(headerSection, tableContainer);
    }
    
    private VBox createHeaderSection() {
        VBox section = new VBox(20);
        
        // Title
        Label title = new Label("Mouvement de Stock");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#222222"));
        
        // Filters card
        HBox filtersCard = new HBox(20);
        filtersCard.setPadding(new Insets(20));
        filtersCard.setAlignment(Pos.CENTER_LEFT);
        filtersCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        // Date range
        VBox dateFromBox = new VBox(5);
        Label dateFromLabel = new Label("Date de début:");
        dateFromLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        DatePicker dateFrom = new DatePicker(LocalDate.now().minusMonths(1));
        dateFrom.setPrefWidth(150);
        dateFromBox.getChildren().addAll(dateFromLabel, dateFrom);
        
        VBox dateToBox = new VBox(5);
        Label dateToLabel = new Label("Date de fin:");
        dateToLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        DatePicker dateTo = new DatePicker(LocalDate.now());
        dateTo.setPrefWidth(150);
        dateToBox.getChildren().addAll(dateToLabel, dateTo);
        
        // Type filter
        VBox typeBox = new VBox(5);
        Label typeLabel = new Label("Type de mouvement:");
        typeLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Tous", "Entrée", "Sortie", "Ajustement");
        typeCombo.setValue("Tous");
        typeCombo.setPrefWidth(150);
        typeBox.getChildren().addAll(typeLabel, typeCombo);
        
        // Search
        VBox searchBox = new VBox(5);
        Label searchLabel = new Label("Rechercher:");
        searchLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        TextField searchField = new TextField();
        searchField.setPromptText("Produit...");
        searchField.setPrefWidth(200);
        searchBox.getChildren().addAll(searchLabel, searchField);
        
        // Filter button
        VBox buttonBox = new VBox(5);
        Label spacerLabel = new Label(" ");
        spacerLabel.setFont(Font.font("System", 12));
        Button filterBtn = createGradientButton("🔍 Filtrer", "#4E54C8", "#8F94FB");
        filterBtn.setPrefHeight(35);
        buttonBox.getChildren().addAll(spacerLabel, filterBtn);
        
        filtersCard.getChildren().addAll(dateFromBox, dateToBox, typeBox, searchBox, buttonBox);
        
        section.getChildren().addAll(title, filtersCard);
        
        return section;
    }
    
    private VBox createMovementsTable() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(25));
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        // Header with stats
        HBox headerBox = new HBox(30);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 15, 0));
        
        Label tableTitle = new Label("Historique des Mouvements");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        tableTitle.setTextFill(Color.web("#222222"));
        
        HBox.setHgrow(tableTitle, Priority.ALWAYS);
        
        // Quick stats
        Label entryLabel = new Label("📥 Entrées: 45");
        entryLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        entryLabel.setTextFill(Color.web("#66BB6A"));
        
        Label exitLabel = new Label("📤 Sorties: 38");
        exitLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        exitLabel.setTextFill(Color.web("#E57373"));
        
        Label adjustLabel = new Label("⚙️ Ajustements: 7");
        adjustLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        adjustLabel.setTextFill(Color.web("#FFB74D"));
        
        headerBox.getChildren().addAll(tableTitle, entryLabel, exitLabel, adjustLabel);
        
        // Table
        movementsTable = new TableView<>();
        movementsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<StockMovement, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(120);
        
        TableColumn<StockMovement, String> productCol = new TableColumn<>("Produit");
        productCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        productCol.setPrefWidth(200);
        
        TableColumn<StockMovement, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);
        
        TableColumn<StockMovement, Integer> quantityCol = new TableColumn<>("Quantité");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(100);
        
        TableColumn<StockMovement, String> reasonCol = new TableColumn<>("Raison");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reasonCol.setPrefWidth(250);
        
        TableColumn<StockMovement, String> userCol = new TableColumn<>("Utilisateur");
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        userCol.setPrefWidth(120);
        
        movementsTable.getColumns().addAll(dateCol, productCol, typeCol, quantityCol, reasonCol, userCol);
        
        // Mock data
        ObservableList<StockMovement> data = FXCollections.observableArrayList(
            new StockMovement("18/11/2025 14:30", "Lait Candia 1L", "Sortie", -10, "Vente au comptoir", "Admin"),
            new StockMovement("18/11/2025 12:15", "Pain Blanc", "Sortie", -25, "Vente au comptoir", "Admin"),
            new StockMovement("18/11/2025 10:00", "Huile Elio 2L", "Entrée", +50, "Livraison fournisseur", "Admin"),
            new StockMovement("17/11/2025 16:45", "Café Legal 250g", "Sortie", -5, "Vente au comptoir", "Vendeur1"),
            new StockMovement("17/11/2025 14:20", "Sucre Blanc 1kg", "Ajustement", +15, "Correction inventaire", "Admin"),
            new StockMovement("17/11/2025 11:30", "Yaourt Danone", "Entrée", +30, "Livraison fournisseur", "Admin"),
            new StockMovement("17/11/2025 09:00", "Pâtes Tria", "Sortie", -12, "Vente au comptoir", "Vendeur2"),
            new StockMovement("16/11/2025 15:10", "Jus Ramy", "Entrée", +40, "Livraison fournisseur", "Admin"),
            new StockMovement("16/11/2025 13:25", "Fromage VQR", "Ajustement", -3, "Produit périmé", "Admin"),
            new StockMovement("16/11/2025 10:50", "Biscuit Bimo", "Sortie", -18, "Vente au comptoir", "Vendeur1"),
            new StockMovement("15/11/2025 16:00", "Lait Candia 1L", "Entrée", +100, "Livraison fournisseur", "Admin"),
            new StockMovement("15/11/2025 14:30", "Huile Elio 2L", "Sortie", -8, "Vente au comptoir", "Vendeur2"),
            new StockMovement("15/11/2025 11:45", "Café Legal 250g", "Sortie", -6, "Vente au comptoir", "Admin"),
            new StockMovement("14/11/2025 15:20", "Pain Blanc", "Sortie", -30, "Vente au comptoir", "Vendeur1"),
            new StockMovement("14/11/2025 09:30", "Sucre Blanc 1kg", "Entrée", +80, "Livraison fournisseur", "Admin")
        );
        
        movementsTable.setItems(data);
        VBox.setVgrow(movementsTable, Priority.ALWAYS);
        
        container.getChildren().addAll(headerBox, movementsTable);
        
        return container;
    }
    
    private Button createGradientButton(String text, String colorFrom, String colorTo) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        button.setTextFill(Color.WHITE);
        button.setCursor(Cursor.HAND);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + colorFrom + ", " + colorTo + "); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 10 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
        );
        
        return button;
    }
    
    public VBox getView() {
        return mainView;
    }
    
    // Inner class for StockMovement data
    public static class StockMovement {
        private String date;
        private String product;
        private String type;
        private int quantity;
        private String reason;
        private String user;
        
        public StockMovement(String date, String product, String type, int quantity, String reason, String user) {
            this.date = date;
            this.product = product;
            this.type = type;
            this.quantity = quantity;
            this.reason = reason;
            this.user = user;
        }
        
        public String getDate() { return date; }
        public String getProduct() { return product; }
        public String getType() { return type; }
        public int getQuantity() { return quantity; }
        public String getReason() { return reason; }
        public String getUser() { return user; }
    }
}
