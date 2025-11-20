package com.supermarket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Random;

/**
 * Products View with table and add/edit panel
 */
public class ProductsView {
    
    private VBox mainView;
    private TableView<Product> table;
    private StackPane overlay;
    private boolean isAddPanelVisible = false;
    private final Random random = new Random();
    
    public ProductsView() {
        createProductsView();
    }
    
    private void createProductsView() {
        mainView = new VBox(20);
        mainView.setPadding(new Insets(0));
        
        // Create overlay for add/edit panel
        overlay = new StackPane();
        
        // Top toolbar
        HBox toolbar = createToolbar();
        
        // Products table
        VBox tableContainer = createProductsTable();
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        
        mainView.getChildren().addAll(toolbar, tableContainer);
    }
    
    private HBox createToolbar() {
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0));
        
        // Title
        Label title = new Label("Gestion des Produits");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#222222"));
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Action buttons
        Button addBtn = createGradientButton("➕ Ajouter Produit", "#66BB6A", "#A5D6A7");
        addBtn.setOnAction(e -> showAddProductPanel());
        
        Button importBtn = createGradientButton("📥 Importer", "#4E54C8", "#8F94FB");
        Button exportBtn = createGradientButton("📤 Exporter", "#FFB74D", "#FFE082");
        
        // Search and filters
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Rechercher produit...");
        searchField.setPrefWidth(250);
        searchField.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 10 20; " +
            "-fx-font-size: 13; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Catégorie");
        categoryFilter.getItems().addAll("Toutes", "Produits laitiers", "Épicerie", "Boissons", "Boulangerie");
        categoryFilter.setPrefWidth(150);
        categoryFilter.setStyle("-fx-background-radius: 10;");
        
        ComboBox<String> sortFilter = new ComboBox<>();
        sortFilter.setPromptText("Trier par");
        sortFilter.getItems().addAll("Nom", "Prix", "Stock", "Date");
        sortFilter.setPrefWidth(120);
        sortFilter.setStyle("-fx-background-radius: 10;");
        
        toolbar.getChildren().addAll(title, spacer, searchField, categoryFilter, sortFilter, addBtn, importBtn, exportBtn);
        
        return toolbar;
    }
    
    private VBox createProductsTable() {
        VBox container = new VBox(0);
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-radius: 15;");
        
        // Columns
        TableColumn<Product, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeCol.setPrefWidth(80);
        
        TableColumn<Product, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Product, String> categoryCol = new TableColumn<>("Catégorie");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(150);
        
        TableColumn<Product, String> buyPriceCol = new TableColumn<>("Prix d'achat");
        buyPriceCol.setCellValueFactory(new PropertyValueFactory<>("buyPrice"));
        buyPriceCol.setPrefWidth(120);
        
        TableColumn<Product, String> sellPriceCol = new TableColumn<>("Prix de vente");
        sellPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        sellPriceCol.setPrefWidth(120);
        
        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockCol.setPrefWidth(80);
        
        TableColumn<Product, String> supplierCol = new TableColumn<>("Fournisseur");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        supplierCol.setPrefWidth(150);
        
        TableColumn<Product, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        table.getColumns().addAll(codeCol, nameCol, categoryCol, buyPriceCol, sellPriceCol, stockCol, supplierCol, statusCol);
        
        // Mock data
        ObservableList<Product> data = FXCollections.observableArrayList(
            new Product("P001", "Lait Candia 1L", "Produits laitiers", "80 DA", "100 DA", 150, "Candia", "✅ En stock"),
            new Product("P002", "Pain Blanc", "Boulangerie", "15 DA", "20 DA", 200, "Boulangerie locale", "✅ En stock"),
            new Product("P003", "Huile Elio 2L", "Épicerie", "350 DA", "420 DA", 45, "Cevital", "✅ En stock"),
            new Product("P004", "Café Legal 250g", "Boissons", "280 DA", "350 DA", 75, "Legal", "✅ En stock"),
            new Product("P005", "Sucre Blanc 1kg", "Épicerie", "100 DA", "130 DA", 120, "Cevital", "✅ En stock"),
            new Product("P006", "Yaourt Danone", "Produits laitiers", "25 DA", "35 DA", 8, "Danone", "⚠️ Faible"),
            new Product("P007", "Pâtes Tria", "Épicerie", "65 DA", "85 DA", 95, "Tria", "✅ En stock"),
            new Product("P008", "Jus Ramy", "Boissons", "45 DA", "60 DA", 130, "Ramy", "✅ En stock"),
            new Product("P009", "Fromage Vache qui rit", "Produits laitiers", "120 DA", "155 DA", 3, "Fromagerie", "🔴 Critique"),
            new Product("P010", "Biscuit Bimo", "Boulangerie", "55 DA", "75 DA", 180, "Bimo", "✅ En stock")
        );
        
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        container.getChildren().add(table);
        
        return container;
    }
    
    private void showAddProductPanel() {
        if (isAddPanelVisible) return;
        
        isAddPanelVisible = true;
        
        // Create blur effect for background
        GaussianBlur blur = new GaussianBlur(10);
        mainView.setEffect(blur);
        
        // Create overlay background
        Pane overlayBg = new Pane();
        overlayBg.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlayBg.setOnMouseClicked(e -> hideAddProductPanel());
        
        // Create add product panel
        VBox addPanel = createAddProductPanel();
        
        overlay.getChildren().addAll(overlayBg, addPanel);
        
        // Add overlay to main view parent
        if (mainView.getParent() instanceof StackPane) {
            StackPane parent = (StackPane) mainView.getParent();
            if (!parent.getChildren().contains(overlay)) {
                parent.getChildren().add(overlay);
            }
        }
    }
    
    private void hideAddProductPanel() {
        mainView.setEffect(null);
        overlay.getChildren().clear();
        isAddPanelVisible = false;
    }
    
    private VBox createAddProductPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(30));
        panel.setMaxWidth(600);
        panel.setMaxHeight(700);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 30, 0, 0, 10);"
        );
        
        // Title
        Label title = new Label("Ajouter un Nouveau Produit");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#222222"));
        
        // Scan section
        VBox scanBox = new VBox(8);
        scanBox.setAlignment(Pos.CENTER_LEFT);
        scanBox.setStyle("-fx-background-color: #F5F7FB; -fx-background-radius: 12; -fx-padding: 15;");
        
        Label scanTitle = new Label("Scanner le produit");
        scanTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        scanTitle.setTextFill(Color.web("#4E54C8"));
        
        Label scanSubtitle = new Label("Cliquez sur le bouton pour simuler un scan de code-barres.");
        scanSubtitle.setFont(Font.font("System", FontWeight.NORMAL, 12));
        scanSubtitle.setTextFill(Color.web("#555555"));
        scanSubtitle.setWrapText(true);
        
        Label scanStatus = new Label("Prêt pour le scan.");
        scanStatus.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        scanStatus.setTextFill(Color.web("#4E54C8"));
        
        TextField codeField = createFormTextField("Ex: P011");
        Button scanBtn = createGradientButton("📷 Scanner le produit", "#4E54C8", "#8F94FB");
        scanBtn.setOnAction(e -> {
            String generatedCode = "SCN-" + (100000 + random.nextInt(900000));
            codeField.setText(generatedCode);
            scanStatus.setText("Code scanné : " + generatedCode + " (simulation)");
        });
        
        scanBox.getChildren().addAll(scanTitle, scanSubtitle, scanBtn, scanStatus);
        
        // Form fields
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(10, 0, 10, 0));
        
        // Code
        Label codeLabel = new Label("Code:");
        form.add(codeLabel, 0, 0);
        form.add(codeField, 1, 0);
        
        // Name
        Label nameLabel = new Label("Nom du produit:");
        TextField nameField = createFormTextField("Ex: Coca Cola 1L");
        form.add(nameLabel, 0, 1);
        form.add(nameField, 1, 1);
        
        // Category
        Label categoryLabel = new Label("Catégorie:");
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Produits laitiers", "Épicerie", "Boissons", "Boulangerie");
        categoryBox.setPromptText("Sélectionner");
        categoryBox.setPrefWidth(300);
        form.add(categoryLabel, 0, 2);
        form.add(categoryBox, 1, 2);
        
        // Buy Price
        Label buyPriceLabel = new Label("Prix d'achat (DA):");
        TextField buyPriceField = createFormTextField("Ex: 100");
        form.add(buyPriceLabel, 0, 3);
        form.add(buyPriceField, 1, 3);
        
        // Sell Price
        Label sellPriceLabel = new Label("Prix de vente (DA):");
        TextField sellPriceField = createFormTextField("Ex: 130");
        form.add(sellPriceLabel, 0, 4);
        form.add(sellPriceField, 1, 4);
        
        // Stock
        Label stockLabel = new Label("Quantité en stock:");
        TextField stockField = createFormTextField("Ex: 50");
        form.add(stockLabel, 0, 5);
        form.add(stockField, 1, 5);
        
        // Supplier
        Label supplierLabel = new Label("Fournisseur:");
        TextField supplierField = createFormTextField("Ex: Fournisseur ABC");
        form.add(supplierLabel, 0, 6);
        form.add(supplierField, 1, 6);
        
        // Image upload section
        VBox imageBox = new VBox(10);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setStyle("-fx-background-color: #F5F7FB; -fx-background-radius: 12; -fx-padding: 15;");
        
        Label imageTitle = new Label("Photo du produit");
        imageTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        imageTitle.setTextFill(Color.web("#4E54C8"));
        
        StackPane imagePreview = new StackPane();
        imagePreview.setPrefSize(180, 180);
        imagePreview.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20; " +
            "-fx-border-color: rgba(78,84,200,0.35); " +
            "-fx-border-radius: 20; " +
            "-fx-border-style: segments(12, 6) line-cap round; " +
            "-fx-border-width: 2;"
        );
        Label imagePlaceholder = new Label("Aucune image\n(sélectionnez un fichier)");
        imagePlaceholder.setTextFill(Color.web("#777777"));
        imagePlaceholder.setWrapText(true);
        imagePlaceholder.setAlignment(Pos.CENTER);
        imagePreview.getChildren().add(imagePlaceholder);
        
        Label imageStatus = new Label("Aucune image sélectionnée.");
        imageStatus.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        imageStatus.setTextFill(Color.web("#555555"));
        imageStatus.setWrapText(true);
        
        Button imageBtn = createGradientButton("🖼️ Ajouter une photo", "#FFB74D", "#FFE082");
        imageBtn.setOnAction(e -> {
            imagePlaceholder.setText("Aperçu de l'image\n(démonstration)");
            imageStatus.setText("Image \"produit_demo.png\" sélectionnée (simulation)");
        });
        
        imageBox.getChildren().addAll(imageTitle, imagePreview, imageBtn, imageStatus);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button saveBtn = createGradientButton("💾 Enregistrer", "#66BB6A", "#A5D6A7");
        saveBtn.setPrefWidth(150);
        saveBtn.setOnAction(e -> {
            // Mock save action
            hideAddProductPanel();
        });
        
        Button cancelBtn = createGradientButton("❌ Annuler", "#E57373", "#EF9A9A");
        cancelBtn.setPrefWidth(150);
        cancelBtn.setOnAction(e -> hideAddProductPanel());
        
        buttonBox.getChildren().addAll(saveBtn, cancelBtn);
        
        panel.getChildren().addAll(title, scanBox, form, imageBox, buttonBox);
        
        return panel;
    }
    
    private TextField createFormTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(300);
        field.setStyle(
            "-fx-background-color: #F5F7FB; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 10 15; " +
            "-fx-font-size: 13;"
        );
        return field;
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
        
        button.setOnMouseEntered(e -> {
            button.setStyle(
                button.getStyle() + 
                "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + colorFrom + ", " + colorTo + "); " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
            );
        });
        
        return button;
    }
    
    public VBox getView() {
        return mainView;
    }
    
    // Inner class for Product data
    public static class Product {
        private String code;
        private String name;
        private String category;
        private String buyPrice;
        private String sellPrice;
        private int stock;
        private String supplier;
        private String status;
        
        public Product(String code, String name, String category, String buyPrice, 
                      String sellPrice, int stock, String supplier, String status) {
            this.code = code;
            this.name = name;
            this.category = category;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.stock = stock;
            this.supplier = supplier;
            this.status = status;
        }
        
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getBuyPrice() { return buyPrice; }
        public String getSellPrice() { return sellPrice; }
        public int getStock() { return stock; }
        public String getSupplier() { return supplier; }
        public String getStatus() { return status; }
    }
}
