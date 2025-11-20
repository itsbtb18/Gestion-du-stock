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
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Clients View with customer management
 */
public class ClientsView {
    
    private VBox mainView;
    private TableView<Client> clientsTable;
    private VBox detailPanel;
    
    public ClientsView() {
        createClientsView();
    }
    
    private void createClientsView() {
        mainView = new VBox(20);
        mainView.setPadding(new Insets(0));
        
        // Title and toolbar
        HBox toolbar = createToolbar();
        
        // Main content
        HBox contentArea = new HBox(20);
        
        // Left: Clients table
        VBox tableContainer = createClientsTable();
        HBox.setHgrow(tableContainer, Priority.ALWAYS);
        
        // Right: Client details panel
        detailPanel = createDetailPanel(null);
        detailPanel.setPrefWidth(350);
        
        contentArea.getChildren().addAll(tableContainer, detailPanel);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        
        mainView.getChildren().addAll(toolbar, contentArea);
    }
    
    private HBox createToolbar() {
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Gestion des Clients");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#222222"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Rechercher client...");
        searchField.setPrefWidth(300);
        searchField.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 10 20; " +
            "-fx-font-size: 13; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Button addBtn = createGradientButton("➕ Ajouter Client", "#66BB6A", "#A5D6A7");
        Button importBtn = createGradientButton("📥 Importer", "#4E54C8", "#8F94FB");
        
        toolbar.getChildren().addAll(title, spacer, searchField, addBtn, importBtn);
        
        return toolbar;
    }
    
