package com.supermarket;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Settings View with application preferences
 */
public class SettingsView {
    
    private VBox mainView;
    
    public SettingsView() {
        createSettingsView();
    }
    
    private void createSettingsView() {
        mainView = new VBox(20);
        mainView.setPadding(new Insets(0));
        
        Label title = new Label("Paramètres");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#222222"));
        
        // Settings sections
        VBox generalSettings = createGeneralSettings();
        VBox appearanceSettings = createAppearanceSettings();
        VBox notificationSettings = createNotificationSettings();
        VBox accountSettings = createAccountSettings();
        
        mainView.getChildren().addAll(title, generalSettings, appearanceSettings, 
                                      notificationSettings, accountSettings);
    }
    
    private VBox createGeneralSettings() {
        VBox section = new VBox(20);
        section.setPadding(new Insets(25));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label sectionTitle = new Label("⚙️ Paramètres Généraux");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.web("#222222"));
        
        // Language setting
        HBox languageRow = createSettingRow(
            "Langue de l'application",
            "Choisissez la langue d'affichage",
            createComboBox("Français", "English", "العربية")
        );
        
        // Currency setting
        HBox currencyRow = createSettingRow(
            "Devise",
            "Devise utilisée pour les prix",
            createComboBox("Dinar Algérien (DA)", "Euro (€)", "Dollar ($)")
        );
        
        // Tax setting
        HBox taxRow = createSettingRow(
            "TVA par défaut",
            "Taux de TVA appliqué",
            createTextField("19%")
        );
        
        section.getChildren().addAll(sectionTitle, languageRow, 
                                     new Separator(), currencyRow, 
                                     new Separator(), taxRow);
        
        return section;
    }
    
    private VBox createAppearanceSettings() {
        VBox section = new VBox(20);
        section.setPadding(new Insets(25));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label sectionTitle = new Label("🎨 Apparence");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.web("#222222"));
        
        // Theme setting
        HBox themeRow = createSettingRow(
            "Thème",
            "Mode d'affichage de l'application",
            createToggleSwitch(true)
        );
        Label themeHint = new Label("Clair                Dark (bientôt disponible)");
        themeHint.setFont(Font.font("System", 10));
        themeHint.setTextFill(Color.web("#999999"));
        HBox themeBox = new HBox(10, themeRow, themeHint);
        themeBox.setAlignment(Pos.CENTER_LEFT);
        
        // Font size setting
        HBox fontSizeRow = createSettingRow(
            "Taille de police",
            "Ajustez la taille du texte",
            createComboBox("Petit", "Moyen", "Grand")
        );
        
        // Animation setting
        HBox animationRow = createSettingRow(
            "Animations",
            "Activer les animations de l'interface",
            createToggleSwitch(true)
        );
        
        section.getChildren().addAll(sectionTitle, themeBox, 
                                     new Separator(), fontSizeRow,
                                     new Separator(), animationRow);
        
        return section;
    }
    
    private VBox createNotificationSettings() {
        VBox section = new VBox(20);
        section.setPadding(new Insets(25));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label sectionTitle = new Label("🔔 Notifications");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.web("#222222"));
        
        // Low stock alerts
        HBox lowStockRow = createSettingRow(
            "Alertes de stock faible",
            "Recevoir des notifications pour les produits en rupture",
            createToggleSwitch(true)
        );
        
        // Sales notifications
        HBox salesRow = createSettingRow(
            "Notifications de ventes",
            "Alertes pour les nouvelles ventes",
            createToggleSwitch(false)
        );
        
        // Daily reports
        HBox reportsRow = createSettingRow(
            "Rapports quotidiens",
            "Recevoir un résumé quotidien par email",
            createToggleSwitch(true)
        );
        
        section.getChildren().addAll(sectionTitle, lowStockRow,
                                     new Separator(), salesRow,
                                     new Separator(), reportsRow);
        
        return section;
    }
    
    private VBox createAccountSettings() {
        VBox section = new VBox(20);
        section.setPadding(new Insets(25));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        Label sectionTitle = new Label("👤 Compte Utilisateur");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.web("#222222"));
        
        GridPane accountGrid = new GridPane();
        accountGrid.setHgap(20);
        accountGrid.setVgap(15);
        
        Label nameLabel = new Label("Nom d'utilisateur:");
        nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        TextField nameField = createTextField("Admin");
        
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        TextField emailField = createTextField("admin@supermarket.dz");
        
        Label roleLabel = new Label("Rôle:");
        roleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        Label roleValue = new Label("Administrateur");
        roleValue.setFont(Font.font("System", 13));
        roleValue.setTextFill(Color.web("#555555"));
        
        accountGrid.add(nameLabel, 0, 0);
        accountGrid.add(nameField, 1, 0);
        accountGrid.add(emailLabel, 0, 1);
        accountGrid.add(emailField, 1, 1);
        accountGrid.add(roleLabel, 0, 2);
        accountGrid.add(roleValue, 1, 2);
        
        // Action buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));
        
        Button changePasswordBtn = createGradientButton("🔒 Changer mot de passe", "#4E54C8", "#8F94FB");
        Button saveBtn = createGradientButton("💾 Sauvegarder les modifications", "#66BB6A", "#A5D6A7");
        
        buttonBox.getChildren().addAll(changePasswordBtn, saveBtn);
        
        section.getChildren().addAll(sectionTitle, accountGrid, buttonBox);
        
        return section;
    }
    
    private HBox createSettingRow(String title, String description, Control control) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 0, 10, 0));
        
        VBox textBox = new VBox(5);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        titleLabel.setTextFill(Color.web("#222222"));
        
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("System", 11));
        descLabel.setTextFill(Color.web("#999999"));
        
        textBox.getChildren().addAll(titleLabel, descLabel);
        
        row.getChildren().addAll(textBox, control);
        
        return row;
    }
    
    private ComboBox<String> createComboBox(String... items) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(items);
        comboBox.setValue(items[0]);
        comboBox.setPrefWidth(200);
        comboBox.setStyle("-fx-background-radius: 8;");
        return comboBox;
    }
    
    private TextField createTextField(String text) {
        TextField field = new TextField(text);
        field.setPrefWidth(250);
        field.setStyle(
            "-fx-background-color: #F5F7FB; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 8 15; " +
            "-fx-font-size: 13;"
        );
        return field;
    }
    
    private ToggleButton createToggleSwitch(boolean selected) {
        ToggleButton toggle = new ToggleButton();
        toggle.setSelected(selected);
        toggle.setPrefWidth(60);
        toggle.setPrefHeight(30);
        toggle.setCursor(Cursor.HAND);
        
        updateToggleStyle(toggle);
        
        toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateToggleStyle(toggle);
        });
        
        return toggle;
    }
    
    private void updateToggleStyle(ToggleButton toggle) {
        if (toggle.isSelected()) {
            toggle.setStyle(
                "-fx-background-color: #66BB6A; " +
                "-fx-background-radius: 15; " +
                "-fx-text-fill: transparent;"
            );
            toggle.setText("ON");
        } else {
            toggle.setStyle(
                "-fx-background-color: #CCCCCC; " +
                "-fx-background-radius: 15; " +
                "-fx-text-fill: transparent;"
            );
            toggle.setText("OFF");
        }
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
}
