package org.example.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.PieChart;
import org.example.model.entity.CategorieDepense;
import org.example.model.entity.Depense;
import org.example.model.service.DepenseService;
import org.example.util.AlertUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DepenseController {

    @FXML private TableView<Depense> depenseTable;
    @FXML private TableColumn<Depense, String> numeroColumn;
    @FXML private TableColumn<Depense, String> dateColumn;
    @FXML private TableColumn<Depense, String> categorieColumn;
    @FXML private TableColumn<Depense, String> descriptionColumn;
    @FXML private TableColumn<Depense, Double> montantColumn;
    @FXML private TableColumn<Depense, String> recurrentColumn;
    
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ComboBox<CategorieDepense> categorieFilterCombo;
    @FXML private Button filterButton;
    @FXML private Button clearFilterButton;
    @FXML private Button refreshButton;
    
    @FXML private Label numeroLabel;
    @FXML private DatePicker dateDepensePicker;
    @FXML private ComboBox<CategorieDepense> categorieCombo;
    @FXML private TextField montantField;
    @FXML private TextField descriptionField;
    @FXML private TextField beneficiaireField;
    @FXML private TextField numeroFactureField;
    @FXML private ComboBox<String> modePaiementCombo;
    @FXML private CheckBox recurrentCheckBox;
    @FXML private TextArea notesArea;
    
    @FXML private Button newButton;
    @FXML private Button saveButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearFormButton;
    
    @FXML private Label totalPeriodeLabel;
    @FXML private Label totalMoisLabel;
    @FXML private Label totalAnneeLabel;
    @FXML private Label moyenneMoisLabel;
    @FXML private PieChart categoryPieChart;
    
    private final DepenseService depenseService = DepenseService.getInstance();
    
    private ObservableList<Depense> depenseList = FXCollections.observableArrayList();
    private Depense selectedDepense = null;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupDepenseTable();
        setupCombos();
        loadAllDepenses();
        setupListeners();
        updateFormState();
        updateSummary();
    }

    private void setupDepenseTable() {
        numeroColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNumero()));
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateDepense().toString()));
        categorieColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategorie().toString()));
        descriptionColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDescription()));
        montantColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getMontant()).asObject());
        recurrentColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isRecurrent() ? "Oui" : "Non"));
        
        depenseTable.setItems(depenseList);
        
        depenseTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> handleDepenseSelected(newValue)
        );
    }

    private void setupCombos() {
        
        categorieCombo.setItems(FXCollections.observableArrayList(CategorieDepense.values()));
        categorieFilterCombo.setItems(FXCollections.observableArrayList(CategorieDepense.values()));
        
        ObservableList<String> modePaiements = FXCollections.observableArrayList(
            "Espèces", "Chèque", "Virement", "Carte bancaire", "Prélèvement"
        );
        modePaiementCombo.setItems(modePaiements);
    }

    private void setupListeners() {
        
        if (dateDebutPicker != null) {
            dateDebutPicker.valueProperty().addListener((obs, old, newVal) -> {
                if (newVal != null && dateFinPicker.getValue() != null) {
                    updateSummary();
                }
            });
        }
        
        if (dateFinPicker != null) {
            dateFinPicker.valueProperty().addListener((obs, old, newVal) -> {
                if (newVal != null && dateDebutPicker.getValue() != null) {
                    updateSummary();
                }
            });
        }
    }

    private void loadAllDepenses() {
        try {
            List<Depense> depenses = depenseService.getAllDepenses();
            depenseList.setAll(depenses);
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de charger les dépenses: " + e.getMessage());
        }
    }

    private void handleDepenseSelected(Depense depense) {
        selectedDepense = depense;
        if (depense != null) {
            isEditMode = true;
            populateForm(depense);
            updateFormState();
        }
    }

    private void populateForm(Depense depense) {
        numeroLabel.setText(depense.getNumero());
        dateDepensePicker.setValue(depense.getDateDepense());
        categorieCombo.setValue(depense.getCategorie());
        montantField.setText(String.valueOf(depense.getMontant()));
        descriptionField.setText(depense.getDescription());
        beneficiaireField.setText(depense.getBeneficiaire());
        numeroFactureField.setText(depense.getNumeroFacture());
        modePaiementCombo.setValue(depense.getModePaiement());
        recurrentCheckBox.setSelected(depense.isRecurrent());
        notesArea.setText(depense.getNotes());
    }

    private void updateFormState() {
        boolean editing = isEditMode && selectedDepense != null;
        
        if (saveButton != null) saveButton.setDisable(editing);
        if (updateButton != null) updateButton.setDisable(!editing);
        if (deleteButton != null) deleteButton.setDisable(!editing);
    }

    private void updateSummary() {
        try {
            
            LocalDate debut = dateDebutPicker != null && dateDebutPicker.getValue() != null 
                ? dateDebutPicker.getValue() : LocalDate.now().withDayOfMonth(1);
            LocalDate fin = dateFinPicker != null && dateFinPicker.getValue() != null 
                ? dateFinPicker.getValue() : LocalDate.now();
            
            double totalPeriode = depenseService.getTotalByPeriode(debut, fin);
            if (totalPeriodeLabel != null) {
                totalPeriodeLabel.setText(String.format("%.2f", totalPeriode));
            }
            
            LocalDate firstDayMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate lastDayMonth = LocalDate.now().withDayOfMonth(
                LocalDate.now().lengthOfMonth());
            double totalMois = depenseService.getTotalByPeriode(firstDayMonth, lastDayMonth);
            if (totalMoisLabel != null) {
                totalMoisLabel.setText(String.format("%.2f", totalMois));
            }
            
            LocalDate firstDayYear = LocalDate.now().withDayOfYear(1);
            LocalDate lastDayYear = LocalDate.now().withDayOfYear(
                LocalDate.now().lengthOfYear());
            double totalAnnee = depenseService.getTotalByPeriode(firstDayYear, lastDayYear);
            if (totalAnneeLabel != null) {
                totalAnneeLabel.setText(String.format("%.2f", totalAnnee));
            }
            
            double moyenne = totalAnnee / 12;
            if (moyenneMoisLabel != null) {
                moyenneMoisLabel.setText(String.format("%.2f", moyenne));
            }
            
            updateCategoryChart(debut, fin);
            
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de calculer les statistiques: " + e.getMessage());
        }
    }

    private void updateCategoryChart(LocalDate debut, LocalDate fin) {
        if (categoryPieChart == null) return;
        
        try {
            Map<CategorieDepense, Double> breakdown = depenseService.getExpenseBreakdown(debut, fin);
            
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            for (Map.Entry<CategorieDepense, Double> entry : breakdown.entrySet()) {
                if (entry.getValue() > 0) {
                    pieData.add(new PieChart.Data(
                        entry.getKey().toString() + " (" + String.format("%.2f", entry.getValue()) + ")",
                        entry.getValue()
                    ));
                }
            }
            
            categoryPieChart.setData(pieData);
            categoryPieChart.setTitle("Répartition par catégorie");
            
        } catch (Exception e) {
            
        }
    }

    @FXML
    private void handleFilter() {
        try {
            LocalDate debut = dateDebutPicker.getValue();
            LocalDate fin = dateFinPicker.getValue();
            CategorieDepense categorie = categorieFilterCombo.getValue();
            
            if (debut == null || fin == null) {
                AlertUtil.showWarning("Validation", "Veuillez sélectionner une période.");
                return;
            }
            
            List<Depense> filtered;
            if (categorie != null) {
                filtered = depenseService.getDepensesByCategorie(categorie, debut, fin);
            } else {
                filtered = depenseService.getDepensesByPeriode(debut, fin);
            }
            
            depenseList.setAll(filtered);
            updateSummary();
            
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur de filtrage: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearFilter() {
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        categorieFilterCombo.setValue(null);
        loadAllDepenses();
        updateSummary();
    }

    @FXML
    private void handleRefresh() {
        loadAllDepenses();
        handleClearForm();
        updateSummary();
    }

    @FXML
    private void handleNew() {
        isEditMode = false;
        selectedDepense = null;
        handleClearForm();
        updateFormState();
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }
        
        try {
            Depense depense = new Depense();
            depense.setDateDepense(dateDepensePicker.getValue());
            depense.setCategorie(categorieCombo.getValue());
            depense.setMontant(Double.parseDouble(montantField.getText().trim()));
            depense.setDescription(descriptionField.getText().trim());
            depense.setBeneficiaire(beneficiaireField.getText().trim());
            depense.setNumeroFacture(numeroFactureField.getText().trim());
            depense.setModePaiement(modePaiementCombo.getValue());
            depense.setRecurrent(recurrentCheckBox.isSelected());
            depense.setNotes(notesArea.getText().trim());
            
            depenseService.createDepense(depense);
            
            AlertUtil.showInfo("Succès", "Dépense créée avec succès!");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de créer la dépense: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedDepense == null || !validateForm()) {
            return;
        }
        
        try {
            selectedDepense.setDateDepense(dateDepensePicker.getValue());
            selectedDepense.setCategorie(categorieCombo.getValue());
            selectedDepense.setMontant(Double.parseDouble(montantField.getText().trim()));
            selectedDepense.setDescription(descriptionField.getText().trim());
            selectedDepense.setBeneficiaire(beneficiaireField.getText().trim());
            selectedDepense.setNumeroFacture(numeroFactureField.getText().trim());
            selectedDepense.setModePaiement(modePaiementCombo.getValue());
            selectedDepense.setRecurrent(recurrentCheckBox.isSelected());
            selectedDepense.setNotes(notesArea.getText().trim());
            
            depenseService.updateDepense(selectedDepense);
            
            AlertUtil.showInfo("Succès", "Dépense mise à jour avec succès!");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de mettre à jour la dépense: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedDepense == null) {
            return;
        }
        
        if (!AlertUtil.showConfirmation("Confirmation", 
            "Êtes-vous sûr de vouloir supprimer cette dépense?")) {
            return;
        }
        
        try {
            depenseService.deleteDepense(selectedDepense.getId());
            AlertUtil.showInfo("Succès", "Dépense supprimée avec succès!");
            handleRefresh();
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Impossible de supprimer la dépense: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearForm() {
        numeroLabel.setText("Nouveau");
        dateDepensePicker.setValue(LocalDate.now());
        categorieCombo.setValue(null);
        montantField.clear();
        descriptionField.clear();
        beneficiaireField.clear();
        numeroFactureField.clear();
        modePaiementCombo.setValue(null);
        recurrentCheckBox.setSelected(false);
        notesArea.clear();
        
        selectedDepense = null;
        isEditMode = false;
        depenseTable.getSelectionModel().clearSelection();
        updateFormState();
    }

    private boolean validateForm() {
        if (dateDepensePicker.getValue() == null) {
            AlertUtil.showWarning("Validation", "La date est requise.");
            return false;
        }
        
        if (categorieCombo.getValue() == null) {
            AlertUtil.showWarning("Validation", "La catégorie est requise.");
            return false;
        }
        
        String montantStr = montantField.getText().trim();
        if (montantStr.isEmpty()) {
            AlertUtil.showWarning("Validation", "Le montant est requis.");
            return false;
        }
        
        try {
            double montant = Double.parseDouble(montantStr);
            if (montant <= 0) {
                AlertUtil.showWarning("Validation", "Le montant doit être positif.");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Validation", "Format du montant invalide.");
            return false;
        }
        
        if (descriptionField.getText().trim().isEmpty()) {
            AlertUtil.showWarning("Validation", "La description est requise.");
            return false;
        }
        
        return true;
    }
}