    private VBox createClientsTable() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(25));
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label tableTitle = new Label("Liste des Clients");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        tableTitle.setTextFill(Color.web("#222222"));
        
        clientsTable = new TableView<>();
        clientsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Client, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        
        TableColumn<Client, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);
        
        TableColumn<Client, String> phoneCol = new TableColumn<>("Téléphone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(130);
        
        TableColumn<Client, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<Client, Integer> pointsCol = new TableColumn<>("Points");
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        pointsCol.setPrefWidth(80);
        
        TableColumn<Client, String> tagCol = new TableColumn<>("Statut");
        tagCol.setCellValueFactory(new PropertyValueFactory<>("tag"));
        tagCol.setPrefWidth(100);
        
        clientsTable.getColumns().addAll(idCol, nameCol, phoneCol, emailCol, pointsCol, tagCol);
        
        // Mock data
        ObservableList<Client> data = FXCollections.observableArrayList(
            new Client("C001", "Ahmed Benali", "0555 123 456", "ahmed.benali@email.dz", 1250, "🌟 VIP"),
            new Client("C002", "Fatima Zahra", "0666 234 567", "fatima.z@email.dz", 450, "👤 Normal"),
            new Client("C003", "Mohamed Khelifi", "0777 345 678", "m.khelifi@email.dz", 2100, "🌟 VIP"),
            new Client("C004", "Amina Saidi", "0555 456 789", "amina.saidi@email.dz", 180, "🆕 Nouveau"),
            new Client("C005", "Karim Bouzid", "0666 567 890", "karim.b@email.dz", 890, "👤 Normal"),
            new Client("C006", "Yasmine Hadj", "0777 678 901", "yasmine.h@email.dz", 3200, "🌟 VIP"),
            new Client("C007", "Omar Sahraoui", "0555 789 012", "omar.s@email.dz", 95, "🆕 Nouveau"),
            new Client("C008", "Nadia Meziane", "0666 890 123", "nadia.m@email.dz", 620, "👤 Normal")
        );
        
        clientsTable.setItems(data);
        VBox.setVgrow(clientsTable, Priority.ALWAYS);
        
        // Selection listener
        clientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateDetailPanel(newVal);
            }
        });
        
        container.getChildren().addAll(tableTitle, clientsTable);
        
        return container;
    }
    
    private VBox createDetailPanel(Client client) {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(25));
        panel.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        if (client == null) {
            Label placeholder = new Label("Sélectionnez un client\npour voir les détails");
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
    
    private void updateDetailPanel(Client client) {
        detailPanel.getChildren().clear();
        detailPanel.setAlignment(Pos.TOP_CENTER);
        
        // Avatar section
        VBox avatarSection = new VBox(10);
        avatarSection.setAlignment(Pos.CENTER);
        
        StackPane avatarContainer = new StackPane();
        Circle avatar = new Circle(50);
        avatar.setFill(Color.web("#4E54C8"));
        avatar.setEffect(new javafx.scene.effect.DropShadow(20, Color.web("#4E54C8", 0.3)));
        
        Label initials = new Label(getInitials(client.getName()));
        initials.setFont(Font.font("System", FontWeight.BOLD, 24));
        initials.setTextFill(Color.WHITE);
        
        avatarContainer.getChildren().addAll(avatar, initials);
        
        Label clientName = new Label(client.getName());
        clientName.setFont(Font.font("System", FontWeight.BOLD, 20));
        clientName.setTextFill(Color.web("#222222"));
        
        Label clientTag = new Label(client.getTag());
        clientTag.setFont(Font.font("System", FontWeight.NORMAL, 12));
        clientTag.setStyle(
            "-fx-background-color: #FFB74D; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 5 15; " +
            "-fx-text-fill: white;"
        );
        
        avatarSection.getChildren().addAll(avatarContainer, clientName, clientTag);
        
        // Separator
        Separator separator1 = new Separator();
        separator1.setPadding(new Insets(10, 0, 10, 0));
        
        // Contact info
        VBox contactInfo = new VBox(15);
        contactInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label contactTitle = new Label("Informations de Contact");
        contactTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        contactTitle.setTextFill(Color.web("#222222"));
        
        HBox phoneBox = createInfoRow("📱", "Téléphone", client.getPhone());
        HBox emailBox = createInfoRow("📧", "Email", client.getEmail());
        HBox idBox = createInfoRow("🆔", "ID Client", client.getId());
        
        contactInfo.getChildren().addAll(contactTitle, phoneBox, emailBox, idBox);
        
        // Separator
        Separator separator2 = new Separator();
        separator2.setPadding(new Insets(10, 0, 10, 0));
        
        // Stats section
        VBox statsSection = new VBox(15);
        statsSection.setAlignment(Pos.CENTER_LEFT);
        
        Label statsTitle = new Label("Statistiques");
        statsTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        statsTitle.setTextFill(Color.web("#222222"));
        
        // Points card
        HBox pointsCard = new HBox(15);
        pointsCard.setPadding(new Insets(15));
        pointsCard.setAlignment(Pos.CENTER_LEFT);
        pointsCard.setStyle(
            "-fx-background-color: linear-gradient(to right, #FFB74D, #FFE082); " +
            "-fx-background-radius: 10;"
        );
        
        Label pointsIcon = new Label("⭐");
        pointsIcon.setStyle("-fx-font-size: 28;");
        
        VBox pointsInfo = new VBox(3);
        Label pointsValue = new Label(client.getPoints() + " Points");
        pointsValue.setFont(Font.font("System", FontWeight.BOLD, 18));
        pointsValue.setTextFill(Color.WHITE);
        
        Label pointsLabel = new Label("Points de Fidélité");
        pointsLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        pointsLabel.setTextFill(Color.web("#FFFFFF", 0.9));
        
        pointsInfo.getChildren().addAll(pointsValue, pointsLabel);
        pointsCard.getChildren().addAll(pointsIcon, pointsInfo);
        
        // Additional stats
        HBox purchasesBox = createInfoRow("🛒", "Achats totaux", "47 achats");
        HBox lastPurchaseBox = createInfoRow("📅", "Dernier achat", "15/11/2025");
        HBox totalSpentBox = createInfoRow("💰", "Total dépensé", "125,000 DA");
        
        statsSection.getChildren().addAll(statsTitle, pointsCard, purchasesBox, lastPurchaseBox, totalSpentBox);
        
        // Action buttons
        VBox actionSection = new VBox(10);
        actionSection.setPadding(new Insets(20, 0, 0, 0));
        
        Button editBtn = createGradientButton("✏️ Modifier", "#4E54C8", "#8F94FB");
        editBtn.setPrefWidth(Double.MAX_VALUE);
        
        Button historyBtn = createGradientButton("📜 Historique", "#66BB6A", "#A5D6A7");
        historyBtn.setPrefWidth(Double.MAX_VALUE);
        
        actionSection.getChildren().addAll(editBtn, historyBtn);
        
        detailPanel.getChildren().addAll(avatarSection, separator1, contactInfo, separator2, statsSection, actionSection);
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
    
    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
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
    
    // Inner class for Client data
    public static class Client {
        private String id;
        private String name;
        private String phone;
        private String email;
        private int points;
        private String tag;
        
        public Client(String id, String name, String phone, String email, int points, String tag) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.points = points;
            this.tag = tag;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public int getPoints() { return points; }
        public String getTag() { return tag; }
    }
}
