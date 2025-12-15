package org.example.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.entity.*;
import org.example.model.service.ProduitService;
import org.example.model.service.RetourService;
import org.example.model.service.VenteService;
import org.example.util.AlertUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RetourController {

    @FXML private TableView<Retour> retourTable;
    @FXML private TableColumn<Retour, String> numeroColumn;
    @FXML private TableColumn<Retour, String> clientColumn;
    @FXML private TableColumn<Retour, String> dateColumn;
    @FXML private TableColumn<Retour, String> statutColumn;
    @FXML private TableColumn<Retour, Double> totalColumn;
    
    @FXML private Label numeroLabel;
    @FXML private ComboBox<Vente> venteCombo;
    @FXML private DatePicker dateRetourPicker;
    @FXML private ComboBox<StatutRetour> statutCombo;
    @FXML private TextField motifField;
    @FXML private TextArea notesArea;
    @FXML private CheckBox remboursementCheckBox;
    
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
    
    @FXML private Label totalRetourLabel;
    @FXML private Label creditNoteLabel;
    
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button approveButton;
    @FXML private Button refuseButton;
    @FXML private Button processRefundButton;
    
    private final RetourService retourService = RetourService.getInstance();
    private final VenteService venteService = VenteService.getInstance();
    private final ProduitService produitService = ProduitService.getInstance();
    
    private ObservableList<Retour> retourList = FXCollections.observableArrayList();
    private ObservableList<LigneRetour> lignesList = FXCollections.observableArrayList();
    private Retour selectedRetour = null;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupRetourTable();
        setupLignesTable();
        setupCombos();
        loadAllRetours();
        setupListeners();
        updateFormState();
    }

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

    private void setupCombos() {
        try {
            
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
            
            statutCombo.setItems(FXCollections.observableArrayList(StatutRetour.values()));
            
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
    }

    private void setupListeners() {
        
        produitCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                prixUnitaireField.setText(String.valueOf(newVal.getPrixVente()));
            }
        });
        
        lignesList.addListener((javafx.collections.ListChangeListener<LigneRetour>) c -> {
            updateTotals();
        });
    }

    private void loadAllRetours() {
        try {
            List<Retour> retours = retourService.getAllRetours();
            retourList.setAll(retours);
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les retours: " + e.getMessage());
        }
    }

    private void handleRetourSelected(Retour retour) {
        selectedRetour = retour;
        if (retour != null) {
            isEditMode = true;
            populateForm(retour);
            updateFormState();
        }
    }

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

    private void updateTotals() {
        double total = 0;
        for (LigneRetour ligne : lignesList) {
            total += ligne.getQuantiteRetournee() * ligne.getPrixUnitaire();
        }
        
        totalRetourLabel.setText(String.format("%.2f", total));
    }

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

    @FXML
    private void handleNew() {
        isEditMode = false;
        selectedRetour = null;
        handleClear();
        updateFormState();
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Vente vente = venteCombo.getValue();
            String motif = motifField.getText().trim();
            List<LigneRetour> lignes = new ArrayList<>(lignesList);
            
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
            
            produitCombo.setValue(null);
            quantiteField.clear();
            prixUnitaireField.clear();
            endommagéCheckBox.setSelected(false);
            
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Validation", "Format numérique invalide.");
        }
    }

    @FXML
    private void handleRemoveLine() {
        LigneRetour selected = lignesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            lignesList.remove(selected);
        }
    }

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

    @FXML
    private void handleRefresh() {
        loadAllRetours();
        handleClear();
    }

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
