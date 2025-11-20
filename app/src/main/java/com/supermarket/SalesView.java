package com.supermarket;

import org.example.model.CreditSale;
import javafx.animation.FadeTransition;
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
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Sales View with POS (Point of Sale) interface
 */
public class SalesView {
    
    private VBox mainView;
    private TableView<SaleItem> saleItemsTable;
    private Label totalLabel;
    private ObservableList<SaleItem> currentSaleItems;
    private double currentTotal = 0.0;
    private final Random random = new Random();
    private static List<CreditSale> creditSales = new ArrayList<>(); // Shared credit sales list
    
    public SalesView() {
        currentSaleItems = FXCollections.observableArrayList();
        createSalesView();
    }
    
    public static List<CreditSale> getCreditSales() {
        return creditSales;
    }
    
    private void createSalesView() {
        mainView = new VBox(20);
        mainView.setPadding(new Insets(0));
        
        // Title
        Label title = new Label("Point de Vente");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#222222"));
        
        // Main content area with POS and sales history
        HBox contentArea = createContentArea();
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        
        mainView.getChildren().addAll(title, contentArea);
    }
    
    private HBox createContentArea() {
        HBox contentArea = new HBox(20);
        
        // Left: POS Panel
        VBox posPanel = createPOSPanel();
        HBox.setHgrow(posPanel, Priority.ALWAYS);
        
        // Right: Recent Sales
        VBox salesHistoryPanel = createSalesHistoryPanel();
        salesHistoryPanel.setPrefWidth(450);
        
        contentArea.getChildren().addAll(posPanel, salesHistoryPanel);
        
        return contentArea;
    }
    
    private VBox createPOSPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        // Sale type selector
        HBox saleTypeSelector = createSaleTypeSelector();
        
        // Product search section
        HBox searchSection = new HBox(10);
        searchSection.setAlignment(Pos.CENTER_LEFT);
        
        TextField productSearch = new TextField();
        productSearch.setPromptText("🔍 Rechercher ou scanner produit...");
        productSearch.setPrefHeight(45);
        HBox.setHgrow(productSearch, Priority.ALWAYS);
        productSearch.setStyle(
            "-fx-background-color: #F5F7FB; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 10 20; " +
            "-fx-font-size: 14;"
        );
        
        Label scanStatus = new Label("Prêt pour le scan.");
        scanStatus.setTextFill(Color.web("#4E54C8"));
        scanStatus.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        scanStatus.setWrapText(true);
        
        Button scanButton = createActionButton("📷 Scanner", "#4E54C8", "#8F94FB");
        scanButton.setOnAction(e -> {
            String scannedCode = "SCN-" + (100000 + random.nextInt(900000));
            productSearch.setText(scannedCode);
            scanStatus.setText("Code scanné : " + scannedCode + " (simulation) – article ajouté au panier");
            addMockItemToSale(scannedCode);
        });
        
        Button addButton = createActionButton("➕ Ajouter", "#66BB6A", "#A5D6A7");
        addButton.setOnAction(e -> {
            addMockItemToSale();
            scanStatus.setText("Produit ajouté manuellement (simulation)");
        });
        
        searchSection.getChildren().addAll(productSearch, scanButton, addButton);
        
        VBox searchContainer = new VBox(6);
        searchContainer.getChildren().addAll(searchSection, scanStatus);
        
        // Sale items table
        VBox tableContainer = createSaleItemsTable();
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        
        // Quick add products grid
        GridPane quickAddGrid = createQuickAddGrid();
        
        // Total section
        VBox totalSection = createTotalSection();
        
        // Action buttons
        HBox actionButtons = createActionButtons();
        
        panel.getChildren().addAll(saleTypeSelector, searchContainer, tableContainer, quickAddGrid, totalSection, actionButtons);
        
