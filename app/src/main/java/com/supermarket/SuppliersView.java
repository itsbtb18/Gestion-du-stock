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

/**
 * Suppliers View - Fournisseurs management
 */
public class SuppliersView {
    
    private VBox mainView;
    private TableView<Supplier> suppliersTable;
    private VBox detailPanel;
    
    public SuppliersView() {
        createSuppliersView();
    }
    
    private void createSuppliersView() {
        mainView = new VBox(20);
        mainView.setPadding(new Insets(0));
        
        // Title and toolbar
        HBox toolbar = createToolbar();
        
        // Main content
        HBox contentArea = new HBox(20);
        
        // Left: Suppliers table
        VBox tableContainer = createSuppliersTable();
        HBox.setHgrow(tableContainer, Priority.ALWAYS);
        
        // Right: Supplier details panel
        detailPanel = createDetailPanel(null);
        detailPanel.setPrefWidth(350);
        
        contentArea.getChildren().addAll(tableContainer, detailPanel);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        
        mainView.getChildren().addAll(toolbar, contentArea);
    }
    
    private HBox createToolbar() {
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Gestion des Fournisseurs");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#222222"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Rechercher fournisseur...");
        searchField.setPrefWidth(300);
        searchField.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 10 20; " +
            "-fx-font-size: 13; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Button addBtn = createGradientButton("➕ Ajouter Fournisseur", "#66BB6A", "#A5D6A7");
        Button importBtn = createGradientButton("📥 Importer", "#4E54C8", "#8F94FB");
        
        toolbar.getChildren().addAll(title, spacer, searchField, addBtn, importBtn);
        
        return toolbar;
    }
    
