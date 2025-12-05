package org.example.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.entity.*;
import org.example.model.service.EmplacementService;
import org.example.model.service.ProduitService;
import org.example.model.service.TransfertService;
import org.example.util.AlertUtil;

import java.time.LocalDate;

/**
 * Controller for Stock Transfer management
 * Handles transfer creation, approval workflow, and inter-location movements
 */
public class TransfertController {

    // FXML UI Components - Transfer List
    @FXML private TableView<TransfertStock> transfertTable;
    @FXML private TableColumn<TransfertStock, String> numeroColumn;
    @FXML private TableColumn<TransfertStock, String> produitColumn;
    @FXML private TableColumn<TransfertStock, String> sourceColumn;
    @FXML private TableColumn<TransfertStock, String> destinationColumn;
    @FXML private TableColumn<TransfertStock, Integer> quantiteColumn;
    @FXML private TableColumn<TransfertStock, String> statutColumn;
    @FXML private TableColumn<TransfertStock, String> dateColumn;
    
    // FXML UI Components - Search & Filter
    @FXML private ComboBox<StatutTransfert> statutFilterCombo;
    @FXML private ComboBox<Emplacement> emplacementFilterCombo;
    @FXML private Button filterButton;
    @FXML private Button clearFilterButton;
    @FXML private Button refreshButton;
    
    // FXML UI Components - Form
    @FXML private Label numeroLabel;
    @FXML private ComboBox<Produit> produitCombo;
    @FXML private ComboBox<Emplacement> sourceCombo;
    @FXML private ComboBox<Emplacement> destinationCombo;
    @FXML private TextField quantiteField;
    @FXML private DatePicker dateTransfertPicker;
    @FXML private TextField demandeurField;
    @FXML private ComboBox<StatutTransfert> statutCombo;
    @FXML private TextArea motifArea;
    @FXML private TextArea notesArea;
    
    // FXML UI Components - Actions
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearFormButton;
    @FXML private Button approveButton;
    @FXML private Button refuseButton;
    @FXML private Button startButton;
    @FXML private Button completeButton;
    @FXML private Button cancelButton;
    
    // FXML UI Components - Info
    @FXML private Label stockSourceLabel;
    @FXML private Label stockDestLabel;
    @FXML private Label validatorLabel;
    
    // Services
    private final TransfertService transfertService = TransfertService.getInstance();
    private final EmplacementService emplacementService = EmplacementService.getInstance();
    private final ProduitService produitService = ProduitService.getInstance();
    
    // State
    private ObservableList<TransfertStock> transfertList = FXCollections.observableArrayList();
    private TransfertStock selectedTransfert = null;
    private boolean isEditMode = false;
    
