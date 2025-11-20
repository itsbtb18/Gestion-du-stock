package com.supermarket;

import com.supermarket.models.CreditItem;
import com.supermarket.models.CreditSale;
import com.supermarket.models.Payment;
import javafx.collections.FXCollections;
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

/**
 * Dialog showing detailed information about a credit sale
 */
public class CreditDetailsDialog {
    
    private Stage dialog;
    private CreditSale creditSale;
    private Runnable onClose;
    
    public CreditDetailsDialog(CreditSale creditSale, Runnable onClose) {
        this.creditSale = creditSale;
        this.onClose = onClose;
        createDialog();
    }
    
    private void createDialog() {
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Détails du Crédit");
        dialog.setWidth(800);
        dialog.setHeight(700);
        
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(25));
        mainContainer.setStyle("-fx-background-color: #F5F7FB;");
        
        // Title with status
        HBox titleBox = createTitleBox();
        
        // Client info card
        VBox clientCard = createClientInfoCard();
        
        // Items table
        VBox itemsSection = createItemsSection();
        VBox.setVgrow(itemsSection, Priority.ALWAYS);
        
        // Payment history
        VBox paymentsSection = createPaymentsSection();
        
        // Summary and actions
        HBox bottomSection = createBottomSection();
        
        mainContainer.getChildren().addAll(titleBox, clientCard, itemsSection, paymentsSection, bottomSection);
        
