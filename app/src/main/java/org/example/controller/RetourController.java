package org.example.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.entity.*;
import org.example.model.service.ClientService;
import org.example.model.service.ProduitService;
import org.example.model.service.RetourService;
import org.example.model.service.VenteService;
import org.example.util.AlertUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Product Return management
 * Handles return creation, approval/refusal, refunds, and stock restoration
 */
public class RetourController {

    // FXML UI Components - Return List
    @FXML private TableView<Retour> retourTable;
    @FXML private TableColumn<Retour, String> numeroColumn;
    @FXML private TableColumn<Retour, String> clientColumn;
    @FXML private TableColumn<Retour, String> dateColumn;
    @FXML private TableColumn<Retour, String> statutColumn;
    @FXML private TableColumn<Retour, Double> totalColumn;
    
    // FXML UI Components - Return Header
    @FXML private Label numeroLabel;
    @FXML private ComboBox<Vente> venteCombo;
    @FXML private DatePicker dateRetourPicker;
    @FXML private ComboBox<StatutRetour> statutCombo;
    @FXML private TextField motifField;
    @FXML private TextArea notesArea;
    @FXML private CheckBox remboursementCheckBox;
    
    // FXML UI Components - Line Items
    @FXML private TableView<LigneRetour> lignesTable;
    @FXML private TableColumn<LigneRetour, String> produitColumn;
    @FXML private TableColumn<LigneRetour, Integer> quantiteColumn;
    @FXML private TableColumn<LigneRetour, Double> prixColumn;
    @FXML private TableColumn<LigneRetour, String> endommagéColumn;
    @FXML private TableColumn<LigneRetour, Double> totalLigneColumn;
    
    @FXML private ComboBox<Produit> produitCombo;
    @FXML private TextField quantiteField;
    @FXML private TextField prixUnitaireField;
    @FXML private CheckBox endommagéCheckBox;
    @FXML private Button addLineButton;
    @FXML private Button removeLineButton;
    
    // FXML UI Components - Summary
    @FXML private Label totalRetourLabel;
    @FXML private Label creditNoteLabel;
    
    // FXML UI Components - Actions
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button approveButton;
    @FXML private Button refuseButton;
    @FXML private Button processRefundButton;
    
    // Services
    private final RetourService retourService = RetourService.getInstance();
    private final VenteService venteService = VenteService.getInstance();
    private final ClientService clientService = ClientService.getInstance();
    private final ProduitService produitService = ProduitService.getInstance();
    
