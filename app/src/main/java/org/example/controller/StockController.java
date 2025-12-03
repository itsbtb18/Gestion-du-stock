package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.model.entity.Produit;
import org.example.model.entity.MouvementStock;
import org.example.model.service.MagasinService;
import org.example.model.service.AlerteService;
import org.example.dao.ProduitDAO;
import org.example.dao.MouvementStockDAO;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * StockController - Controller for stock movements
 * Integrates with Observer pattern to trigger alerts
 */
public class StockController implements Initializable {
    
    @FXML private TableView<MouvementStock> tableMouvements;
    @FXML private TableColumn<MouvementStock, String> colProduit;
    @FXML private TableColumn<MouvementStock, String> colType;
    @FXML private TableColumn<MouvementStock, Integer> colQuantite;
    @FXML private TableColumn<MouvementStock, LocalDateTime> colDate;
    @FXML private TableColumn<MouvementStock, String> colMotif;
    
    @FXML private ComboBox<Produit> cmbProduit;
    @FXML private ComboBox<String> cmbTypeMouvement;
    @FXML private TextField txtQuantite;
    @FXML private TextArea txtMotif;
    @FXML private Label lblStockActuel;
    @FXML private Label lblNombreAlertes;
    
    private MagasinService magasinService;
    private AlerteService alerteService;
    private ProduitDAO produitDAO;
    private MouvementStockDAO mouvementDAO;
    private ObservableList<MouvementStock> listeMouvements;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        magasinService = MagasinService.getInstance();
        alerteService = magasinService.getAlerteService();
        produitDAO = magasinService.getProduitDAO();
        mouvementDAO = magasinService.getMouvementStockDAO();
        listeMouvements = FXCollections.observableArrayList();
        
        initializeTable();
        initializeForm();
        chargerMouvements();
        mettreAJourNombreAlertes();
    }
    
    private void initializeTable() {
        colProduit.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getProduit().getNom()
            )
        );
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
        
        tableMouvements.setItems(listeMouvements);
    }
    
    private void initializeForm() {
        // Initialize movement types
        cmbTypeMouvement.getItems().addAll("ENTREE", "SORTIE", "AJUSTEMENT", "RETOUR");
        
        // Product selection listener
        cmbProduit.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, nouveau) -> {
                if (nouveau != null) {
                    lblStockActuel.setText("Stock actuel: " + nouveau.getQuantiteStock());
                }
            }
        );
    }
    
    private void chargerMouvements() {
        // TODO: Load from DAO when implemented
        listeMouvements.clear();
    }
    
    @FXML
    private void handleEnregistrerMouvement() {
        if (!validerFormulaire()) {
            return;
        }
        
        try {
            Produit produit = cmbProduit.getValue();
            String type = cmbTypeMouvement.getValue();
            int quantite = Integer.parseInt(txtQuantite.getText());
            
            // Store old quantity for observer notification
            int ancienneQuantite = produit.getQuantiteStock();
            
            // Update stock based on movement type
            if ("ENTREE".equals(type) || "RETOUR".equals(type)) {
                produit.ajouterStock(quantite);
            } else if ("SORTIE".equals(type)) {
                produit.retirerStock(quantite);
            }
            
            int nouvelleQuantite = produit.getQuantiteStock();
            
            // ** OBSERVER PATTERN: Notify observers of stock change **
            alerteService.notifierChangementStock(produit, ancienneQuantite, nouvelleQuantite);
            
            // Create movement record
            // TODO: Save to DAO when implemented
            
            afficherMessage("Succès", "Mouvement enregistré", Alert.AlertType.INFORMATION);
            resetForm();
            mettreAJourNombreAlertes();
            
        } catch (IllegalArgumentException e) {
            afficherMessage("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            afficherMessage("Erreur", "Erreur: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleVoirAlertes() {
        // TODO: Navigate to alerts view
        System.out.println("Affichage des alertes...");
    }
    
    private boolean validerFormulaire() {
        if (cmbProduit.getValue() == null) {
            afficherMessage("Validation", "Veuillez sélectionner un produit", Alert.AlertType.WARNING);
            return false;
        }
        
        if (cmbTypeMouvement.getValue() == null) {
            afficherMessage("Validation", "Veuillez sélectionner un type de mouvement", Alert.AlertType.WARNING);
            return false;
        }
        
        try {
            int quantite = Integer.parseInt(txtQuantite.getText());
            if (quantite <= 0) {
                afficherMessage("Validation", "La quantité doit être positive", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            afficherMessage("Validation", "Quantité invalide", Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }
    
    private void resetForm() {
        cmbProduit.setValue(null);
        cmbTypeMouvement.setValue(null);
        txtQuantite.clear();
        txtMotif.clear();
        lblStockActuel.setText("Stock actuel: -");
    }
    
    private void mettreAJourNombreAlertes() {
        int nbAlertes = alerteService.getNombreTotalAlertes();
        lblNombreAlertes.setText("Alertes actives: " + nbAlertes);
        
        if (nbAlertes > 0) {
            lblNombreAlertes.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            lblNombreAlertes.setStyle("-fx-text-fill: green;");
        }
    }
    
    private void afficherMessage(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