    /**
     * Get current user ID from session
     */
    private Long getCurrentUserId() {
        return org.example.util.SessionManager.getInstance().getCurrentUser().getId();
    }

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        setupTransfertTable();
        setupCombos();
        loadAllTransferts();
        setupListeners();
        updateFormState();
    }

    /**
     * Setup transfer table
     */
    private void setupTransfertTable() {
        numeroColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNumeroTransfert()));
        produitColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getProduit().getNom()));
        sourceColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmplacementSource().getNom()));
        destinationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmplacementDestination().getNom()));
        quantiteColumn.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());
        statutColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatut().toString()));
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateTransfert().toString()));
        
        transfertTable.setItems(transfertList);
        
        // Selection listener
        transfertTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> handleTransfertSelected(newValue)
        );
    }

    /**
     * Setup combo boxes
     */
    private void setupCombos() {
        try {
            // Produit combo
            produitCombo.setItems(FXCollections.observableArrayList(produitService.getAllProduits()));
            produitCombo.setConverter(new javafx.util.StringConverter<Produit>() {
                @Override
                public String toString(Produit p) {
                    return p != null ? p.getNom() + " (" + p.getCode() + ")" : "";
                }
                @Override
                public Produit fromString(String string) {
                    return null;
                }
            });
            
            // Emplacement combos
            ObservableList<Emplacement> emplacements = FXCollections.observableArrayList(
                emplacementService.getActiveEmplacements());
            
            sourceCombo.setItems(emplacements);
            destinationCombo.setItems(emplacements);
            emplacementFilterCombo.setItems(emplacements);
            
            javafx.util.StringConverter<Emplacement> emplacementConverter = 
                new javafx.util.StringConverter<Emplacement>() {
                    @Override
                    public String toString(Emplacement e) {
                        return e != null ? e.getNom() + " (" + e.getCode() + ")" : "";
                    }
                    @Override
                    public Emplacement fromString(String string) {
                        return null;
                    }
                };
            
            sourceCombo.setConverter(emplacementConverter);
            destinationCombo.setConverter(emplacementConverter);
            emplacementFilterCombo.setConverter(emplacementConverter);
            
            // Statut combos
            statutCombo.setItems(FXCollections.observableArrayList(StatutTransfert.values()));
            statutFilterCombo.setItems(FXCollections.observableArrayList(StatutTransfert.values()));
            
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
    }

    /**
     * Setup listeners
     */
    private void setupListeners() {
        // Update stock info when source/product changes
        sourceCombo.valueProperty().addListener((obs, old, newVal) -> updateStockInfo());
        produitCombo.valueProperty().addListener((obs, old, newVal) -> updateStockInfo());
        destinationCombo.valueProperty().addListener((obs, old, newVal) -> updateStockInfo());
    }

    /**
     * Update stock information labels
     */
    private void updateStockInfo() {
        if (stockSourceLabel == null || stockDestLabel == null) return;
        
        Produit produit = produitCombo.getValue();
        Emplacement source = sourceCombo.getValue();
        Emplacement dest = destinationCombo.getValue();
        
        if (produit != null && source != null) {
            try {
                // Get current stock quantity for the product
                // Note: This shows global stock, not location-specific
                // For full multi-location support, need location-specific stock table
                int stockQty = produit.getQuantiteStock();
                stockSourceLabel.setText("Stock source: " + stockQty + " unités");
            } catch (Exception e) {
                stockSourceLabel.setText("Stock source: Erreur");
            }
        } else {
            stockSourceLabel.setText("Stock source: -");
        }
        
        if (produit != null && dest != null) {
            try {
                // Get current stock quantity for the product
                // Note: This shows global stock, not location-specific
                // For full multi-location support, need location-specific stock table
                int stockQty = produit.getQuantiteStock();
                stockDestLabel.setText("Stock destination: " + stockQty + " unités");
            } catch (Exception e) {
                stockDestLabel.setText("Stock destination: Erreur");
            }
        } else {
            stockDestLabel.setText("Stock destination: -");
        }
    }

    /**
     * Load all transfers
     */
    private void loadAllTransferts() {
        try {
            transfertList.setAll(transfertService.getAllTransferts());
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les transferts: " + e.getMessage());
        }
    }

    /**
     * Handle transfer selection
     */
    private void handleTransfertSelected(TransfertStock transfert) {
        selectedTransfert = transfert;
        if (transfert != null) {
            isEditMode = true;
            populateForm(transfert);
            updateFormState();
        }
    }

    /**
     * Populate form with transfer data
     */
    private void populateForm(TransfertStock transfert) {
        numeroLabel.setText(transfert.getNumeroTransfert());
        produitCombo.setValue(transfert.getProduit());
        sourceCombo.setValue(transfert.getEmplacementSource());
        destinationCombo.setValue(transfert.getEmplacementDestination());
        quantiteField.setText(String.valueOf(transfert.getQuantite()));
        dateTransfertPicker.setValue(transfert.getDateTransfert().toLocalDate());
        demandeurField.setText(transfert.getDemandeur() != null ? transfert.getDemandeur().getNom() : "");
        statutCombo.setValue(transfert.getStatut());
        motifArea.setText(transfert.getMotif());
        notesArea.setText(transfert.getNotes());
        
        if (transfert.getValidateurId() != null) {
            validatorLabel.setText("Validateur ID: " + transfert.getValidateurId());
        } else {
            validatorLabel.setText("Non validé");
        }
        
        updateStockInfo();
    }

    /**
     * Update form state based on mode and status
     */
    private void updateFormState() {
        boolean editing = isEditMode && selectedTransfert != null;
        
        saveButton.setDisable(editing);
        updateButton.setDisable(!editing);
        deleteButton.setDisable(!editing);
        
        if (editing) {
            StatutTransfert statut = selectedTransfert.getStatut();
            approveButton.setDisable(statut != StatutTransfert.EN_ATTENTE);
            refuseButton.setDisable(statut != StatutTransfert.EN_ATTENTE);
            startButton.setDisable(statut != StatutTransfert.APPROUVE);
            completeButton.setDisable(statut != StatutTransfert.EN_TRANSIT);
            cancelButton.setDisable(statut == StatutTransfert.RECU || statut == StatutTransfert.ANNULE);
        } else {
            approveButton.setDisable(true);
            refuseButton.setDisable(true);
            startButton.setDisable(true);
            completeButton.setDisable(true);
            cancelButton.setDisable(true);
        }
        
        produitCombo.setDisable(editing);
        sourceCombo.setDisable(editing);
        destinationCombo.setDisable(editing);
    }

    /**
     * Handle filter action
     */
    @FXML
    private void handleFilter() {
        try {
            StatutTransfert statut = statutFilterCombo.getValue();
            Emplacement emplacement = emplacementFilterCombo.getValue();
            
            if (statut != null) {
                transfertList.setAll(transfertService.getTransfertsByStatut(statut));
            } else if (emplacement != null) {
                transfertList.setAll(transfertService.getTransfertsByEmplacement(emplacement.getId()));
            } else {
                loadAllTransferts();
            }
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur de filtrage: " + e.getMessage());
        }
    }

    /**
     * Handle clear filter
     */
    @FXML
    private void handleClearFilter() {
        statutFilterCombo.setValue(null);
        emplacementFilterCombo.setValue(null);
        loadAllTransferts();
    }

    /**
     * Handle refresh
     */
    @FXML
    private void handleRefresh() {
        loadAllTransferts();
        handleClearForm();
    }

    /**
     * Handle new transfer
     */
    @FXML
    private void handleNew() {
        isEditMode = false;
        selectedTransfert = null;
        handleClearForm();
        updateFormState();
    }

    /**
     * Handle save (new transfer)
     */
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create TransfertStock object
            TransfertStock transfert = new TransfertStock();
            transfert.setProduit(produitCombo.getValue());
            transfert.setEmplacementSource(sourceCombo.getValue());
            transfert.setEmplacementDestination(destinationCombo.getValue());
            transfert.setQuantite(Integer.parseInt(quantiteField.getText().trim()));
            transfert.setMotif(motifArea.getText().trim());
            transfert.setCommentaire(demandeurField.getText().trim());
            
            TransfertStock created = transfertService.createTransfert(transfert);
            
            AlertUtil.showInfo("Succès", "Transfert créé avec succès! Numéro: " + created.getNumeroTransfert());
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de créer le transfert: " + e.getMessage());
        }
    }

    /**
     * Handle approve transfer
     */
    @FXML
    private void handleApprove() {
        if (selectedTransfert == null) return;
        
        if (!AlertUtil.showConfirmation("Confirmation", 
            "Approuver ce transfert?")) {
            return;
        }
        
        try {
            transfertService.approveTransfert(selectedTransfert.getId(), getCurrentUserId());
            AlertUtil.showInfo("Succès", "Transfert approuvé!");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible d'approuver: " + e.getMessage());
        }
    }

    /**
     * Handle refuse transfer
     */
    @FXML
    private void handleRefuse() {
        if (selectedTransfert == null) return;
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Refuser le transfert");
        dialog.setHeaderText("Motif du refus");
        dialog.setContentText("Veuillez entrer le motif du refus:");
        
        dialog.showAndWait().ifPresent(motif -> {
            try {
                transfertService.refuseTransfert(selectedTransfert.getId(), motif, getCurrentUserId());
                AlertUtil.showInfo("Succès", "Transfert refusé.");
                handleRefresh();
            } catch (Exception e) {
                AlertUtil.showError("Erreur", "Impossible de refuser: " + e.getMessage());
            }
        });
    }

    /**
     * Handle start transfer (put in transit)
     */
    @FXML
    private void handleStart() {
        if (selectedTransfert == null) return;
        
        if (!AlertUtil.showConfirmation("Confirmation", 
            "Démarrer le transfert? Le stock sera déduit de la source.")) {
            return;
        }
        
        try {
            transfertService.startTransfert(selectedTransfert.getId());
            AlertUtil.showInfo("Succès", "Transfert démarré (en transit)!");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de démarrer: " + e.getMessage());
        }
    }

    /**
     * Handle complete transfer
     */
    @FXML
    private void handleComplete() {
        if (selectedTransfert == null) return;
        
        if (!AlertUtil.showConfirmation("Confirmation", 
            "Compléter le transfert? Le stock sera ajouté à la destination.")) {
            return;
        }
        
        try {
            transfertService.completeTransfert(selectedTransfert.getId());
            AlertUtil.showInfo("Succès", "Transfert complété!");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de compléter: " + e.getMessage());
        }
    }

    /**
     * Handle cancel transfer
     */
    @FXML
    private void handleCancel() {
        if (selectedTransfert == null) return;
        
        if (!AlertUtil.showConfirmation("Confirmation", 
            "Annuler ce transfert?")) {
            return;
        }
        
        try {
            transfertService.cancelTransfert(selectedTransfert.getId());
            AlertUtil.showInfo("Succès", "Transfert annulé.");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible d'annuler: " + e.getMessage());
        }
    }

    /**
     * Handle clear form
     */
    @FXML
    private void handleClearForm() {
        numeroLabel.setText("Nouveau");
        produitCombo.setValue(null);
        sourceCombo.setValue(null);
        destinationCombo.setValue(null);
        quantiteField.clear();
        dateTransfertPicker.setValue(LocalDate.now());
        demandeurField.clear();
        statutCombo.setValue(StatutTransfert.EN_ATTENTE);
        motifArea.clear();
        notesArea.clear();
        validatorLabel.setText("Non validé");
        
        stockSourceLabel.setText("Stock source: -");
        stockDestLabel.setText("Stock destination: -");
        
        selectedTransfert = null;
        isEditMode = false;
        transfertTable.getSelectionModel().clearSelection();
        updateFormState();
    }

    /**
     * Validate form inputs
     */
    private boolean validateForm() {
        if (produitCombo.getValue() == null) {
            AlertUtil.showWarning("Validation", "Veuillez sélectionner un produit.");
            return false;
        }
        
        if (sourceCombo.getValue() == null) {
            AlertUtil.showWarning("Validation", "Veuillez sélectionner l'emplacement source.");
            return false;
        }
        
        if (destinationCombo.getValue() == null) {
            AlertUtil.showWarning("Validation", "Veuillez sélectionner l'emplacement destination.");
            return false;
        }
        
        if (sourceCombo.getValue().equals(destinationCombo.getValue())) {
            AlertUtil.showWarning("Validation", "Source et destination doivent être différents.");
            return false;
        }
        
        String quantiteStr = quantiteField.getText().trim();
        if (quantiteStr.isEmpty()) {
            AlertUtil.showWarning("Validation", "La quantité est requise.");
            return false;
        }
        
        try {
            int quantite = Integer.parseInt(quantiteStr);
            if (quantite <= 0) {
                AlertUtil.showWarning("Validation", "La quantité doit être positive.");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Validation", "Format de quantité invalide.");
            return false;
        }
        
        if (demandeurField.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation", "Le nom du demandeur est requis.");
            return false;
        }
        
        return true;
    }
}
