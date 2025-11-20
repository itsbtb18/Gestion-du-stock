package com.supermarket;

import org.example.model.CreditSale;
import org.example.model.Payment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
 * Credit View - Manage credit sales and payments
 */
public class CreditView {
    
    private VBox mainView;
    private TableView<CreditSaleRow> creditsTable;
    private FilteredList<CreditSaleRow> filteredData;
    private ComboBox<String> statusFilter;
    
    public CreditView() {
        createCreditView();
    }
    
    private void createCreditView() {
        mainView = new VBox(20);
        mainView.setPadding(new Insets(0));
        
        // Title and toolbar
        HBox toolbar = createToolbar();
        
        // Main content
        VBox tableContainer = createCreditsTable();
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        
        mainView.getChildren().addAll(toolbar, tableContainer);
    }
    
    private HBox createToolbar() {
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Gestion des Crédits");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#222222"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Rechercher par nom, prénom ou ID...");
        searchField.setPrefWidth(350);
        searchField.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 10 20; " +
            "-fx-font-size: 13; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterCredits(newVal, statusFilter.getValue());
        });
        
        // Status filter
        statusFilter = new ComboBox<>();
        statusFilter.setItems(FXCollections.observableArrayList(
            "Tous", "Non payé", "Partiellement payé", "Payé"
        ));
        statusFilter.setValue("Tous");
        statusFilter.setPrefWidth(180);
        statusFilter.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 5; " +
            "-fx-font-size: 13;"
        );
        
        statusFilter.setOnAction(e -> {
            filterCredits(searchField.getText(), statusFilter.getValue());
        });
        
        Button refreshBtn = createGradientButton("🔄 Actualiser", "#4E54C8", "#8F94FB");
        refreshBtn.setOnAction(e -> refreshTable());
        
        toolbar.getChildren().addAll(title, spacer, searchField, statusFilter, refreshBtn);
        
        return toolbar;
    }
    
    private VBox createCreditsTable() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(25));
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        // Summary cards
        HBox summaryCards = createSummaryCards();
        
        Label tableTitle = new Label("Liste des Crédits");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        tableTitle.setTextFill(Color.web("#222222"));
        
        creditsTable = new TableView<>();
        creditsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<CreditSaleRow, String> idCol = new TableColumn<>("ID Client");
        idCol.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        idCol.setPrefWidth(100);
        
        TableColumn<CreditSaleRow, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        nomCol.setPrefWidth(130);
        
        TableColumn<CreditSaleRow, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        prenomCol.setPrefWidth(130);
        
        TableColumn<CreditSaleRow, String> dateCol = new TableColumn<>("Date de Vente");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateString"));
        dateCol.setPrefWidth(120);
        
        TableColumn<CreditSaleRow, String> totalCol = new TableColumn<>("Montant Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmountString"));
        totalCol.setPrefWidth(120);
        
        TableColumn<CreditSaleRow, String> remainingCol = new TableColumn<>("Montant Restant");
        remainingCol.setCellValueFactory(new PropertyValueFactory<>("remainingAmountString"));
        remainingCol.setPrefWidth(130);
        
        TableColumn<CreditSaleRow, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(150);
        statusCol.setCellFactory(column -> new TableCell<CreditSaleRow, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(status);
                    statusLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));
                    statusLabel.setPadding(new Insets(5, 12, 5, 12));
                    statusLabel.setStyle(
                        "-fx-background-radius: 12; " +
                        getStatusColor(status)
                    );
                    setGraphic(statusLabel);
                }
            }
        });
        
        TableColumn<CreditSaleRow, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(200);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button detailsBtn = new Button("👁️ Détails");
            private final Button payBtn = new Button("💰 Payer");
            private final HBox buttonBox = new HBox(8);
            
            {
                detailsBtn.setStyle(
                    "-fx-background-color: #4E54C8; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 6; " +
                    "-fx-font-size: 11; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 6 12;"
                );
                
                payBtn.setStyle(
                    "-fx-background-color: #66BB6A; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 6; " +
                    "-fx-font-size: 11; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 6 12;"
                );
                
                detailsBtn.setOnAction(e -> {
                    CreditSaleRow row = getTableView().getItems().get(getIndex());
                    showCreditDetails(row.getCreditSale());
                });
                
                payBtn.setOnAction(e -> {
                    CreditSaleRow row = getTableView().getItems().get(getIndex());
                    handlePayment(row.getCreditSale());
                });
                
                buttonBox.getChildren().addAll(detailsBtn, payBtn);
                buttonBox.setAlignment(Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CreditSaleRow row = getTableView().getItems().get(getIndex());
                    if ("Payé".equals(row.getStatus())) {
                        payBtn.setDisable(true);
                        payBtn.setStyle(
                            "-fx-background-color: #CCCCCC; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 6; " +
                            "-fx-font-size: 11; " +
                            "-fx-padding: 6 12;"
                        );
                    }
                    setGraphic(buttonBox);
                }
            }
        });
        
        creditsTable.getColumns().addAll(idCol, nomCol, prenomCol, dateCol, totalCol, remainingCol, statusCol, actionCol);
        
        // Load data
        loadCreditsData();
        
        VBox.setVgrow(creditsTable, Priority.ALWAYS);
        
        container.getChildren().addAll(summaryCards, tableTitle, creditsTable);
        
        return container;
    }
    
    private HBox createSummaryCards() {
        HBox cardsContainer = new HBox(20);
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setPadding(new Insets(0, 0, 20, 0));
        
        VBox totalCard = createSummaryCard("💳", "Total Crédits", getTotalCreditsCount() + "", "#4E54C8", "#8F94FB");
        VBox unpaidCard = createSummaryCard("⚠️", "Non Payés", getUnpaidCreditsCount() + "", "#FF6B6B", "#FFB74D");
        VBox amountCard = createSummaryCard("💰", "Montant Restant", String.format("%.0f DA", getTotalRemainingAmount()), "#66BB6A", "#A5D6A7");
        
        HBox.setHgrow(totalCard, Priority.ALWAYS);
        HBox.setHgrow(unpaidCard, Priority.ALWAYS);
        HBox.setHgrow(amountCard, Priority.ALWAYS);
        
        cardsContainer.getChildren().addAll(totalCard, unpaidCard, amountCard);
        
        return cardsContainer;
    }
    
    private VBox createSummaryCard(String icon, String title, String value, String colorFrom, String colorTo) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + colorFrom + ", " + colorTo + "); " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 32;");
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.WHITE);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        titleLabel.setTextFill(Color.web("#FFFFFF", 0.9));
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        
        return card;
    }
    
    private void loadCreditsData() {
        ObservableList<CreditSaleRow> data = FXCollections.observableArrayList();
        
        for (CreditSale creditSale : SalesView.getCreditSales()) {
            data.add(new CreditSaleRow(creditSale));
        }
        
        filteredData = new FilteredList<>(data, p -> true);
        creditsTable.setItems(filteredData);
    }
    
    private void refreshTable() {
        loadCreditsData();
    }
    
    private void filterCredits(String searchText, String status) {
        filteredData.setPredicate(row -> {
            boolean matchesSearch = true;
            boolean matchesStatus = true;
            
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                matchesSearch = row.getNom().toLowerCase().contains(lowerCaseFilter) ||
                               row.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                               row.getClientId().toLowerCase().contains(lowerCaseFilter);
            }
            
            if (status != null && !"Tous".equals(status)) {
                matchesStatus = row.getStatus().equals(status);
            }
            
            return matchesSearch && matchesStatus;
        });
    }
    
    private void showCreditDetails(CreditSale creditSale) {
        CreditDetailsDialog dialog = new CreditDetailsDialog(creditSale, () -> refreshTable());
        dialog.show();
    }
    
    private void handlePayment(CreditSale creditSale) {
        if ("Payé".equals(creditSale.getStatus())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Crédit Payé");
            alert.setHeaderText(null);
            alert.setContentText("Ce crédit a déjà été payé intégralement.");
            alert.showAndWait();
            return;
        }
        
        // Create payment dialog
        Dialog<Payment> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un Paiement");
        dialog.setHeaderText(String.format("Client: %s\nMontant restant: %.2f DA", 
            creditSale.getFullName(), creditSale.getRemainingAmount()));
        
        ButtonType confirmButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField amountField = new TextField();
        amountField.setPromptText("Montant du paiement");
        amountField.setText(String.valueOf(creditSale.getRemainingAmount()));
        
        ComboBox<String> methodCombo = new ComboBox<>();
        methodCombo.setItems(FXCollections.observableArrayList("Espèces", "Carte", "Virement"));
        methodCombo.setValue("Espèces");
        
        CheckBox fullPaymentCheck = new CheckBox("Marquer comme payé intégralement");
        fullPaymentCheck.setSelected(true);
        
        fullPaymentCheck.setOnAction(e -> {
            if (fullPaymentCheck.isSelected()) {
                amountField.setText(String.valueOf(creditSale.getRemainingAmount()));
            }
        });
        
        grid.add(new Label("Montant:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Méthode:"), 0, 1);
        grid.add(methodCombo, 1, 1);
        grid.add(fullPaymentCheck, 0, 2, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    if (amount <= 0 || amount > creditSale.getRemainingAmount()) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setContentText("Montant invalide!");
                        errorAlert.showAndWait();
                        return null;
                    }
                    return new Payment(amount, LocalDate.now(), methodCombo.getValue());
                } catch (NumberFormatException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setContentText("Montant invalide!");
                    errorAlert.showAndWait();
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(payment -> {
            creditSale.addPayment(payment);
            
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Paiement Enregistré");
            successAlert.setHeaderText("✅ Paiement ajouté avec succès!");
            successAlert.setContentText(String.format(
                "Montant payé: %.2f DA\nMontant restant: %.2f DA\nStatut: %s",
                payment.getAmount(),
                creditSale.getRemainingAmount(),
                creditSale.getStatus()
            ));
            successAlert.showAndWait();
            
            refreshTable();
        });
    }
    
    private String getStatusColor(String status) {
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
    
    private int getTotalCreditsCount() {
        return SalesView.getCreditSales().size();
    }
    
    private int getUnpaidCreditsCount() {
        return (int) SalesView.getCreditSales().stream()
            .filter(c -> !"Payé".equals(c.getStatus()))
            .count();
    }
    
    private double getTotalRemainingAmount() {
        return SalesView.getCreditSales().stream()
            .mapToDouble(CreditSale::getRemainingAmount)
            .sum();
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
    
    // Inner class for table row
    public static class CreditSaleRow {
        private CreditSale creditSale;
        
        public CreditSaleRow(CreditSale creditSale) {
            this.creditSale = creditSale;
        }
        
        public String getClientId() { return creditSale.getClientId(); }
        public String getNom() { return creditSale.getNom(); }
        public String getPrenom() { return creditSale.getPrenom(); }
        public String getDateString() { return creditSale.getDateString(); }
        public String getTotalAmountString() { return String.format("%.2f DA", creditSale.getTotalAmount()); }
        public String getRemainingAmountString() { return String.format("%.2f DA", creditSale.getRemainingAmount()); }
        public String getStatus() { return creditSale.getStatus(); }
        public CreditSale getCreditSale() { return creditSale; }
    }
}
