package com.supermarket;

import org.example.model.CreditItem;
import org.example.model.CreditSale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

/**
 * Dialog for creating a credit sale
 */
public class CreditSaleDialog {
    
    private Stage dialog;
    private TextField nomField;
    private TextField prenomField;
    private TextField idField;
    private Label totalAmountLabel;
    private TableView<CreditItem> itemsTable;
    private double totalAmount;
    private List<SalesView.SaleItem> cartItems;
    private Consumer<CreditSale> onCreditSaleCreated;
    
    public CreditSaleDialog(List<SalesView.SaleItem> cartItems, double totalAmount, Consumer<CreditSale> onCreditSaleCreated) {
        this.cartItems = cartItems;
        this.totalAmount = totalAmount;
        this.onCreditSaleCreated = onCreditSaleCreated;
        createDialog();
    }
    
    private void createDialog() {
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Vente à Crédit");
        dialog.setWidth(750);
        dialog.setHeight(750);
        
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #F5F7FB;");
        
        // Title
        Label title = new Label("📝 Nouvelle Vente à Crédit");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#222222"));
        
        // Client information section
        VBox clientSection = createClientSection();
        
        // Purchase details section
        VBox purchaseSection = createPurchaseDetailsSection();
        
        // Total section
        HBox totalSection = createTotalSection();
        
        // Action buttons
        HBox actionButtons = createActionButtons();
        
        mainContainer.getChildren().addAll(title, clientSection, purchaseSection, totalSection, actionButtons);
        
        // Wrap in ScrollPane
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: #F5F7FB; -fx-background: #F5F7FB;");
        
        Scene scene = new Scene(scrollPane);
        dialog.setScene(scene);
    }
    
    private VBox createClientSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label sectionTitle = new Label("Informations Client");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(Color.web("#222222"));
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        
        // Nom
        Label nomLabel = new Label("Nom *");
        nomLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        nomField = new TextField();
        nomField.setPromptText("Entrez le nom");
        nomField.setPrefHeight(40);
        nomField.setStyle(
            "-fx-background-color: #F5F7FB; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 15;"
        );
        
        // Prénom
        Label prenomLabel = new Label("Prénom *");
        prenomLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        prenomField = new TextField();
        prenomField.setPromptText("Entrez le prénom");
        prenomField.setPrefHeight(40);
        prenomField.setStyle(
            "-fx-background-color: #F5F7FB; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 15;"
        );
        
        // ID (auto-generated)
        Label idLabel = new Label("ID Client *");
        idLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        idField = new TextField();
        idField.setText(generateClientId());
        idField.setEditable(false);
        idField.setPrefHeight(40);
        idField.setStyle(
            "-fx-background-color: #E8EAF6; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 10 15; " +
            "-fx-opacity: 0.9;"
        );
        
        grid.add(nomLabel, 0, 0);
        grid.add(nomField, 0, 1);
        grid.add(prenomLabel, 1, 0);
        grid.add(prenomField, 1, 1);
        grid.add(idLabel, 0, 2);
        grid.add(idField, 0, 3);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);
        
        section.getChildren().addAll(sectionTitle, grid);
        
        return section;
    }
    
    private VBox createPurchaseDetailsSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label sectionTitle = new Label("Détails de l'Achat");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(Color.web("#222222"));
        
        itemsTable = new TableView<>();
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemsTable.setPrefHeight(220);
        itemsTable.setMinHeight(220);
        
        TableColumn<CreditItem, String> productCol = new TableColumn<>("Produit");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productCol.setPrefWidth(250);
        
        TableColumn<CreditItem, Integer> quantityCol = new TableColumn<>("Quantité");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(100);
        
        TableColumn<CreditItem, String> priceCol = new TableColumn<>("Prix Unit.");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPriceString"));
        priceCol.setPrefWidth(120);
        
        TableColumn<CreditItem, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPriceString"));
        totalCol.setPrefWidth(120);
        
        itemsTable.getColumns().addAll(productCol, quantityCol, priceCol, totalCol);
        
        // Convert cart items to credit items
        ObservableList<CreditItem> creditItems = FXCollections.observableArrayList();
        for (SalesView.SaleItem item : cartItems) {
            String priceStr = item.getPrice().replace(" DA", "").trim();
            double price = Double.parseDouble(priceStr);
            creditItems.add(new CreditItem(item.getProduct(), item.getQuantity(), price));
        }
        itemsTable.setItems(creditItems);
        
        section.getChildren().addAll(sectionTitle, itemsTable);
        VBox.setVgrow(itemsTable, Priority.ALWAYS);
        
        return section;
    }
    
    private HBox createTotalSection() {
        HBox section = new HBox(15);
        section.setPadding(new Insets(20));
        section.setAlignment(Pos.CENTER);
        section.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #FF6B6B, #FFB74D); " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        
        Label totalTitleLabel = new Label("Montant Total à Crédit:");
        totalTitleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        totalTitleLabel.setTextFill(Color.WHITE);
        
        totalAmountLabel = new Label(String.format("%.2f DA", totalAmount));
        totalAmountLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        totalAmountLabel.setTextFill(Color.WHITE);
        
        section.getChildren().addAll(totalTitleLabel, totalAmountLabel);
        
        return section;
    }
    
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button confirmBtn = createGradientButton("✅ Confirmer la Vente à Crédit", "#66BB6A", "#A5D6A7");
        confirmBtn.setPrefWidth(250);
        confirmBtn.setPrefHeight(45);
        confirmBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        confirmBtn.setOnAction(e -> confirmCreditSale());
        
        Button cancelBtn = createGradientButton("❌ Annuler", "#E57373", "#EF9A9A");
        cancelBtn.setPrefWidth(130);
        cancelBtn.setPrefHeight(45);
        cancelBtn.setOnAction(e -> dialog.close());
        
        buttonBox.getChildren().addAll(confirmBtn, cancelBtn);
        
        return buttonBox;
    }
    
    private void confirmCreditSale() {
        // Validate fields
        if (nomField.getText().trim().isEmpty() || 
            prenomField.getText().trim().isEmpty() || 
            idField.getText().trim().isEmpty()) {
            
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation");
            alert.setHeaderText("Champs obligatoires");
            alert.setContentText("Veuillez remplir tous les champs (Nom, Prénom, ID).");
            alert.showAndWait();
            return;
        }
        
        // Create credit sale
        CreditSale creditSale = new CreditSale(
            idField.getText().trim(),
            nomField.getText().trim(),
            prenomField.getText().trim(),
            totalAmount,
            LocalDate.now()
        );
        
        // Add items
        for (CreditItem item : itemsTable.getItems()) {
            creditSale.addItem(item);
        }
        
        // Callback
        if (onCreditSaleCreated != null) {
            onCreditSaleCreated.accept(creditSale);
        }
        
        dialog.close();
    }
    
    private Button createGradientButton(String text, String colorFrom, String colorTo) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        button.setTextFill(Color.WHITE);
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + colorFrom + ", " + colorTo + "); " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 10 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2); " +
            "-fx-cursor: hand;"
        );
        
        return button;
    }
    
    private String generateClientId() {
        // Generate unique ID based on timestamp and random number
        long timestamp = System.currentTimeMillis() % 100000;
        int random = (int)(Math.random() * 1000);
        return String.format("CLI-%05d%03d", timestamp, random);
    }
    
    public void show() {
        dialog.showAndWait();
    }
}
