package org.example.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.entity.Fournisseur;
import org.example.model.service.FournisseurService;
import org.example.util.AlertUtil;

import java.util.List;

public class FournisseurController {

    @FXML private TableView<Fournisseur> fournisseurTable;
    @FXML private TableColumn<Fournisseur, String> codeColumn;
    @FXML private TableColumn<Fournisseur, String> nomColumn;
    @FXML private TableColumn<Fournisseur, String> contactColumn;
    @FXML private TableColumn<Fournisseur, String> telephoneColumn;
    @FXML private TableColumn<Fournisseur, String> emailColumn;
    @FXML private TableColumn<Fournisseur, Double> ratingColumn;
    @FXML private TableColumn<Fournisseur, String> actifColumn;
    
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button showActiveButton;
    @FXML private Button showAllButton;
    
    @FXML private TextField codeField;
    @FXML private TextField nomField;
    @FXML private TextField contactField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private TextField adresseField;
    @FXML private TextField villeField;
    @FXML private TextField paysField;
    @FXML private TextField codePostalField;
    @FXML private TextArea notesArea;
    @FXML private Slider ratingSlider;
    @FXML private Label ratingLabel;
    @FXML private CheckBox actifCheckBox;
    
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearFormButton;
    @FXML private Button rateButton;
    
    private final FournisseurService fournisseurService = FournisseurService.getInstance();
    