    private VBox createSuppliersTable() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(25));
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label tableTitle = new Label("Liste des Fournisseurs");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        tableTitle.setTextFill(Color.web("#222222"));
        
        suppliersTable = new TableView<>();
        suppliersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Supplier, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        
        TableColumn<Supplier, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Supplier, String> phoneCol = new TableColumn<>("Téléphone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(130);
        
        TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<Supplier, String> categoryCol = new TableColumn<>("Catégorie");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(150);
        
        TableColumn<Supplier, String> deliveryCol = new TableColumn<>("Délai Livraison");
        deliveryCol.setCellValueFactory(new PropertyValueFactory<>("deliveryTime"));
        deliveryCol.setPrefWidth(120);
        
        suppliersTable.getColumns().addAll(idCol, nameCol, phoneCol, emailCol, categoryCol, deliveryCol);
        
        // Mock data
        ObservableList<Supplier> data = FXCollections.observableArrayList(
            new Supplier("F001", "Cevital", "0555 100 200", "contact@cevital.dz", "Épicerie", "2-3 jours"),
            new Supplier("F002", "Candia", "0666 200 300", "info@candia.dz", "Produits laitiers", "1-2 jours"),
            new Supplier("F003", "Bimo", "0777 300 400", "ventes@bimo.dz", "Boulangerie", "1 jour"),
            new Supplier("F004", "Ramy", "0555 400 500", "contact@ramy.dz", "Boissons", "2 jours"),
            new Supplier("F005", "Tria", "0666 500 600", "info@tria.dz", "Épicerie", "3 jours"),
            new Supplier("F006", "Danone", "0777 600 700", "contact@danone.dz", "Produits laitiers", "1-2 jours"),
            new Supplier("F007", "Legal", "0555 700 800", "ventes@legal.dz", "Boissons", "2-3 jours")
        );
        
        suppliersTable.setItems(data);
        VBox.setVgrow(suppliersTable, Priority.ALWAYS);
        
        // Selection listener
        suppliersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateDetailPanel(newVal);
            }
        });
        
        container.getChildren().addAll(tableTitle, suppliersTable);
        
        return container;
    }
    
    private VBox createDetailPanel(Supplier supplier) {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25));
        panel.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        if (supplier == null) {
            Label placeholder = new Label("Sélectionnez un fournisseur\npour voir les détails");
            placeholder.setFont(Font.font("System", FontWeight.NORMAL, 14));
            placeholder.setTextFill(Color.web("#999999"));
            placeholder.setAlignment(Pos.CENTER);
            placeholder.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            
            VBox.setVgrow(placeholder, Priority.ALWAYS);
            panel.getChildren().add(placeholder);
            panel.setAlignment(Pos.CENTER);
        }
        
        return panel;
    }
    
    private void updateDetailPanel(Supplier supplier) {
        detailPanel.getChildren().clear();
        detailPanel.setAlignment(Pos.TOP_CENTER);
        
        // Icon section
        VBox iconSection = new VBox(10);
        iconSection.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label("🏢");
        iconLabel.setStyle("-fx-font-size: 60;");
        
        Label supplierName = new Label(supplier.getName());
        supplierName.setFont(Font.font("System", FontWeight.BOLD, 20));
        supplierName.setTextFill(Color.web("#222222"));
        
        Label categoryLabel = new Label(supplier.getCategory());
        categoryLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        categoryLabel.setStyle(
            "-fx-background-color: #4E54C8; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 5 15; " +
            "-fx-text-fill: white;"
        );
        
        iconSection.getChildren().addAll(iconLabel, supplierName, categoryLabel);
        
        // Separator
        Separator separator1 = new Separator();
        separator1.setPadding(new Insets(10, 0, 10, 0));
        
        // Contact info
        VBox contactInfo = new VBox(15);
        contactInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label contactTitle = new Label("Informations de Contact");
        contactTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        contactTitle.setTextFill(Color.web("#222222"));
        
        HBox phoneBox = createInfoRow("📱", "Téléphone", supplier.getPhone());
        HBox emailBox = createInfoRow("📧", "Email", supplier.getEmail());
        HBox idBox = createInfoRow("🆔", "ID Fournisseur", supplier.getId());
        HBox deliveryBox = createInfoRow("🚚", "Délai de livraison", supplier.getDeliveryTime());
        
        contactInfo.getChildren().addAll(contactTitle, phoneBox, emailBox, idBox, deliveryBox);
        
        // Separator
        Separator separator2 = new Separator();
        separator2.setPadding(new Insets(10, 0, 10, 0));
        
        // Products supplied
        VBox productsSection = new VBox(15);
        productsSection.setAlignment(Pos.CENTER_LEFT);
        
        Label productsTitle = new Label("Produits Fournis");
        productsTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        productsTitle.setTextFill(Color.web("#222222"));
        
        ListView<String> productsList = new ListView<>();
        productsList.setPrefHeight(120);
        productsList.setItems(FXCollections.observableArrayList(
            "• Huile de table",
            "• Sucre blanc",
            "• Farine",
            "• Pâtes alimentaires",
            "• Conserves diverses"
        ));
        productsList.setStyle("-fx-background-radius: 10;");
        
        productsSection.getChildren().addAll(productsTitle, productsList);
        
        // Note section
        VBox noteSection = new VBox(10);
        noteSection.setAlignment(Pos.CENTER_LEFT);
        
        Label noteTitle = new Label("Note Interne");
        noteTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        noteTitle.setTextFill(Color.web("#222222"));
        
        TextArea noteArea = new TextArea("Fournisseur fiable avec de bons délais de livraison. Prix compétitifs.");
        noteArea.setEditable(false);
        noteArea.setPrefHeight(80);
        noteArea.setWrapText(true);
        noteArea.setStyle(
            "-fx-background-color: #F5F7FB; " +
            "-fx-background-radius: 10; " +
            "-fx-font-size: 12;"
        );
        
        noteSection.getChildren().addAll(noteTitle, noteArea);
        
        // Action buttons
        VBox actionSection = new VBox(10);
        actionSection.setPadding(new Insets(20, 0, 0, 0));
        
        Button editBtn = createGradientButton("✏️ Modifier", "#4E54C8", "#8F94FB");
        editBtn.setPrefWidth(Double.MAX_VALUE);
        
        Button ordersBtn = createGradientButton("📦 Commandes", "#66BB6A", "#A5D6A7");
        ordersBtn.setPrefWidth(Double.MAX_VALUE);
        
        actionSection.getChildren().addAll(editBtn, ordersBtn);
        
        detailPanel.getChildren().addAll(iconSection, separator1, contactInfo, separator2, productsSection, noteSection, actionSection);
    }
    
    private HBox createInfoRow(String icon, String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16;");
        iconLabel.setMinWidth(25);
        
        VBox textBox = new VBox(2);
        Label labelText = new Label(label);
        labelText.setFont(Font.font("System", FontWeight.NORMAL, 11));
        labelText.setTextFill(Color.web("#999999"));
        
        Label valueText = new Label(value);
        valueText.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        valueText.setTextFill(Color.web("#222222"));
        
        textBox.getChildren().addAll(labelText, valueText);
        row.getChildren().addAll(iconLabel, textBox);
        
        return row;
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
    
    // Inner class for Supplier data
    public static class Supplier {
        private String id;
        private String name;
        private String phone;
        private String email;
        private String category;
        private String deliveryTime;
        
        public Supplier(String id, String name, String phone, String email, String category, String deliveryTime) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.category = category;
            this.deliveryTime = deliveryTime;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getCategory() { return category; }
        public String getDeliveryTime() { return deliveryTime; }
    }
}