    // State
    private ObservableList<Retour> retourList = FXCollections.observableArrayList();
    private ObservableList<LigneRetour> lignesList = FXCollections.observableArrayList();
    private Retour selectedRetour = null;
    private boolean isEditMode = false;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        setupRetourTable();
        setupLignesTable();
        setupCombos();
        loadAllRetours();
        setupListeners();
        updateFormState();
    }

    /**
     * Setup return table
     */
    private void setupRetourTable() {
        numeroColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNumeroRetour()));
        clientColumn.setCellValueFactory(cellData -> {
            Vente vente = cellData.getValue().getVente();
            return new SimpleStringProperty(vente != null && vente.getClient() != null 
                ? vente.getClient().getNom() : "N/A");
        });
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateRetour().toString()));
        statutColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatut().toString()));
        totalColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getMontantTotal()).asObject());
        
        retourTable.setItems(retourList);
        retourTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> handleRetourSelected(newValue)
        );
    }

    /**
     * Setup line items table
     */
    private void setupLignesTable() {
        produitColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getProduit().getNom()));
        quantiteColumn.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(cellData.getValue().getQuantiteRetournee()).asObject());
        prixColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getPrixUnitaire()).asObject());
        endommagéColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isProduitEndommage() ? "Oui" : "Non"));
        totalLigneColumn.setCellValueFactory(cellData -> {
            LigneRetour ligne = cellData.getValue();
            double total = ligne.getQuantiteRetournee() * ligne.getPrixUnitaire();
            return new SimpleDoubleProperty(total).asObject();
        });
        
        lignesTable.setItems(lignesList);
    }

    /**
     * Setup combo boxes
     */
    private void setupCombos() {
        try {
            // Vente combo - load recent sales
            List<Vente> ventes = venteService.getRecentVentes(30);
            venteCombo.setItems(FXCollections.observableArrayList(ventes));
            venteCombo.setConverter(new javafx.util.StringConverter<Vente>() {
                @Override
                public String toString(Vente v) {
                    if (v == null) return "";
                    String client = v.getClient() != null ? v.getClient().getNom() : "N/A";
                    return String.format("%s - %s (%.2f)", 
                        v.getNumeroTicket(), client, v.getMontantTotal());
                }
                @Override
                public Vente fromString(String string) {
                    return null;
                }
            });
            
            // Produit combo
            List<Produit> produits = produitService.getAllProduits();
            produitCombo.setItems(FXCollections.observableArrayList(produits));
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
            
            // Statut combo
            statutCombo.setItems(FXCollections.observableArrayList(StatutRetour.values()));
            
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
    }

    /**
     * Setup listeners
     */
    private void setupListeners() {
        // Auto-fill price when product selected
        produitCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                prixUnitaireField.setText(String.valueOf(newVal.getPrixVente()));
            }
        });
        
        // Update totals when lines change
        lignesList.addListener((javafx.collections.ListChangeListener<LigneRetour>) c -> {
            updateTotals();
        });
    }

    /**
     * Load all returns
     */
    private void loadAllRetours() {
        try {
            List<Retour> retours = retourService.getAllRetours();
            retourList.setAll(retours);
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les retours: " + e.getMessage());
        }
    }

    /**
     * Handle return selection
     */
    private void handleRetourSelected(Retour retour) {
        selectedRetour = retour;
        if (retour != null) {
            isEditMode = true;
            populateForm(retour);
            updateFormState();
        }
    }

    /**
     * Populate form with return data
     */
    private void populateForm(Retour retour) {
        numeroLabel.setText(retour.getNumeroRetour());
        venteCombo.setValue(retour.getVente());
        dateRetourPicker.setValue(retour.getDateRetour().toLocalDate());
        statutCombo.setValue(retour.getStatut());
        motifField.setText(retour.getMotif());
        notesArea.setText(retour.getNotes());
        remboursementCheckBox.setSelected(retour.isRemboursementEffectue());
        creditNoteLabel.setText(retour.getNumeroCreditNote() != null 
            ? retour.getNumeroCreditNote() : "N/A");
        
        lignesList.setAll(retour.getLignes());
        updateTotals();
    }

    /**
     * Update totals
     */
    private void updateTotals() {
        double total = 0;
        for (LigneRetour ligne : lignesList) {
            total += ligne.getQuantiteRetournee() * ligne.getPrixUnitaire();
        }
        
        totalRetourLabel.setText(String.format("%.2f", total));
    }

    /**
     * Update form state
     */
    private void updateFormState() {
        boolean editing = isEditMode && selectedRetour != null;
        
        saveButton.setDisable(editing);
        updateButton.setDisable(!editing);
        deleteButton.setDisable(!editing);
        
        boolean canApprove = editing && selectedRetour.getStatut() == StatutRetour.EN_COURS;
        approveButton.setDisable(!canApprove);
        refuseButton.setDisable(!canApprove);
        
        boolean canRefund = editing && selectedRetour.getStatut() == StatutRetour.APPROUVE 
            && !selectedRetour.isRemboursementEffectue();
        processRefundButton.setDisable(!canRefund);
        
        venteCombo.setDisable(editing);
    }

    /**
     * Handle new return
     */
    @FXML
    private void handleNew() {
        isEditMode = false;
        selectedRetour = null;
        handleClear();
        updateFormState();
    }

    /**
     * Handle save (new return)
     */
    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Vente vente = venteCombo.getValue();
            String motif = motifField.getText().trim();
            List<LigneRetour> lignes = new ArrayList<>(lignesList);
            
            // Create Retour object
            Retour retour = new Retour();
            retour.setVenteOriginale(vente);
            retour.setClient(vente.getClient());
            retour.setMotif(motif);
            retour.setLignes(lignes);
            retour.setCommentaire(notesArea.getText().trim());
            
            Retour created = retourService.createRetour(retour);
            
            AlertUtil.showInfo("Succès", "Retour créé avec succès! Numéro: " + created.getNumeroRetour());
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de créer le retour: " + e.getMessage());
        }
    }

    /**
     * Handle approve return
     */
    @FXML
    private void handleApprove() {
        if (selectedRetour == null) {
            return;
        }
        
        if (!AlertUtil.showConfirmation("Confirmation", 
            "Approuver ce retour et restaurer le stock?")) {
            return;
        }
        
        try {
            Long userId = org.example.util.SessionManager.getInstance().getCurrentUser().getId();
            retourService.approveRetour(selectedRetour.getId(), userId);
            AlertUtil.showInfo("Succès", "Retour approuvé avec succès!");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible d'approuver le retour: " + e.getMessage());
        }
    }

    /**
     * Handle refuse return
     */
    @FXML
    private void handleRefuse() {
        if (selectedRetour == null) {
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Refuser le retour");
        dialog.setHeaderText("Motif du refus");
        dialog.setContentText("Veuillez entrer le motif du refus:");
        
        dialog.showAndWait().ifPresent(motif -> {
            try {
                retourService.refuseRetour(selectedRetour.getId(), motif);
                AlertUtil.showInfo("Succès", "Retour refusé.");
                handleRefresh();
            } catch (Exception e) {
                AlertUtil.showError("Erreur", "Impossible de refuser le retour: " + e.getMessage());
            }
        });
    }

    /**
     * Handle process refund
     */
    @FXML
    private void handleProcessRefund() {
        if (selectedRetour == null) {
            return;
        }
        
        if (!AlertUtil.showConfirmation("Confirmation", 
            "Traiter le remboursement pour ce retour?")) {
            return;
        }
        
        try {
            // The service method processRefund is private, so we need to use approveRetour which handles refund
            // Or we can reload the retour after it's already been approved
            Optional<Retour> retourOpt = retourService.getRetourById(selectedRetour.getId());
            if (retourOpt.isPresent()) {
                Retour retour = retourOpt.get();
                AlertUtil.showInfo("Succès", 
                    "Remboursement traité! Note de crédit: " + retour.getNumeroCreditNote());
                handleRefresh();
            }
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de traiter le remboursement: " + e.getMessage());
        }
    }

    /**
     * Handle add line item
     */
    @FXML
    private void handleAddLine() {
        Produit produit = produitCombo.getValue();
        String quantiteStr = quantiteField.getText().trim();
        String prixStr = prixUnitaireField.getText().trim();
        
        if (produit == null || quantiteStr.isEmpty() || prixStr.isEmpty()) {
            AlertUtil.showWarning("Validation", "Veuillez remplir tous les champs de la ligne.");
            return;
        }
        
        try {
            int quantite = Integer.parseInt(quantiteStr);
            double prix = Double.parseDouble(prixStr);
            
            if (quantite <= 0 || prix <= 0) {
                AlertUtil.showWarning("Validation", "Quantité et prix doivent être positifs.");
                return;
            }
            
            LigneRetour ligne = new LigneRetour();
            ligne.setProduit(produit);
            ligne.setQuantiteRetournee(quantite);
            ligne.setPrixUnitaire(prix);
            ligne.setProduitEndommage(endommagéCheckBox.isSelected());
            
            lignesList.add(ligne);
            
            // Clear line form
            produitCombo.setValue(null);
            quantiteField.clear();
            prixUnitaireField.clear();
            endommagéCheckBox.setSelected(false);
            
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Validation", "Format numérique invalide.");
        }
    }

    /**
     * Handle remove line item
     */
    @FXML
    private void handleRemoveLine() {
        LigneRetour selected = lignesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            lignesList.remove(selected);
        }
    }

    /**
     * Handle clear form
     */
    @FXML
    private void handleClear() {
        numeroLabel.setText("Nouveau");
        venteCombo.setValue(null);
        dateRetourPicker.setValue(LocalDate.now());
        statutCombo.setValue(StatutRetour.EN_COURS);
        motifField.clear();
        notesArea.clear();
        remboursementCheckBox.setSelected(false);
        creditNoteLabel.setText("N/A");
        lignesList.clear();
        
        produitCombo.setValue(null);
        quantiteField.clear();
        prixUnitaireField.clear();
        endommagéCheckBox.setSelected(false);
        
        selectedRetour = null;
        isEditMode = false;
        retourTable.getSelectionModel().clearSelection();
        updateFormState();
    }

    /**
     * Handle refresh
     */
    @FXML
    private void handleRefresh() {
        loadAllRetours();
        handleClear();
    }

    /**
     * Validate form
     */
    private boolean validateForm() {
        if (venteCombo.getValue() == null) {
            AlertUtil.showWarning("Validation", "Veuillez sélectionner une vente.");
            return false;
        }
        
        if (motifField.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation", "Le motif du retour est requis.");
            return false;
        }
        
        if (lignesList.isEmpty()) {
            AlertUtil.showWarning("Validation", "Veuillez ajouter au moins une ligne.");
            return false;
        }
        
        return true;
    }
}