        return panel;
    }
    
    private HBox createSaleTypeSelector() {
        HBox selector = new HBox(15);
        selector.setAlignment(Pos.CENTER);
        selector.setPadding(new Insets(15));
        selector.setStyle(
            "-fx-background-color: #F5F7FB; " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: #4E54C8; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 12;"
        );
        
        Label selectorLabel = new Label("Type de Vente:");
        selectorLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        selectorLabel.setTextFill(Color.web("#222222"));
        
        Button ventePaidBtn = createSaleTypeButton("💵 Vente Payée", "#66BB6A", "#A5D6A7");
        ventePaidBtn.setPrefWidth(180);
        ventePaidBtn.setPrefHeight(45);
        ventePaidBtn.setOnAction(e -> handlePaidSale());
        
        Button venteCreditBtn = createSaleTypeButton("💳 Vente Crédit", "#FF6B6B", "#FFB74D");
        venteCreditBtn.setPrefWidth(180);
        venteCreditBtn.setPrefHeight(45);
        venteCreditBtn.setOnAction(e -> handleCreditSale());
        
        selector.getChildren().addAll(selectorLabel, ventePaidBtn, venteCreditBtn);
        
        return selector;
    }
    
    private Button createSaleTypeButton(String text, String colorFrom, String colorTo) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setCursor(Cursor.HAND);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + colorFrom + ", " + colorTo + "); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 10 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + colorFrom + ", " + colorTo + "); " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 15, 0, 0, 5); " +
                "-fx-scale-x: 1.05; " +
                "-fx-scale-y: 1.05;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + colorFrom + ", " + colorTo + "); " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"
            );
        });
        
        return button;
    }
    
    private void handlePaidSale() {
        if (currentSaleItems.isEmpty()) {
            showAlert("Panier vide", "Veuillez ajouter des articles avant de valider la vente.", Alert.AlertType.WARNING);
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Vente Payée");
        alert.setHeaderText("✅ Vente enregistrée avec succès!");
        alert.setContentText(String.format("Montant total: %.2f DA\nPaiement: Reçu\nStock: Mis à jour", currentTotal));
        alert.showAndWait();
        clearSale();
    }
    
    private void handleCreditSale() {
        if (currentSaleItems.isEmpty()) {
            showAlert("Panier vide", "Veuillez ajouter des articles avant de créer une vente à crédit.", Alert.AlertType.WARNING);
            return;
        }
        
        // Convert current sale items to a list
        List<SaleItem> items = new ArrayList<>(currentSaleItems);
        
        // Open credit sale dialog
        CreditSaleDialog dialog = new CreditSaleDialog(items, currentTotal, creditSale -> {
            // Save credit sale
            creditSales.add(creditSale);
            
            // Show success message with option to view credit list
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Vente à Crédit Enregistrée");
            successAlert.setHeaderText("✅ Vente à crédit créée avec succès!");
            successAlert.setContentText(
                String.format("Client: %s\nMontant: %.2f DA\nStatut: %s\n\nLe stock a été mis à jour.",
                    creditSale.getFullName(),
                    creditSale.getTotalAmount(),
                    creditSale.getStatus())
            );
            
            ButtonType viewCreditsBtn = new ButtonType("Voir la Liste des Crédits");
            ButtonType closeBtn = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
            successAlert.getButtonTypes().setAll(viewCreditsBtn, closeBtn);
            
            successAlert.showAndWait().ifPresent(response -> {
                if (response == viewCreditsBtn) {
                    // Switch to credit view (will be implemented in Main.java)
                    // For now, just show a toast
                    showAlert("Navigation", "Naviguez vers la section Crédit pour voir tous les crédits.", Alert.AlertType.INFORMATION);
                }
            });
            
            clearSale();
        });
        
        dialog.show();
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private VBox createSaleItemsTable() {
        VBox container = new VBox(10);
        
        Label tableTitle = new Label("Articles de la vente");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        tableTitle.setTextFill(Color.web("#555555"));
        
        saleItemsTable = new TableView<>();
        saleItemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        saleItemsTable.setItems(currentSaleItems);
        saleItemsTable.setPlaceholder(new Label("Aucun article ajouté"));
        saleItemsTable.setPrefHeight(180);
        saleItemsTable.setMaxHeight(180);
        
        TableColumn<SaleItem, String> productCol = new TableColumn<>("Produit");
        productCol.setCellValueFactory(new PropertyValueFactory<>("product"));
        productCol.setPrefWidth(250);
        
        TableColumn<SaleItem, Integer> quantityCol = new TableColumn<>("Qté");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(60);
        
        TableColumn<SaleItem, String> priceCol = new TableColumn<>("Prix Unit.");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);
        
        TableColumn<SaleItem, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(100);
        
        TableColumn<SaleItem, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(80);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑️");
            {
                deleteBtn.setStyle(
                    "-fx-background-color: #E57373; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 5; " +
                    "-fx-cursor: hand;"
                );
                deleteBtn.setOnAction(e -> {
                    SaleItem item = getTableView().getItems().get(getIndex());
                    removeItemFromSale(item);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
        
        saleItemsTable.getColumns().addAll(productCol, quantityCol, priceCol, totalCol, actionCol);
        
        container.getChildren().addAll(tableTitle, saleItemsTable);
        
        return container;
    }
    
    private GridPane createQuickAddGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        
        Label quickAddLabel = new Label("Ajout Rapide:");
        quickAddLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        quickAddLabel.setTextFill(Color.web("#555555"));
        
        Button[] quickButtons = {
            createQuickAddButton("🥛 Lait", 100),
            createQuickAddButton("🍞 Pain", 20),
            createQuickAddButton("☕ Café", 350),
            createQuickAddButton("🧃 Jus", 60)
        };
        
        grid.add(quickAddLabel, 0, 0, 4, 1);
        grid.add(quickButtons[0], 0, 1);
        grid.add(quickButtons[1], 1, 1);
        grid.add(quickButtons[2], 2, 1);
        grid.add(quickButtons[3], 3, 1);
        
        return grid;
    }
    
    private Button createQuickAddButton(String text, double price) {
        Button btn = new Button(text);
        btn.setPrefWidth(90);
        btn.setPrefHeight(35);
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        btn.setCursor(Cursor.HAND);
        btn.setStyle(
            "-fx-background-color: #F5F7FB; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #4E54C8; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 8;"
        );
        
        btn.setOnAction(e -> addQuickItem(text, price));
        
        return btn;
    }
    
    private void addQuickItem(String productName, double price) {
        SaleItem item = new SaleItem(productName, 1, price + " DA", price + " DA");
        currentSaleItems.add(item);
        currentTotal += price;
        updateTotal();
        animateItemAdded();
    }
    
    private void addMockItemToSale() {
        addMockItemToSale(null);
    }
    
    private void addMockItemToSale(String scannedCode) {
        String[] products = {"Sucre 1kg", "Huile 2L", "Pâtes", "Biscuit"};
        double[] prices = {130, 420, 85, 75};
        
        int index = random.nextInt(products.length);
        String productName = products[index];
        double price = prices[index];
        
        if (scannedCode != null) {
            productName = "Scan " + productName;
        }
        
        SaleItem item = new SaleItem(productName, 1, price + " DA", price + " DA");
        currentSaleItems.add(item);
        currentTotal += price;
        updateTotal();
        animateItemAdded();
    }
    
    private void removeItemFromSale(SaleItem item) {
        currentSaleItems.remove(item);
        String totalStr = item.getTotal().replace(" DA", "");
        currentTotal -= Double.parseDouble(totalStr);
        updateTotal();
    }
    
    private void updateTotal() {
        totalLabel.setText(String.format("%.2f DA", currentTotal));
    }
    
    private void animateItemAdded() {
        FadeTransition ft = new FadeTransition(Duration.millis(300), saleItemsTable);
        ft.setFromValue(0.7);
        ft.setToValue(1.0);
        ft.play();
    }
    
    private VBox createTotalSection() {
        VBox section = new VBox(8);
        section.setPadding(new Insets(15));
        section.setAlignment(Pos.CENTER);
        section.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #4E54C8, #8F94FB); " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        
        Label totalTitleLabel = new Label("TOTAL À PAYER");
        totalTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        totalTitleLabel.setTextFill(Color.web("#FFFFFF", 0.9));
        
        totalLabel = new Label("0.00 DA");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        totalLabel.setTextFill(Color.WHITE);
        
        section.getChildren().addAll(totalTitleLabel, totalLabel);
        
        return section;
    }
    
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button cancelBtn = createActionButton("🗑️ Vider le Panier", "#E57373", "#EF9A9A");
        cancelBtn.setPrefWidth(200);
        cancelBtn.setPrefHeight(45);
        cancelBtn.setOnAction(e -> {
            if (!currentSaleItems.isEmpty()) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setHeaderText("Vider le panier?");
                confirmAlert.setContentText("Êtes-vous sûr de vouloir vider le panier?");
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        clearSale();
                    }
                });
            }
        });
        
        buttonBox.getChildren().add(cancelBtn);
        
        return buttonBox;
    }
    
    private void clearSale() {
        currentSaleItems.clear();
        currentTotal = 0.0;
        updateTotal();
    }
    
    private VBox createSalesHistoryPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25));
        panel.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label title = new Label("Dernières Ventes");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#222222"));
        
        TableView<SaleHistory> historyTable = new TableView<>();
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<SaleHistory, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<SaleHistory, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);
        
        TableColumn<SaleHistory, String> clientCol = new TableColumn<>("Client");
        clientCol.setCellValueFactory(new PropertyValueFactory<>("client"));
        clientCol.setPrefWidth(100);
        
        TableColumn<SaleHistory, String> amountCol = new TableColumn<>("Montant");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(100);
        
        TableColumn<SaleHistory, String> paymentCol = new TableColumn<>("Paiement");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("payment"));
        paymentCol.setPrefWidth(80);
        
        historyTable.getColumns().addAll(idCol, dateCol, clientCol, amountCol, paymentCol);
        
        // Mock data
        ObservableList<SaleHistory> data = FXCollections.observableArrayList(
            new SaleHistory("V001", "18/11/2025", "Client A", "1,250 DA", "💳 Carte"),
            new SaleHistory("V002", "18/11/2025", "Client B", "850 DA", "💵 Espèces"),
            new SaleHistory("V003", "18/11/2025", "Anonymous", "2,100 DA", "💳 Carte"),
            new SaleHistory("V004", "18/11/2025", "Client C", "450 DA", "💵 Espèces"),
            new SaleHistory("V005", "18/11/2025", "Client D", "3,200 DA", "💳 Carte"),
            new SaleHistory("V006", "18/11/2025", "Anonymous", "675 DA", "💵 Espèces"),
            new SaleHistory("V007", "17/11/2025", "Client E", "1,890 DA", "💳 Carte")
        );
        
        historyTable.setItems(data);
        VBox.setVgrow(historyTable, Priority.ALWAYS);
        
        panel.getChildren().addAll(title, historyTable);
        
        return panel;
    }
    
    private Button createActionButton(String text, String colorFrom, String colorTo) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
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
    
    // Inner classes for data
    public static class SaleItem {
        private String product;
        private int quantity;
        private String price;
        private String total;
        
        public SaleItem(String product, int quantity, String price, String total) {
            this.product = product;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
        }
        
        public String getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public String getPrice() { return price; }
        public String getTotal() { return total; }
    }
    
    public static class SaleHistory {
        private String id;
        private String date;
        private String client;
        private String amount;
        private String payment;
        
        public SaleHistory(String id, String date, String client, String amount, String payment) {
            this.id = id;
            this.date = date;
            this.client = client;
            this.amount = amount;
            this.payment = payment;
        }
        
        public String getId() { return id; }
        public String getDate() { return date; }
        public String getClient() { return client; }
        public String getAmount() { return amount; }
        public String getPayment() { return payment; }
    }
}
