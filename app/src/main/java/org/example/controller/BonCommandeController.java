package org.example.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.entity.*;
import org.example.model.service.FournisseurService;
import org.example.model.service.ProduitService;
import org.example.util.AlertUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BonCommandeController {

    @FXML private TableView<BonCommande> commandeTable;
    @FXML private TableColumn<BonCommande, String> numeroColumn;
    @FXML private TableColumn<BonCommande, String> fournisseurColumn;
    @FXML private TableColumn<BonCommande, String> dateColumn;
    @FXML private TableColumn<BonCommande, String> statutColumn;
    @FXML private TableColumn<BonCommande, Double> totalColumn;
   
    @FXML private Label numeroLabel;
    @FXML private ComboBox<Fournisseur> fournisseurCombo;
    @FXML private DatePicker dateCommandePicker;
    @FXML private DatePicker dateLivraisonPicker;
    @FXML private ComboBox<StatutCommande> statutCombo;
    @FXML private TextArea notesArea;
   
    @FXML private TableView<LigneCommande> lignesTable;
    @FXML private TableColumn<LigneCommande, String> produitColumn;
    @FXML private TableColumn<LigneCommande, Integer> quantiteColumn;
    @FXML private TableColumn<LigneCommande, Double> prixColumn;
    @FXML private TableColumn<LigneCommande, Double> totalLigneColumn;
   
    @FXML private ComboBox<Produit> produitCombo;
    @FXML private TextField quantiteField;
    @FXML private TextField prixUnitaireField;
    @FXML private Button addLineButton;
    @FXML private Button removeLineButton;
   
    @FXML private Label totalHTLabel;
    @FXML private Label taxeLabel;
    @FXML private Label totalTTCLabel;
   
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button approveButton;
    @FXML private Button receiveButton;
   
    private final FournisseurService fournisseurService = FournisseurService.getInstance();
    private final ProduitService produitService = ProduitService.getInstance();
    private final org.example.model.service.BonCommandeService bonCommandeService =
        org.example.model.service.BonCommandeService.getInstance();
   
    private ObservableList<BonCommande> commandeList = FXCollections.observableArrayList();
    private ObservableList<LigneCommande> lignesList = FXCollections.observableArrayList();
    private BonCommande selectedCommande = null;
    private boolean isEditMode = false;
    private static final double TVA_RATE = 0.20; 
   
    public static class LigneCommande {
        private final Produit produit;
        private int quantite;
        private double prixUnitaire;
       
        public LigneCommande(Produit produit, int quantite, double prixUnitaire) {
            this.produit = produit;
            this.quantite = quantite;
            this.prixUnitaire = prixUnitaire;
        }
       
        public Produit getProduit() {
            return produit;
        }
       
        public int getQuantite() {
            return quantite;
        }
       
        public void setQuantite(int quantite) {
            this.quantite = quantite;
        }
       
        public double getPrixUnitaire() {
            return prixUnitaire;
        }
       
        public void setPrixUnitaire(double prixUnitaire) {
            this.prixUnitaire = prixUnitaire;
        }
       
        public double getTotal() {
            return quantite * prixUnitaire;
        }
    }

    @FXML
    public void initialize() {
        setupCommandeTable();
        setupLignesTable();
        setupCombos();
        loadAllCommandes();
        setupListeners();
        updateFormState();
    }

    private void setupCommandeTable() {
        numeroColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getNumero()));
        fournisseurColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getFournisseur().getNom()));
        dateColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getDateCommande().toString()));
        statutColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStatut().toString()));
        totalColumn.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(cellData.getValue().getMontantTotal()).asObject());

        commandeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        commandeTable.setItems(commandeList);
        commandeTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> handleCommandeSelected(newValue)
        );
    }

    private void setupLignesTable() {
        produitColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getProduit().getNom()));
        quantiteColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());
        prixColumn.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(cellData.getValue().getPrixUnitaire()).asObject());
        totalLigneColumn.setCellValueFactory(cellData -> {
            LigneCommande ligne = cellData.getValue();
            double total = ligne.getQuantite() * ligne.getPrixUnitaire();
            return new SimpleDoubleProperty(total).asObject();
        });
       
        lignesTable.setItems(lignesList);
    }

    private void setupCombos() {
        try {
            
            List<Fournisseur> fournisseurs = fournisseurService.getActiveFournisseurs();
            fournisseurCombo.setItems(FXCollections.observableArrayList(fournisseurs));
            fournisseurCombo.setConverter(new javafx.util.StringConverter<Fournisseur>() {
                @Override
                public String toString(Fournisseur f) {
                    return f != null ? f.getNom() : "";
                }
                @Override
                public Fournisseur fromString(String string) {
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
           
            statutCombo.setItems(FXCollections.observableArrayList(StatutCommande.values()));
           
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
    }

    private void setupListeners() {
        
        produitCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                prixUnitaireField.setText(String.valueOf(newVal.getPrix()));
            }
        });
       
        lignesList.addListener((javafx.collections.ListChangeListener<LigneCommande>) c -> {
            updateTotals();
        });
    }

    private void loadAllCommandes() {
        try {
            commandeList.setAll(bonCommandeService.getAllCommandes());
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les commandes: " + e.getMessage());
            commandeList.clear();
        }
    }

    private void handleCommandeSelected(BonCommande commande) {
        selectedCommande = commande;
        if (commande != null) {
            isEditMode = true;
            populateForm(commande);
            updateFormState();
        }
    }

    private void populateForm(BonCommande commande) {
        numeroLabel.setText(commande.getNumero());
        fournisseurCombo.setValue(commande.getFournisseur());
        dateCommandePicker.setValue(commande.getDateCommande().toLocalDate());
        dateLivraisonPicker.setValue(commande.getDateLivraisonPrevue());
        statutCombo.setValue(commande.getStatut());
        notesArea.setText(commande.getCommentaire() != null ? commande.getCommentaire() : "");
       
        lignesList.clear();
        for (LigneBonCommande lbc : commande.getLignes()) {
            lignesList.add(new LigneCommande(
                lbc.getProduit(),
                lbc.getQuantiteCommandee(),
                lbc.getPrixUnitaire()
            ));
        }
        updateTotals();
    }

    private void updateTotals() {
        double totalHT = 0;
        for (LigneCommande ligne : lignesList) {
            totalHT += ligne.getQuantite() * ligne.getPrixUnitaire();
        }
       
        double taxe = totalHT * TVA_RATE;
        double totalTTC = totalHT + taxe;
       
        totalHTLabel.setText(String.format("%.2f", totalHT));
        taxeLabel.setText(String.format("%.2f", taxe));
        totalTTCLabel.setText(String.format("%.2f", totalTTC));
    }

    private void updateFormState() {
        boolean editing = isEditMode && selectedCommande != null;
       
        saveButton.setDisable(editing);
        updateButton.setDisable(!editing);
        deleteButton.setDisable(!editing);
        approveButton.setDisable(!editing || selectedCommande.getStatut() != StatutCommande.VALIDEE);
        receiveButton.setDisable(!editing || selectedCommande.getStatut() != StatutCommande.ENVOYEE);
       
        fournisseurCombo.setDisable(editing);
    }

    @FXML
    private void handleNew() {
        isEditMode = false;
        selectedCommande = null;
        handleClear();
        updateFormState();
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
       
        try {
            BonCommande commande = new BonCommande();
            commande.setNumero("BC-" + System.currentTimeMillis());
            commande.setFournisseur(fournisseurCombo.getValue());
            commande.setDateCommande(LocalDateTime.now());
            commande.setDateLivraisonPrevue(dateLivraisonPicker.getValue());
            commande.setStatut(StatutCommande.BROUILLON);
            commande.setCommentaire(notesArea.getText().trim());
           
            List<LigneBonCommande> lignes = new ArrayList<>();
            for (LigneCommande ligne : lignesList) {
                LigneBonCommande lbc = new LigneBonCommande(
                    ligne.getProduit(),
                    ligne.getQuantite(),
                    ligne.getPrixUnitaire()
                );
                lignes.add(lbc);
            }
            commande.setLignes(lignes);
           
            double total = 0;
            for (LigneCommande ligne : lignesList) {
                total += ligne.getQuantite() * ligne.getPrixUnitaire();
            }
            commande.setMontantTotal(total * (1 + TVA_RATE));
           
            if (isEditMode && selectedCommande != null) {
                bonCommandeService.updateCommande(commande);
                AlertUtil.showInfo("Succès", "Bon de commande mis à jour avec succès!");
            } else {
                bonCommandeService.createCommande(commande);
                AlertUtil.showInfo("Succès", "Bon de commande créé avec succès!");
            }
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de créer le bon de commande: " + e.getMessage());
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
           
            LigneCommande ligne = new LigneCommande(produit, quantite, prix);
            lignesList.add(ligne);
           
            produitCombo.setValue(null);
            quantiteField.clear();
            prixUnitaireField.clear();
           
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Validation", "Format numérique invalide.");
        }
    }

    @FXML
    private void handleRemoveLine() {
        LigneCommande selected = lignesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            lignesList.remove(selected);
        }
    }

    @FXML
    private void handleClear() {
        numeroLabel.setText("Nouveau");
        fournisseurCombo.setValue(null);
        dateCommandePicker.setValue(LocalDate.now());
        dateLivraisonPicker.setValue(LocalDate.now().plusDays(7));
        statutCombo.setValue(StatutCommande.BROUILLON);
        notesArea.clear();
        lignesList.clear();
       
        produitCombo.setValue(null);
        quantiteField.clear();
        prixUnitaireField.clear();
       
        selectedCommande = null;
        isEditMode = false;
        commandeTable.getSelectionModel().clearSelection();
        updateFormState();
    }

    @FXML
    private void handleRefresh() {
        loadAllCommandes();
        handleClear();
    }

    @FXML
    private void handleApprove() {
        BonCommande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            AlertUtil.showWarning("Sélection", "Veuillez sélectionner une commande à approuver.");
            return;
        }
       
        if (selectedCommande.getStatut() != StatutCommande.BROUILLON &&
            selectedCommande.getStatut() != StatutCommande.VALIDEE) {
            AlertUtil.showWarning("Statut", "Seules les commandes en brouillon ou validées peuvent être approuvées.");
            return;
        }
       
        try {
            selectedCommande.setStatut(StatutCommande.VALIDEE);
            bonCommandeService.updateCommande(selectedCommande);
            loadAllCommandes();
            AlertUtil.showInfo("Succès", "Commande approuvée avec succès.");
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur lors de l'approbation: " + e.getMessage());
        }
    }

    @FXML
    private void handleReceive() {
        BonCommande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            AlertUtil.showWarning("Sélection", "Veuillez sélectionner une commande à réceptionner.");
            return;
        }
       
        if (selectedCommande.getStatut() != StatutCommande.VALIDEE &&
            selectedCommande.getStatut() != StatutCommande.ENVOYEE) {
            AlertUtil.showWarning("Statut", "Seules les commandes validées ou envoyées peuvent être réceptionnées.");
            return;
        }
       
        try {
            selectedCommande.setStatut(StatutCommande.RECUE);
            bonCommandeService.updateCommande(selectedCommande);
           
            for (LigneBonCommande ligne : selectedCommande.getLignes()) {
                Produit produit = ligne.getProduit();
                produit.setQuantiteStock(produit.getQuantiteStock() + ligne.getQuantiteCommandee());
                produitService.updateProduit(produit);
            }
           
            loadAllCommandes();
            AlertUtil.showInfo("Succès", "Commande réceptionnée avec succès. Stock mis à jour.");
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur lors de la réception: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        if (fournisseurCombo.getValue() == null) {
            AlertUtil.showWarning("Validation", "Veuillez sélectionner un fournisseur.");
            return false;
        }
       
        if (lignesList.isEmpty()) {
            AlertUtil.showWarning("Validation", "Veuillez ajouter au moins une ligne.");
            return false;
        }
       
        return true;
    }
}