package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.model.entity.Produit;
import org.example.model.entity.Categorie;
import org.example.model.service.MagasinService;
import org.example.dao.ProduitDAO;
import org.example.util.InputValidator;
import org.example.exception.ValidationException;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;

public class ProduitController implements Initializable {
    
    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, String> colCode;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, Double> colPrix;
    @FXML private TableColumn<Produit, Integer> colStock;
    @FXML private TableColumn<Produit, String> colCategorie;
    @FXML private TableColumn<Produit, String> colUnite;
    
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
    
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnNouveau;
    @FXML private Button btnCalculatrice;
    @FXML private Button btnCalculatriceStock;
    @FXML private TextField txtRecherche;
    
    private MagasinService magasinService;
    private ProduitDAO produitDAO;
    private ObservableList<Produit> listeProduits;
    private Produit produitSelectionne;
    
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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        magasinService = MagasinService.getInstance();
        produitDAO = magasinService.getProduitDAO();
        listeProduits = FXCollections.observableArrayList();
        
        initializeTableColumns();
        
        loadCategories();
        
        chargerProduits();
        
        tableProduits.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> handleSelectionChange(newValue)
        );
        
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
        
        tableProduits.setItems(listeProduits);
    }
    
    private void chargerProduits() {
        listeProduits.clear();
        try {
            List<Produit> produits = produitDAO.findAll();
            listeProduits.addAll(produits);
        } catch (Exception e) {
            afficherMessage("Erreur", "Erreur lors du chargement des produits: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleAjouter() {
        if (!validerFormulaire()) {
            return;
        }
        
        try {
            Produit nouveauProduit = creerProduitDepuisFormulaire();
            
            produitDAO.save(nouveauProduit);
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
            
            produitDAO.update(produitSelectionne);
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
                produitDAO.delete(produitSelectionne.getId());
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
            listeProduits.clear();
            try {
                List<Produit> resultats = produitDAO.search(recherche);
                listeProduits.addAll(resultats);
            } catch (Exception e) {
                afficherMessage("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleActualiser() {
        chargerProduits();
        txtRecherche.clear();
        resetForm();
        afficherMessage("Succès", "Liste des produits actualisée", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleReinitialiser() {
        resetForm();
        tableProduits.getSelectionModel().clearSelection();
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
        try {
            
            InputValidator.validateNotEmpty(txtCode.getText(), "Code");
            InputValidator.validateCode(txtCode.getText(), "Code");
            
            InputValidator.validateMinLength(txtNom.getText(), "Nom", 3);
            InputValidator.validateMaxLength(txtNom.getText(), "Nom", 100);
            
            double prix = Double.parseDouble(txtPrix.getText());
            InputValidator.validatePositive(prix, "Prix");
            
            int stock = Integer.parseInt(txtStock.getText());
            InputValidator.validateNonNegative(stock, "Stock");
            
            int seuil = Integer.parseInt(txtSeuilAlerte.getText());
            InputValidator.validatePositive(seuil, "Seuil d'alerte");
            
            InputValidator.validateNotEmpty(txtUnite.getText(), "Unité");
            
            return true;
            
        } catch (ValidationException e) {
            afficherMessage("Erreur de validation", e.getMessage(), Alert.AlertType.WARNING);
            return false;
        } catch (NumberFormatException e) {
            afficherMessage("Erreur de saisie", 
                "Prix et Stock doivent être des nombres valides", 
                Alert.AlertType.WARNING);
            return false;
        }
    }
    
    private Produit creerProduitDepuisFormulaire() {
        Produit produit = new Produit();
        
        produit.setCode(InputValidator.normalizeCode(txtCode.getText()));
        produit.setNom(txtNom.getText().trim());
        produit.setDescription(txtDescription.getText().trim());
        produit.setPrix(Double.parseDouble(txtPrix.getText()));
        produit.setQuantiteStock(Integer.parseInt(txtStock.getText()));
        produit.setSeuilAlerte(Integer.parseInt(txtSeuilAlerte.getText()));
        produit.setCategorie(cmbCategorie.getValue());
        produit.setUnite(txtUnite.getText().trim());
        produit.setDateExpiration(dateExpiration.getValue());
        produit.setFournisseur(txtFournisseur.getText().trim());
        produit.setEmplacement(txtEmplacement.getText().trim());
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
        txtCode.setDisable(true); 
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
    
    @FXML
    private void handleShowCalculator() {
        showCalculator(txtPrix);
    }
    
    @FXML
    private void handleShowCalculatorStock() {
        showCalculator(txtStock);
    }
    
    private void showCalculator(TextField targetField) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Calculatrice");
        dialog.setHeaderText("Entrez une expression mathématique");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        
        TextField expressionField = new TextField();
        expressionField.setPromptText("Ex: 100 + 50 * 2");
        expressionField.setStyle("-fx-font-size: 14; -fx-padding: 10;");
        
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(5);
        buttonGrid.setVgap(5);
        buttonGrid.setStyle("-fx-padding: 10;");
        
        String[][] buttonLabels = {
            {"7", "8", "9", "/"},
            {"4", "5", "6", "*"},
            {"1", "2", "3", "-"},
            {"0", ".", "=", "+"}
        };
        
        for (int i = 0; i < buttonLabels.length; i++) {
            for (int j = 0; j < buttonLabels[i].length; j++) {
                String label = buttonLabels[i][j];
                Button btn = new Button(label);
                btn.setStyle("-fx-font-size: 16; -fx-padding: 10; -fx-min-width: 60; -fx-min-height: 50;");
                
                final String buttonLabel = label;
                btn.setOnAction(e -> {
                    if ("=".equals(buttonLabel)) {
                        try {
                            double result = evaluateExpression(expressionField.getText());
                            expressionField.setText(String.valueOf(result));
                        } catch (Exception ex) {
                            expressionField.setText("Erreur");
                        }
                    } else {
                        expressionField.appendText(buttonLabel);
                    }
                });
                
                buttonGrid.add(btn, j, i);
            }
        }
        
        content.getChildren().addAll(expressionField, buttonGrid);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double value = evaluateExpression(expressionField.getText());
                targetField.setText(String.format("%.2f", value));
            } catch (Exception e) {
                afficherMessage("Erreur", "Expression invalide", Alert.AlertType.ERROR);
            }
        }
    }
    
    private double evaluateExpression(String expression) throws Exception {
        
        expression = expression.trim();
        if (expression.isEmpty()) {
            return 0;
        }
        
        try {
            javax.script.ScriptEngineManager manager = new javax.script.ScriptEngineManager();
            javax.script.ScriptEngine engine = manager.getEngineByName("JavaScript");
            Object result = engine.eval(expression);
            if (result instanceof Number) {
                return ((Number) result).doubleValue();
            }
            return Double.parseDouble(result.toString());
        } catch (Exception e) {
            throw new Exception("Erreur d'evaluation: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAddCategorie() {
        Dialog<Categorie> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle Categorie");
        dialog.setHeaderText("Creer une nouvelle categorie");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        TextField codeField = new TextField();
        codeField.setPromptText("Code (ex: CAT-001)");
        
        TextField nomField = new TextField();
        nomField.setPromptText("Nom de la categorie");
        
        TextArea descField = new TextArea();
        descField.setPromptText("Description (optionnel)");
        descField.setPrefRowCount(2);
        
        content.getChildren().addAll(
            new Label("Code *:"), codeField,
            new Label("Nom *:"), nomField,
            new Label("Description:"), descField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String code = codeField.getText().trim();
                String nom = nomField.getText().trim();
                if (!code.isEmpty() && !nom.isEmpty()) {
                    Categorie cat = new Categorie();
                    cat.setCode(code);
                    cat.setNom(nom);
                    cat.setDescription(descField.getText().trim());
                    cat.setActif(true);
                    return cat;
                }
            }
            return null;
        });
        
        Optional<Categorie> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                org.example.dao.CategorieDAO categorieDAO = new org.example.dao.CategorieDAO();
                categorieDAO.save(result.get());
                loadCategories();
                cmbCategorie.setValue(result.get());
                afficherMessage("Succes", "Categorie creee avec succes", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                afficherMessage("Erreur", "Erreur lors de la creation: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleEditCategorie() {
        Categorie selected = cmbCategorie.getValue();
        if (selected == null) {
            afficherMessage("Attention", "Veuillez selectionner une categorie", Alert.AlertType.WARNING);
            return;
        }
        
        Dialog<Categorie> dialog = new Dialog<>();
        dialog.setTitle("Modifier Categorie");
        dialog.setHeaderText("Modifier la categorie: " + selected.getNom());
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        TextField codeField = new TextField(selected.getCode());
        TextField nomField = new TextField(selected.getNom());
        TextArea descField = new TextArea(selected.getDescription() != null ? selected.getDescription() : "");
        descField.setPrefRowCount(2);
        
        content.getChildren().addAll(
            new Label("Code *:"), codeField,
            new Label("Nom *:"), nomField,
            new Label("Description:"), descField
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String code = codeField.getText().trim();
                String nom = nomField.getText().trim();
                if (!code.isEmpty() && !nom.isEmpty()) {
                    selected.setCode(code);
                    selected.setNom(nom);
                    selected.setDescription(descField.getText().trim());
                    return selected;
                }
            }
            return null;
        });
        
        Optional<Categorie> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                org.example.dao.CategorieDAO categorieDAO = new org.example.dao.CategorieDAO();
                categorieDAO.update(result.get());
                loadCategories();
                cmbCategorie.setValue(result.get());
                afficherMessage("Succes", "Categorie modifiee avec succes", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                afficherMessage("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleDeleteCategorie() {
        Categorie selected = cmbCategorie.getValue();
        if (selected == null) {
            afficherMessage("Attention", "Veuillez selectionner une categorie", Alert.AlertType.WARNING);
            return;
        }
        
        Optional<ButtonType> confirm = afficherConfirmation(
            "Confirmation",
            "Voulez-vous vraiment supprimer la categorie '" + selected.getNom() + "'?\n" +
            "Les produits de cette categorie ne seront plus associes a aucune categorie."
        );
        
        if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
            try {
                org.example.dao.CategorieDAO categorieDAO = new org.example.dao.CategorieDAO();
                categorieDAO.delete(selected.getId());
                loadCategories();
                cmbCategorie.setValue(null);
                afficherMessage("Succes", "Categorie supprimee avec succes", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                afficherMessage("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    private void loadCategories() {
        try {
            org.example.dao.CategorieDAO categorieDAO = new org.example.dao.CategorieDAO();
            List<Categorie> categories = categorieDAO.findAll();
            cmbCategorie.getItems().clear();
            cmbCategorie.getItems().addAll(categories);
            
            cmbCategorie.setConverter(new javafx.util.StringConverter<Categorie>() {
                @Override
                public String toString(Categorie cat) {
                    return cat != null ? cat.getNom() : "";
                }
                
                @Override
                public Categorie fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            afficherMessage("Erreur", "Erreur lors du chargement des categories: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