    private ObservableList<Fournisseur> fournisseurList = FXCollections.observableArrayList();
    private Fournisseur selectedFournisseur = null;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupFournisseurTable();
        loadAllFournisseurs();
        setupListeners();
        setupRatingSlider();
        updateFormState();
    }

    private void setupFournisseurTable() {
        codeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCode()));
        nomColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNom()));
        contactColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getContact()));
        telephoneColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTelephone()));
        emailColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmail()));
        ratingColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getNotePerforme()).asObject());
        actifColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isActif() ? "Oui" : "Non"));
        
        fournisseurTable.setItems(fournisseurList);
        
        fournisseurTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> handleFournisseurSelected(newValue)
        );
    }

    private void setupRatingSlider() {
        if (ratingSlider != null) {
            ratingSlider.setMin(0);
            ratingSlider.setMax(5);
            ratingSlider.setValue(3.0);
            ratingSlider.setBlockIncrement(0.5);
            ratingSlider.setMajorTickUnit(1);
            ratingSlider.setMinorTickCount(1);
            ratingSlider.setShowTickLabels(true);
            ratingSlider.setShowTickMarks(true);
            
            ratingSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (ratingLabel != null) {
                    ratingLabel.setText(String.format("%.1f / 5.0", newValue.doubleValue()));
                }
            });
        }
    }

    private void setupListeners() {
        
        if (searchField != null) {
            searchField.setOnAction(event -> handleSearch());
        }
    }

    private void loadAllFournisseurs() {
        try {
            List<Fournisseur> fournisseurs = fournisseurService.getAllFournisseurs();
            fournisseurList.setAll(fournisseurs);
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les fournisseurs: " + e.getMessage());
        }
    }

    private void handleFournisseurSelected(Fournisseur fournisseur) {
        selectedFournisseur = fournisseur;
        if (fournisseur != null) {
            isEditMode = true;
            populateForm(fournisseur);
            updateFormState();
        }
    }

    private void populateForm(Fournisseur fournisseur) {
        codeField.setText(fournisseur.getCode());
        nomField.setText(fournisseur.getNom());
        contactField.setText(fournisseur.getContact());
        telephoneField.setText(fournisseur.getTelephone());
        emailField.setText(fournisseur.getEmail());
        adresseField.setText(fournisseur.getAdresse());
        villeField.setText(fournisseur.getVille());
        paysField.setText(fournisseur.getPays());
        codePostalField.setText(fournisseur.getCodePostal());
        notesArea.setText(fournisseur.getNotes());
        actifCheckBox.setSelected(fournisseur.isActif());
        
        if (ratingSlider != null) {
            ratingSlider.setValue(fournisseur.getNotePerforme());
        }
    }

    private void updateFormState() {
        boolean editing = isEditMode && selectedFournisseur != null;
        
        if (saveButton != null) saveButton.setDisable(editing);
        if (updateButton != null) updateButton.setDisable(!editing);
        if (deleteButton != null) deleteButton.setDisable(!editing);
        if (rateButton != null) rateButton.setDisable(!editing);
        
        if (codeField != null) codeField.setEditable(!editing);
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadAllFournisseurs();
            return;
        }
        
        try {
            List<Fournisseur> results = fournisseurService.searchFournisseurs(query);
            fournisseurList.setAll(results);
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur de recherche: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadAllFournisseurs();
        handleClearForm();
    }

    @FXML
    private void handleShowActive() {
        try {
            List<Fournisseur> actifs = fournisseurService.getActiveFournisseurs();
            fournisseurList.setAll(actifs);
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les fournisseurs actifs: " + e.getMessage());
        }
    }

    @FXML
    private void handleShowAll() {
        loadAllFournisseurs();
    }

    @FXML
    private void handleNew() {
        isEditMode = false;
        selectedFournisseur = null;
        handleClearForm();
        updateFormState();
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Fournisseur fournisseur = new Fournisseur();
            fournisseur.setCode(codeField.getText().trim());
            fournisseur.setNom(nomField.getText().trim());
            fournisseur.setContact(contactField.getText().trim());
            fournisseur.setTelephone(telephoneField.getText().trim());
            fournisseur.setEmail(emailField.getText().trim());
            fournisseur.setAdresse(adresseField.getText().trim());
            fournisseur.setVille(villeField.getText().trim());
            fournisseur.setPays(paysField.getText().trim());
            fournisseur.setCodePostal(codePostalField.getText().trim());
            fournisseur.setNotes(notesArea.getText().trim());
            fournisseur.setActif(actifCheckBox.isSelected());
            
            fournisseurService.registerFournisseur(fournisseur);
            
            AlertUtil.showInfo("Succès", "Fournisseur créé avec succès!");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de créer le fournisseur: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedFournisseur == null || !validateForm()) {
            return;
        }
        
        try {
            selectedFournisseur.setNom(nomField.getText().trim());
            selectedFournisseur.setContact(contactField.getText().trim());
            selectedFournisseur.setTelephone(telephoneField.getText().trim());
            selectedFournisseur.setEmail(emailField.getText().trim());
            selectedFournisseur.setAdresse(adresseField.getText().trim());
            selectedFournisseur.setVille(villeField.getText().trim());
            selectedFournisseur.setPays(paysField.getText().trim());
            selectedFournisseur.setCodePostal(codePostalField.getText().trim());
            selectedFournisseur.setNotes(notesArea.getText().trim());
            selectedFournisseur.setActif(actifCheckBox.isSelected());
            
            fournisseurService.updateFournisseur(selectedFournisseur);
            
            AlertUtil.showInfo("Succès", "Fournisseur mis à jour avec succès!");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de mettre à jour le fournisseur: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedFournisseur == null) {
            return;
        }
        
        if (!AlertUtil.showConfirmation("Confirmation", 
            "Êtes-vous sûr de vouloir désactiver ce fournisseur?")) {
            return;
        }
        
        try {
            fournisseurService.deactivateFournisseur(selectedFournisseur.getId());
            AlertUtil.showInfo("Succès", "Fournisseur désactivé avec succès!");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de désactiver le fournisseur: " + e.getMessage());
        }
    }

    @FXML
    private void handleRate() {
        if (selectedFournisseur == null) {
            return;
        }
        
        try {
            double newRating = ratingSlider.getValue();
            fournisseurService.updatePerformanceRating(selectedFournisseur.getId(), newRating);
            
            AlertUtil.showInfo("Succès", 
                String.format("Note mise à jour: %.1f/5.0", newRating));
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de mettre à jour la note: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearForm() {
        codeField.clear();
        nomField.clear();
        contactField.clear();
        telephoneField.clear();
        emailField.clear();
        adresseField.clear();
        villeField.clear();
        paysField.clear();
        codePostalField.clear();
        notesArea.clear();
        actifCheckBox.setSelected(true);
        
        if (ratingSlider != null) {
            ratingSlider.setValue(3.0);
        }
        
        selectedFournisseur = null;
        isEditMode = false;
        fournisseurTable.getSelectionModel().clearSelection();
        updateFormState();
    }

    private boolean validateForm() {
        if (codeField.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation", "Le code fournisseur est requis.");
            return false;
        }
        
        if (nomField.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation", "Le nom du fournisseur est requis.");
            return false;
        }
        
        if (telephoneField.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation", "Le téléphone est requis.");
            return false;
        }
        
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            AlertUtil.showWarning("Validation", "Format d'email invalide.");
            return false;
        }
        
        return true;
    }
}
