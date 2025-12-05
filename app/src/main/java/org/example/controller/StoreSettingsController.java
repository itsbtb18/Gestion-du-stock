package org.example.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.model.entity.*;
import org.example.model.entity.LoyaltyProgramConfig.RewardType;
import org.example.model.entity.Promotion.PromotionType;
import org.example.model.entity.Promotion.PromotionScope;
import org.example.model.service.StoreService;
import org.example.model.service.PromotionService;
import org.example.util.StoreContext;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Store Settings view
 * Handles store configuration, promotions, loyalty program, and advanced settings
 */
public class StoreSettingsController {

    // Tab Pane
    @FXML private TabPane settingsTabPane;
    
    // Store Info Tab
    @FXML private TextField storeCodeField;
    @FXML private TextField storeNameField;
    @FXML private TextArea storeAddressField;
    @FXML private TextField storePhoneField;
    @FXML private TextField storeEmailField;
    @FXML private TextField storeLogoField;
    @FXML private ComboBox<String> currencyCombo;
    @FXML private ComboBox<String> languageCombo;
    @FXML private CheckBox storeActiveCheck;
    
    // Promotions Tab
    @FXML private TableView<Promotion> promotionsTable;
    @FXML private TableColumn<Promotion, String> promoNameColumn;
    @FXML private TableColumn<Promotion, String> promoTypeColumn;
    @FXML private TableColumn<Promotion, Double> promoValueColumn;
    @FXML private TableColumn<Promotion, String> promoScopeColumn;
    @FXML private TableColumn<Promotion, String> promoStartColumn;
    @FXML private TableColumn<Promotion, String> promoEndColumn;
    @FXML private TableColumn<Promotion, Boolean> promoActiveColumn;
    
    // Loyalty Tab
    @FXML private CheckBox loyaltyEnabledCheck;
    @FXML private Spinner<Integer> pointsPerCurrencySpinner;
    @FXML private TextField minPurchaseField;
    @FXML private Spinner<Integer> rewardThresholdSpinner;
    @FXML private ComboBox<RewardType> rewardTypeCombo;
    @FXML private TextField rewardValueField;
    @FXML private CheckBox allowPartialRedemptionCheck;
    @FXML private Spinner<Integer> minRedemptionPointsSpinner;
    @FXML private Spinner<Double> maxRedemptionPercentSpinner;
    @FXML private Spinner<Integer> pointsExpirationSpinner;
    
    // Advanced Settings Tab
    @FXML private CheckBox allowNegativeStockCheck;
    @FXML private CheckBox lowStockNotificationsCheck;
    @FXML private CheckBox expirationAlertsCheck;
    @FXML private Spinner<Integer> expirationAlertDaysSpinner;
    @FXML private CheckBox requireManagerApprovalCheck;
    @FXML private TextField maxDiscountField;
    @FXML private TextField defaultVatRateField;
    @FXML private TextField invoiceHeaderField;
    @FXML private TextArea invoiceFooterField;
    
    // Buttons
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    // Services
    private final StoreService storeService = StoreService.getInstance();
    private final PromotionService promotionService = PromotionService.getInstance();
    
