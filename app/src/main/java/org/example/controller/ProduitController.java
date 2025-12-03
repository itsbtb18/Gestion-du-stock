package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.entity.Produit;
import org.example.model.entity.Categorie;
import org.example.model.service.MagasinService;
import org.example.dao.ProduitDAO;
import org.example.Util.ValidationUtil;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.Optional;

/**
 * ProduitController - Controller for product management (CRUD)
 * Part of the Controller layer in MVC architecture
 */
public class ProduitController implements Initializable {
    
    // FXML Components - Table
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, String> colCode;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, Double> colPrix;
    @FXML private TableColumn<Produit, Integer> colStock;
    @FXML private TableColumn<Produit, String> colCategorie;
    @FXML private TableColumn<Produit, String> colUnite;
    
    // FXML Components - Input Fields
    @FXML private TextField txtCode;
    @FXML private TextField txtNom;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtPrix;
    @FXML private TextField txtStock;
    @FXML private TextField txtSeuilAlerte;
    @FXML private ComboBox<Categorie> cmbCategorie;
    @FXML private TextField txtUnite;
    @FXML private DatePicker dateExpiration;
    @FXML private TextField txtFournisseur;
    @FXML private TextField txtEmplacement;
    @FXML private CheckBox chkActif;
    
    // FXML Components - Buttons
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnNouveau;
    @FXML private TextField txtRecherche;
    
    // Model and Data
    private MagasinService magasinService;
    private ProduitDAO produitDAO;
    private ObservableList<Produit> listeProduits;
    private Produit produitSelectionne;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        magasinService = MagasinService.getInstance();
        produitDAO = magasinService.getProduitDAO();
        listeProduits = FXCollections.observableArrayList();
        
        // Initialize table columns
        initializeTableColumns();
        
        // Load initial data
        chargerProduits();
        