        Scene scene = new Scene(mainContainer);
        dialog.setScene(scene);
    }
    
    private HBox createTitleBox() {
        HBox titleBox = new HBox(15);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📋 Détails du Crédit");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#222222"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(creditSale.getStatus());
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        statusLabel.setPadding(new Insets(8, 20, 8, 20));
        statusLabel.setStyle(
            "-fx-background-radius: 15; " +
            getStatusStyle(creditSale.getStatus())
        );
        
        titleBox.getChildren().addAll(title, spacer, statusLabel);
        
        return titleBox;
    }
    
    private VBox createClientInfoCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #4E54C8, #8F94FB); " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 3);"
        );
        
        Label cardTitle = new Label("👤 Informations Client");
        cardTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        cardTitle.setTextFill(Color.WHITE);
        
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(10);
        
        addInfoRow(grid, 0, "Nom Complet:", creditSale.getFullName());
        addInfoRow(grid, 1, "ID Client:", creditSale.getClientId());
        addInfoRow(grid, 2, "Date de Vente:", creditSale.getDateString());
        
        card.getChildren().addAll(cardTitle, grid);
        
        return card;
    }
    
    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        labelNode.setTextFill(Color.web("#FFFFFF", 0.9));
        
        Label valueNode = new Label(value);
        valueNode.setFont(Font.font("System", FontWeight.BOLD, 14));
        valueNode.setTextFill(Color.WHITE);
        
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }
    
    private VBox createItemsSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label sectionTitle = new Label("🛒 Articles Achetés");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(Color.web("#222222"));
        
        TableView<CreditItem> itemsTable = new TableView<>();
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemsTable.setPrefHeight(200);
        
        TableColumn<CreditItem, String> productCol = new TableColumn<>("Produit");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productCol.setPrefWidth(300);
        
        TableColumn<CreditItem, Integer> quantityCol = new TableColumn<>("Quantité");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(100);
        
        TableColumn<CreditItem, String> priceCol = new TableColumn<>("Prix Unitaire");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPriceString"));
        priceCol.setPrefWidth(150);
        
        TableColumn<CreditItem, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPriceString"));
        totalCol.setPrefWidth(150);
        
        itemsTable.getColumns().addAll(productCol, quantityCol, priceCol, totalCol);
        itemsTable.setItems(FXCollections.observableArrayList(creditSale.getItems()));
        
        section.getChildren().addAll(sectionTitle, itemsTable);
        VBox.setVgrow(section, Priority.ALWAYS);
        
        return section;
    }
    
    private VBox createPaymentsSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label sectionTitle = new Label("💰 Historique des Paiements");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        sectionTitle.setTextFill(Color.web("#222222"));
        
        if (creditSale.getPayments().isEmpty()) {
            Label noPayments = new Label("Aucun paiement enregistré");
            noPayments.setFont(Font.font("System", FontWeight.NORMAL, 13));
            noPayments.setTextFill(Color.web("#999999"));
            noPayments.setPadding(new Insets(20));
            section.getChildren().addAll(sectionTitle, noPayments);
        } else {
            TableView<Payment> paymentsTable = new TableView<>();
            paymentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            paymentsTable.setPrefHeight(150);
            
            TableColumn<Payment, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("dateString"));
            dateCol.setPrefWidth(150);
            
            TableColumn<Payment, String> amountCol = new TableColumn<>("Montant");
            amountCol.setCellValueFactory(new PropertyValueFactory<>("amountString"));
            amountCol.setPrefWidth(150);
            
            TableColumn<Payment, String> methodCol = new TableColumn<>("Méthode");
            methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
            methodCol.setPrefWidth(150);
            
            paymentsTable.getColumns().addAll(dateCol, amountCol, methodCol);
            paymentsTable.setItems(FXCollections.observableArrayList(creditSale.getPayments()));
            
            section.getChildren().addAll(sectionTitle, paymentsTable);
        }
        
        return section;
    }
    
    private HBox createBottomSection() {
        HBox bottomBox = new HBox(20);
        bottomBox.setAlignment(Pos.CENTER);
        
        // Summary card
        VBox summaryCard = new VBox(10);
        summaryCard.setPadding(new Insets(20));
        summaryCard.setAlignment(Pos.CENTER);
        summaryCard.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #FF6B6B, #FFB74D); " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        HBox.setHgrow(summaryCard, Priority.ALWAYS);
        
        Label totalLabel = new Label("Montant Total:");
        totalLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        totalLabel.setTextFill(Color.web("#FFFFFF", 0.9));
        
        Label totalValue = new Label(String.format("%.2f DA", creditSale.getTotalAmount()));
        totalValue.setFont(Font.font("System", FontWeight.BOLD, 24));
        totalValue.setTextFill(Color.WHITE);
        
        Label remainingLabel = new Label("Montant Restant:");
        remainingLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        remainingLabel.setTextFill(Color.web("#FFFFFF", 0.9));
        
        Label remainingValue = new Label(String.format("%.2f DA", creditSale.getRemainingAmount()));
        remainingValue.setFont(Font.font("System", FontWeight.BOLD, 28));
        remainingValue.setTextFill(Color.WHITE);
        
        summaryCard.getChildren().addAll(totalLabel, totalValue, 
            new Separator(), remainingLabel, remainingValue);
        
        // Close button
        Button closeBtn = new Button("✖️ Fermer");
        closeBtn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        closeBtn.setPrefWidth(150);
        closeBtn.setPrefHeight(50);
        closeBtn.setTextFill(Color.WHITE);
        closeBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #4E54C8, #8F94FB); " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2); " +
            "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> {
            if (onClose != null) {
                onClose.run();
            }
            dialog.close();
        });
        
        bottomBox.getChildren().addAll(summaryCard, closeBtn);
        
        return bottomBox;
    }
    
    private String getStatusStyle(String status) {
        switch (status) {
            case "Non payé":
                return "-fx-background-color: #FF6B6B; -fx-text-fill: white;";
            case "Partiellement payé":
                return "-fx-background-color: #FFB74D; -fx-text-fill: white;";
            case "Payé":
                return "-fx-background-color: #66BB6A; -fx-text-fill: white;";
            default:
                return "-fx-background-color: #CCCCCC; -fx-text-fill: white;";
        }
    }
    
    public void show() {
        dialog.showAndWait();
    }
}