    // State
    private Store currentStore;
    private StoreSettings currentSettings;
    private LoyaltyProgramConfig currentLoyaltyConfig;
    private ObservableList<Promotion> promotionsList = FXCollections.observableArrayList();

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        loadCurrentStore();
        setupComboBoxes();
        setupSpinners();
        setupPromotionsTable();
        loadStoreData();
        loadPromotions();
    }
    
    /**
     * Load current store from context
     */
    private void loadCurrentStore() {
        currentStore = StoreContext.getInstance().getCurrentStore();
        currentSettings = StoreContext.getInstance().getStoreSettings();
        currentLoyaltyConfig = StoreContext.getInstance().getLoyaltyConfig();
        
        if (currentStore == null || currentSettings == null || currentLoyaltyConfig == null) {
            showAlert("Erreur", "Impossible de charger les paramètres du magasin", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Setup combo boxes
     */
    private void setupComboBoxes() {
        // Currency combo
        currencyCombo.setItems(FXCollections.observableArrayList("MAD", "EUR", "USD", "GBP"));
        
        // Language combo
        languageCombo.setItems(FXCollections.observableArrayList("fr", "ar", "en", "es"));
        
        // Reward type combo
        rewardTypeCombo.setItems(FXCollections.observableArrayList(RewardType.values()));
    }
    
    /**
     * Setup spinners with value factories
     */
    private void setupSpinners() {
        pointsPerCurrencySpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        
        rewardThresholdSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 10000, 100));
        
        minRedemptionPointsSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 50));
        
        maxRedemptionPercentSpinner.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1.0, 0.3, 0.1));
        
        pointsExpirationSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 3650, 365));
        
        expirationAlertDaysSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 365, 30));
    }
    
    /**
     * Setup promotions table
     */
    private void setupPromotionsTable() {
        promoNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getName()));
        
        promoTypeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getType().toString()));
        
        promoValueColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getValue()).asObject());
        
        promoScopeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getScope().toString()));
        
        promoStartColumn.setCellValueFactory(cellData -> {
            LocalDate startDate = cellData.getValue().getStartDate();
            String dateStr = startDate != null ? startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
            return new SimpleStringProperty(dateStr);
        });
        
        promoEndColumn.setCellValueFactory(cellData -> {
            LocalDate endDate = cellData.getValue().getEndDate();
            String dateStr = endDate != null ? endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
            return new SimpleStringProperty(dateStr);
        });
        
        promoActiveColumn.setCellValueFactory(cellData -> 
            new SimpleBooleanProperty(cellData.getValue().isActive()));
        promoActiveColumn.setCellFactory(CheckBoxTableCell.forTableColumn(promoActiveColumn));
        
        promotionsTable.setItems(promotionsList);
    }
    
    /**
     * Load store data into UI
     */
    private void loadStoreData() {
        // Store Info
        storeCodeField.setText(currentStore.getCode());
        storeNameField.setText(currentStore.getName());
        storeAddressField.setText(currentStore.getAddress());
        storePhoneField.setText(currentStore.getPhone());
        storeEmailField.setText(currentStore.getEmail());
        storeLogoField.setText(currentStore.getLogoPath());
        currencyCombo.setValue(currentStore.getCurrency());
        languageCombo.setValue(currentStore.getLanguage());
        storeActiveCheck.setSelected(currentStore.isActive());
        
        // Advanced Settings
        allowNegativeStockCheck.setSelected(currentSettings.isAllowNegativeStock());
        requireManagerApprovalCheck.setSelected(currentSettings.isRequireManagerApproval());
        defaultVatRateField.setText(String.valueOf(currentSettings.getDefaultVatRate() * 100));
        maxDiscountField.setText(String.valueOf(currentSettings.getMaxDiscountPercent()));
        invoiceHeaderField.setText(currentSettings.getInvoiceHeaderText());
        invoiceFooterField.setText(currentSettings.getInvoiceFooterText());
        lowStockNotificationsCheck.setSelected(currentSettings.isLowStockNotificationsEnabled());
        expirationAlertsCheck.setSelected(currentSettings.isExpirationAlertsEnabled());
        expirationAlertDaysSpinner.getValueFactory().setValue(currentSettings.getExpirationAlertDays());
        
        // Loyalty Settings
        loyaltyEnabledCheck.setSelected(currentLoyaltyConfig.isEnabled());
        pointsPerCurrencySpinner.getValueFactory().setValue(currentLoyaltyConfig.getPointsPerCurrencyUnit());
        minPurchaseField.setText(String.valueOf(currentLoyaltyConfig.getMinimumPurchaseAmount()));
        rewardThresholdSpinner.getValueFactory().setValue(currentLoyaltyConfig.getRewardThreshold());
        rewardTypeCombo.setValue(currentLoyaltyConfig.getRewardType());
        rewardValueField.setText(String.valueOf(currentLoyaltyConfig.getRewardValue()));
        allowPartialRedemptionCheck.setSelected(currentLoyaltyConfig.isAllowPartialRedemption());
        minRedemptionPointsSpinner.getValueFactory().setValue(currentLoyaltyConfig.getMinimumRedemptionPoints());
        maxRedemptionPercentSpinner.getValueFactory().setValue(currentLoyaltyConfig.getMaxRedemptionPercentOfTotal());
        pointsExpirationSpinner.getValueFactory().setValue(currentLoyaltyConfig.getPointsExpirationDays());
    }
    
    /**
     * Load promotions from database
     */
    private void loadPromotions() {
        List<Promotion> promotions = promotionService.getAllPromotions();
        promotionsList.clear();
        promotionsList.addAll(promotions);
    }
    
    /**
     * Handle save button
     */
    @FXML
    private void handleSave() {
        try {
            // Update Store
            currentStore.setCode(storeCodeField.getText());
            currentStore.setName(storeNameField.getText());
            currentStore.setAddress(storeAddressField.getText());
            currentStore.setPhone(storePhoneField.getText());
            currentStore.setEmail(storeEmailField.getText());
            currentStore.setLogoPath(storeLogoField.getText());
            currentStore.setCurrency(currencyCombo.getValue());
            currentStore.setLanguage(languageCombo.getValue());
            currentStore.setActive(storeActiveCheck.isSelected());
            
            // Update Settings
            currentSettings.setAllowNegativeStock(allowNegativeStockCheck.isSelected());
            currentSettings.setRequireManagerApproval(requireManagerApprovalCheck.isSelected());
            currentSettings.setDefaultVatRate(Double.parseDouble(defaultVatRateField.getText()) / 100.0);
            currentSettings.setMaxDiscountPercent(Double.parseDouble(maxDiscountField.getText()));
            currentSettings.setInvoiceHeaderText(invoiceHeaderField.getText());
            currentSettings.setInvoiceFooterText(invoiceFooterField.getText());
            currentSettings.setLoyaltyProgramEnabled(loyaltyEnabledCheck.isSelected());
            currentSettings.setLowStockNotificationsEnabled(lowStockNotificationsCheck.isSelected());
            currentSettings.setExpirationAlertsEnabled(expirationAlertsCheck.isSelected());
            currentSettings.setExpirationAlertDays(expirationAlertDaysSpinner.getValue());
            
            // Update Loyalty Config
            currentLoyaltyConfig.setEnabled(loyaltyEnabledCheck.isSelected());
            currentLoyaltyConfig.setPointsPerCurrencyUnit(pointsPerCurrencySpinner.getValue());
            currentLoyaltyConfig.setMinimumPurchaseAmount(Double.parseDouble(minPurchaseField.getText()));
            currentLoyaltyConfig.setRewardThreshold(rewardThresholdSpinner.getValue());
            currentLoyaltyConfig.setRewardType(rewardTypeCombo.getValue());
            currentLoyaltyConfig.setRewardValue(Double.parseDouble(rewardValueField.getText()));
            currentLoyaltyConfig.setAllowPartialRedemption(allowPartialRedemptionCheck.isSelected());
            currentLoyaltyConfig.setMinimumRedemptionPoints(minRedemptionPointsSpinner.getValue());
            currentLoyaltyConfig.setMaxRedemptionPercentOfTotal(maxRedemptionPercentSpinner.getValue());
            currentLoyaltyConfig.setPointsExpirationDays(pointsExpirationSpinner.getValue());
            
            // Save to database
            storeService.updateStore(currentStore);
            storeService.updateSettings(currentSettings);
            storeService.updateLoyaltyConfig(currentLoyaltyConfig);
            
            showAlert("Succès", "Paramètres enregistrés avec succès", Alert.AlertType.INFORMATION);
            
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez vérifier les valeurs numériques", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'enregistrement: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Handle cancel button
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Handle browse logo button
     */
    @FXML
    private void handleBrowseLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner le logo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File file = fileChooser.showOpenDialog(storeLogoField.getScene().getWindow());
        if (file != null) {
            storeLogoField.setText(file.getAbsolutePath());
        }
    }
    
    /**
     * Handle add promotion button
     */
    @FXML
    private void handleAddPromotion() {
        // Create dialog for adding promotion
        Dialog<Promotion> dialog = createPromotionDialog(null);
        Optional<Promotion> result = dialog.showAndWait();
        
        result.ifPresent(promotion -> {
            try {
                promotionService.createPromotion(promotion);
                loadPromotions();
                showAlert("Succès", "Promotion créée avec succès", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la création: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
    
    /**
     * Handle edit promotion button
     */
    @FXML
    private void handleEditPromotion() {
        Promotion selected = promotionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une promotion", Alert.AlertType.WARNING);
            return;
        }
        
        Dialog<Promotion> dialog = createPromotionDialog(selected);
        Optional<Promotion> result = dialog.showAndWait();
        
        result.ifPresent(promotion -> {
            try {
                promotionService.updatePromotion(promotion);
                loadPromotions();
                showAlert("Succès", "Promotion modifiée avec succès", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
    
    /**
     * Handle delete promotion button
     */
    @FXML
    private void handleDeletePromotion() {
        Promotion selected = promotionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une promotion", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la promotion");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette promotion ?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            promotionService.deletePromotion(selected.getId());
            loadPromotions();
            showAlert("Succès", "Promotion supprimée", Alert.AlertType.INFORMATION);
        }
    }
    
    /**
     * Handle refresh promotions button
     */
    @FXML
    private void handleRefreshPromotions() {
        loadPromotions();
    }
    
    /**
     * Create promotion dialog
     */
    private Dialog<Promotion> createPromotionDialog(Promotion existingPromo) {
        Dialog<Promotion> dialog = new Dialog<>();
        dialog.setTitle(existingPromo == null ? "Nouvelle Promotion" : "Modifier Promotion");
        dialog.setHeaderText("Entrez les détails de la promotion");
        
        // Create form fields
        TextField nameField = new TextField();
        nameField.setPromptText("Nom de la promotion");
        
        TextArea descField = new TextArea();
        descField.setPromptText("Description");
        descField.setPrefRowCount(2);
        
        ComboBox<PromotionType> typeCombo = new ComboBox<>();
        typeCombo.setItems(FXCollections.observableArrayList(PromotionType.values()));
        
        TextField valueField = new TextField();
        valueField.setPromptText("Valeur");
        
        ComboBox<PromotionScope> scopeCombo = new ComboBox<>();
        scopeCombo.setItems(FXCollections.observableArrayList(PromotionScope.values()));
        
        TextField targetIdsField = new TextField();
        targetIdsField.setPromptText("IDs cibles (séparés par virgules)");
        
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        
        CheckBox activeCheck = new CheckBox("Active");
        
        Spinner<Integer> prioritySpinner = new Spinner<>(0, 100, 0);
        
        // Load existing values if editing
        if (existingPromo != null) {
            nameField.setText(existingPromo.getName());
            descField.setText(existingPromo.getDescription());
            typeCombo.setValue(existingPromo.getType());
            valueField.setText(String.valueOf(existingPromo.getValue()));
            scopeCombo.setValue(existingPromo.getScope());
            targetIdsField.setText(existingPromo.getTargetIds());
            startDatePicker.setValue(existingPromo.getStartDate());
            endDatePicker.setValue(existingPromo.getEndDate());
            activeCheck.setSelected(existingPromo.isActive());
            prioritySpinner.getValueFactory().setValue(existingPromo.getPriority());
        }
        
        // Create form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);
        grid.add(new Label("Valeur:"), 0, 3);
        grid.add(valueField, 1, 3);
        grid.add(new Label("Portée:"), 0, 4);
        grid.add(scopeCombo, 1, 4);
        grid.add(new Label("IDs Cibles:"), 0, 5);
        grid.add(targetIdsField, 1, 5);
        grid.add(new Label("Date début:"), 0, 6);
        grid.add(startDatePicker, 1, 6);
        grid.add(new Label("Date fin:"), 0, 7);
        grid.add(endDatePicker, 1, 7);
        grid.add(new Label("Priorité:"), 0, 8);
        grid.add(prioritySpinner, 1, 8);
        grid.add(activeCheck, 1, 9);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Promotion promo = existingPromo != null ? existingPromo : new Promotion();
                promo.setStoreId(currentStore.getId());
                promo.setName(nameField.getText());
                promo.setDescription(descField.getText());
                promo.setType(typeCombo.getValue());
                promo.setValue(Double.parseDouble(valueField.getText()));
                promo.setScope(scopeCombo.getValue());
                promo.setTargetIds(targetIdsField.getText());
                promo.setStartDate(startDatePicker.getValue());
                promo.setEndDate(endDatePicker.getValue());
                promo.setActive(activeCheck.isSelected());
                promo.setPriority(prioritySpinner.getValue());
                if (existingPromo == null) {
                    promo.setCreatedDate(LocalDateTime.now());
                }
                return promo;
            }
            return null;
        });
        
        return dialog;
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