        // Set up table selection listener
        tableProduits.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> handleSelectionChange(newValue)
        );
        
        // Initialize form state
        resetForm();
    }
    
    private void initializeTableColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colCategorie.setCellValueFactory(cellData -> {
            Categorie cat = cellData.getValue().getCategorie();
            return new javafx.beans.property.SimpleStringProperty(
                cat != null ? cat.getNom() : "N/A"
            );
        });
        colUnite.setCellValueFactory(new PropertyValueFactory<>("unite"));
        
        // Set table items
        tableProduits.setItems(listeProduits);
    }
    
    private void chargerProduits() {
        // TODO: Load from DAO when implemented
        // For now, create sample data
        listeProduits.clear();
        // listeProduits.addAll(produitDAO.findAll());
    }
    
    @FXML
    private void handleAjouter() {
        if (!validerFormulaire()) {
            return;
        }
        
        try {
            Produit nouveauProduit = creerProduitDepuisFormulaire();
            
            // TODO: Save to DAO when implemented
            // produitDAO.save(nouveauProduit);
            
            listeProduits.add(nouveauProduit);
            afficherMessage("Succès", "Produit ajouté avec succès", Alert.AlertType.INFORMATION);
            resetForm();
            
        } catch (Exception e) {
            afficherMessage("Erreur", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleModifier() {
        if (produitSelectionne == null) {
            afficherMessage("Attention", "Veuillez sélectionner un produit", Alert.AlertType.WARNING);
            return;
        }
        
        if (!validerFormulaire()) {
            return;
        }
        
        try {
            mettreAJourProduit(produitSelectionne);
            
            // TODO: Update in DAO when implemented
            // produitDAO.update(produitSelectionne);
            
            tableProduits.refresh();
            afficherMessage("Succès", "Produit modifié avec succès", Alert.AlertType.INFORMATION);
            
        } catch (Exception e) {
            afficherMessage("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleSupprimer() {
        if (produitSelectionne == null) {
            afficherMessage("Attention", "Veuillez sélectionner un produit", Alert.AlertType.WARNING);
            return;
        }
        
        Optional<ButtonType> result = afficherConfirmation(
            "Confirmation",
            "Voulez-vous vraiment supprimer ce produit ?"
        );
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // TODO: Delete from DAO when implemented
                // produitDAO.delete(produitSelectionne.getCode());
                
                listeProduits.remove(produitSelectionne);
                afficherMessage("Succès", "Produit supprimé avec succès", Alert.AlertType.INFORMATION);
                resetForm();
                
            } catch (Exception e) {
                afficherMessage("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleNouveau() {
        resetForm();
    }
    
    @FXML
    private void handleRecherche() {
        String recherche = txtRecherche.getText().toLowerCase();
        if (recherche.isEmpty()) {
            chargerProduits();
        } else {
            // TODO: Implement search in DAO
            // For now, simple filter
            listeProduits.clear();
            // Add filtered results
        }
    }
    
    private void handleSelectionChange(Produit produit) {
        produitSelectionne = produit;
        if (produit != null) {
            remplirFormulaire(produit);
            btnModifier.setDisable(false);
            btnSupprimer.setDisable(false);
        } else {
            btnModifier.setDisable(true);
            btnSupprimer.setDisable(true);
        }
    }
    
    private boolean validerFormulaire() {
        // TODO: Use ValidationUtil when implemented
        if (txtCode.getText().isEmpty() || txtNom.getText().isEmpty()) {
            afficherMessage("Validation", "Code et Nom sont obligatoires", Alert.AlertType.WARNING);
            return false;
        }
        
        try {
            Double.parseDouble(txtPrix.getText());
            Integer.parseInt(txtStock.getText());
        } catch (NumberFormatException e) {
            afficherMessage("Validation", "Prix et Stock doivent être numériques", Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }
    
    private Produit creerProduitDepuisFormulaire() {
        Produit produit = new Produit();
        produit.setCode(txtCode.getText());
        produit.setNom(txtNom.getText());
        produit.setDescription(txtDescription.getText());
        produit.setPrix(Double.parseDouble(txtPrix.getText()));
        produit.setQuantiteStock(Integer.parseInt(txtStock.getText()));
        produit.setSeuilAlerte(Integer.parseInt(txtSeuilAlerte.getText()));
        produit.setCategorie(cmbCategorie.getValue());
        produit.setUnite(txtUnite.getText());
        produit.setDateExpiration(dateExpiration.getValue());
        produit.setFournisseur(txtFournisseur.getText());
        produit.setEmplacement(txtEmplacement.getText());
        produit.setActif(chkActif.isSelected());
        return produit;
    }
    
    private void mettreAJourProduit(Produit produit) {
        produit.setNom(txtNom.getText());
        produit.setDescription(txtDescription.getText());
        produit.setPrix(Double.parseDouble(txtPrix.getText()));
        produit.setQuantiteStock(Integer.parseInt(txtStock.getText()));
        produit.setSeuilAlerte(Integer.parseInt(txtSeuilAlerte.getText()));
        produit.setCategorie(cmbCategorie.getValue());
        produit.setUnite(txtUnite.getText());
        produit.setDateExpiration(dateExpiration.getValue());
        produit.setFournisseur(txtFournisseur.getText());
        produit.setEmplacement(txtEmplacement.getText());
        produit.setActif(chkActif.isSelected());
    }
    
    private void remplirFormulaire(Produit produit) {
        txtCode.setText(produit.getCode());
        txtNom.setText(produit.getNom());
        txtDescription.setText(produit.getDescription());
        txtPrix.setText(String.valueOf(produit.getPrix()));
        txtStock.setText(String.valueOf(produit.getQuantiteStock()));
        txtSeuilAlerte.setText(String.valueOf(produit.getSeuilAlerte()));
        cmbCategorie.setValue(produit.getCategorie());
        txtUnite.setText(produit.getUnite());
        dateExpiration.setValue(produit.getDateExpiration());
        txtFournisseur.setText(produit.getFournisseur());
        txtEmplacement.setText(produit.getEmplacement());
        chkActif.setSelected(produit.isActif());
        txtCode.setDisable(true); // Code cannot be changed
    }
    
    private void resetForm() {
        txtCode.clear();
        txtNom.clear();
        txtDescription.clear();
        txtPrix.clear();
        txtStock.clear();
        txtSeuilAlerte.setText("10");
        cmbCategorie.setValue(null);
        txtUnite.clear();
        dateExpiration.setValue(null);
        txtFournisseur.clear();
        txtEmplacement.clear();
        chkActif.setSelected(true);
        txtCode.setDisable(false);
        
        produitSelectionne = null;
        tableProduits.getSelectionModel().clearSelection();
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }
    
    private void afficherMessage(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private Optional<ButtonType> afficherConfirmation(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
