package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dao.VenteDAO;
import org.example.dao.MouvementStockDAO;
import org.example.model.entity.Vente;
import org.example.model.entity.MouvementStock;
import org.example.util.CSVUtil;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * RapportController - Generate and export sales and stock reports
 * Provides date filtering and CSV export functionality
 */
public class RapportController implements Initializable {
    
    // FXML Components - Filters
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private ComboBox<String> comboTypeRapport;
    @FXML private Button btnGenerer;
    @FXML private Button btnExporterCSV;
    
    // FXML Components - Sales Table
    @FXML private TableView<Vente> tableVentes;
    @FXML private TableColumn<Vente, String> colNumero;
    @FXML private TableColumn<Vente, LocalDate> colDate;
    @FXML private TableColumn<Vente, Double> colMontant;
    @FXML private TableColumn<Vente, String> colClient;
    
    // FXML Components - Stock Movements Table
    @FXML private TableView<MouvementStock> tableMouvements;
    @FXML private TableColumn<MouvementStock, String> colProduit;
    @FXML private TableColumn<MouvementStock, String> colType;
    @FXML private TableColumn<MouvementStock, Integer> colQuantite;
    @FXML private TableColumn<MouvementStock, LocalDate> colDateMouvement;
    
    // FXML Components - Summary
    @FXML private Label lblTotalVentes;
    @FXML private Label lblNombreVentes;
    @FXML private Label lblMoyenneVente;
    
    // Data
    private final VenteDAO venteDAO = new VenteDAO();
    private final MouvementStockDAO mouvementDAO = new MouvementStockDAO();
    private ObservableList<Vente> ventesData = FXCollections.observableArrayList();
    private ObservableList<MouvementStock> mouvementsData = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize report type combo
        comboTypeRapport.setItems(FXCollections.observableArrayList(
            "Ventes", "Mouvements de Stock", "Tous"
        ));
        comboTypeRapport.setValue("Ventes");
        
        // Set default date range (last 30 days)
        dateFin.setValue(LocalDate.now());
        dateDebut.setValue(LocalDate.now().minusDays(30));
        
        // Setup sales table columns
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateVente"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantFinal"));
        colClient.setCellValueFactory(cellData -> {
            if (cellData.getValue().getClient() != null) {
                return new SimpleStringProperty(cellData.getValue().getClient().getNom());
            }
            return new SimpleStringProperty("Anonyme");
        });
        
        // Setup stock movements table columns
        colProduit.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getProduit() != null ? 
                cellData.getValue().getProduit().getNom() : "N/A"
            )
        );
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colDateMouvement.setCellValueFactory(new PropertyValueFactory<>("dateMouvement"));
        
        // Setup event handlers
        btnGenerer.setOnAction(e -> handleGenererRapport());
        btnExporterCSV.setOnAction(e -> handleExporterCSV());
        comboTypeRapport.setOnAction(e -> toggleTableVisibility());
        
        // Initial visibility
        toggleTableVisibility();
        
        // Load initial data
        handleGenererRapport();
    }
    
    /**
     * Generate report based on selected filters
     */
    @FXML
    private void handleGenererRapport() {
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();
        String typeRapport = comboTypeRapport.getValue();
        
        if (debut == null || fin == null) {
            showAlert("Erreur", "Veuillez sélectionner une période", Alert.AlertType.WARNING);
            return;
        }
        
        if (debut.isAfter(fin)) {
            showAlert("Erreur", "La date de début doit être avant la date de fin", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            if ("Ventes".equals(typeRapport) || "Tous".equals(typeRapport)) {
                loadVentesData(debut, fin);
            }
            
            if ("Mouvements de Stock".equals(typeRapport) || "Tous".equals(typeRapport)) {
                loadMouvementsData(debut, fin);
            }
            
            updateSummary();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la génération du rapport: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
            org.example.util.LoggerUtil.logError(RapportController.class, "Error generating report", e);
        }
    }
    
    /**
     * Load sales data for the period
     */
    private void loadVentesData(LocalDate debut, LocalDate fin) {
        List<Vente> ventes = venteDAO.findByDateRange(debut, fin);
        ventesData.clear();
        ventesData.addAll(ventes);
        tableVentes.setItems(ventesData);
    }
    
    /**
     * Load stock movements data for the period
     */
    private void loadMouvementsData(LocalDate debut, LocalDate fin) {
        List<MouvementStock> mouvements = mouvementDAO.findByDateRange(debut, fin);
        mouvementsData.clear();
        mouvementsData.addAll(mouvements);
        tableMouvements.setItems(mouvementsData);
    }
    
    /**
     * Update summary statistics
     */
    private void updateSummary() {
        if (!ventesData.isEmpty()) {
            double total = ventesData.stream()
                .mapToDouble(Vente::getMontantFinal)
                .sum();
            double moyenne = total / ventesData.size();
            
            lblTotalVentes.setText(String.format("%.2f DH", total));
            lblNombreVentes.setText(String.valueOf(ventesData.size()));
            lblMoyenneVente.setText(String.format("%.2f DH", moyenne));
        } else {
            lblTotalVentes.setText("0.00 DH");
            lblNombreVentes.setText("0");
            lblMoyenneVente.setText("0.00 DH");
        }
    }
    
    /**
     * Export current report to CSV
     */
    @FXML
    private void handleExporterCSV() {
        String typeRapport = comboTypeRapport.getValue();
        
        try {
            if ("Ventes".equals(typeRapport) && !ventesData.isEmpty()) {
                String filename = "ventes_" + LocalDate.now() + ".csv";
                CSVUtil.exportVentes(ventesData, filename);
                showAlert("Succès", "Rapport exporté vers: data/" + filename, Alert.AlertType.INFORMATION);
            } else if ("Mouvements de Stock".equals(typeRapport) && !mouvementsData.isEmpty()) {
                String filename = "mouvements_" + LocalDate.now() + ".csv";
                CSVUtil.exportMouvements(mouvementsData, filename);
                showAlert("Succès", "Rapport exporté vers: data/" + filename, Alert.AlertType.INFORMATION);
            } else {
                showAlert("Attention", "Aucune donnée à exporter", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'export: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Toggle table visibility based on report type
     */
    private void toggleTableVisibility() {
        String type = comboTypeRapport.getValue();
        boolean showVentes = "Ventes".equals(type) || "Tous".equals(type);
        boolean showMouvements = "Mouvements de Stock".equals(type) || "Tous".equals(type);
        
        tableVentes.setVisible(showVentes);
        tableVentes.setManaged(showVentes);
        tableMouvements.setVisible(showMouvements);
        tableMouvements.setManaged(showMouvements);
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
