package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dao.VenteDAO;
import org.example.dao.ProduitDAO;
import org.example.dao.ClientDAO;
import org.example.model.entity.Vente;
import org.example.model.entity.Produit;
import org.example.model.service.StatistiquesService;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StatistiquesController - Display sales statistics, charts, and KPIs
 * Provides dashboard with key performance indicators
 */
public class StatistiquesController implements Initializable {
    
    // FXML Components - KPIs
    @FXML private Label lblChiffreAffaires;
    @FXML private Label lblNombreVentes;
    @FXML private Label lblPanierMoyen;
    @FXML private Label lblNombreClients;
    @FXML private Label lblProduitsEnStock;
    @FXML private Label lblAlertes;
    
    // FXML Components - Charts
    @FXML private LineChart<String, Number> chartVentes;
    @FXML private BarChart<String, Number> chartTopProduits;
    @FXML private PieChart chartCategories;
    
    // FXML Components - Recent Sales Table
    @FXML private TableView<Vente> tableRecentVentes;
    @FXML private TableColumn<Vente, String> colNumero;
    @FXML private TableColumn<Vente, LocalDate> colDate;
    @FXML private TableColumn<Vente, Double> colMontant;
    
    // FXML Components - Filters
    @FXML private ComboBox<String> comboPeriode;
    @FXML private Button btnRefresh;
    
    // Services and DAOs
    private final StatistiquesService statsService = StatistiquesService.getInstance();
    private final VenteDAO venteDAO = new VenteDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize period combo
        comboPeriode.setItems(FXCollections.observableArrayList(
            "Aujourd'hui", "Cette semaine", "Ce mois", "Cette année"
        ));
        comboPeriode.setValue("Ce mois");
        
        // Setup recent sales table
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateVente"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montantFinal"));
        
        // Setup event handlers
        btnRefresh.setOnAction(e -> loadDashboard());
        comboPeriode.setOnAction(e -> loadDashboard());
        
        // Load initial data
        loadDashboard();
    }
    
    /**
     * Load all dashboard data
     */
    private void loadDashboard() {
        try {
            LocalDate[] dateRange = getDateRange();
            LocalDate debut = dateRange[0];
            LocalDate fin = dateRange[1];
            
            loadKPIs(debut, fin);
            loadSalesChart(debut, fin);
            loadTopProductsChart(debut, fin);
            loadCategoriesChart(debut, fin);
            loadRecentSales();
            
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement du tableau de bord: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
            org.example.util.LoggerUtil.logError(StatistiquesController.class, "Error loading dashboard", e);
        }
    }
    
    /**
     * Get date range based on selected period
     */
    private LocalDate[] getDateRange() {
        LocalDate fin = LocalDate.now();
        LocalDate debut;
        
        String periode = comboPeriode.getValue();
        switch (periode) {
            case "Aujourd'hui":
                debut = fin;
                break;
            case "Cette semaine":
                debut = fin.minusDays(7);
                break;
            case "Cette année":
                debut = fin.minusYears(1);
                break;
            case "Ce mois":
            default:
                debut = fin.minusMonths(1);
                break;
        }
        
        return new LocalDate[]{debut, fin};
    }
    
    /**
     * Load key performance indicators
     */
    private void loadKPIs(LocalDate debut, LocalDate fin) {
        try {
            double[] stats = venteDAO.getStatistics(debut, fin);
            lblChiffreAffaires.setText(String.format("%.2f DH", stats[1]));
            lblNombreVentes.setText(String.valueOf((int)stats[0]));
            lblPanierMoyen.setText(String.format("%.2f DH", stats[2]));
            
            // Additional KPIs
            int nombreClients = clientDAO.findAll().size();
            lblNombreClients.setText(String.valueOf(nombreClients));
            
            int produitsEnStock = produitDAO.findAll().stream()
                .filter(p -> p.getQuantiteStock() > 0)
                .collect(Collectors.toList())
                .size();
            lblProduitsEnStock.setText(String.valueOf(produitsEnStock));
            
            int alertes = produitDAO.findAll().stream()
                .filter(p -> p.getQuantiteStock() < p.getSeuilAlerte())
                .collect(Collectors.toList())
                .size();
            lblAlertes.setText(String.valueOf(alertes));
        } catch (Exception e) {
            System.err.println("Error loading KPIs: " + e.getMessage());
            org.example.util.LoggerUtil.logError(StatistiquesController.class, "Error loading KPIs", e);
        }
    }
    
    /**
     * Load sales trend chart
     */
    private void loadSalesChart(LocalDate debut, LocalDate fin) {
        chartVentes.getData().clear();
        
        List<Vente> ventes = venteDAO.findByDateRange(debut, fin);
        
        // Group sales by date
        Map<LocalDate, Double> salesByDate = ventes.stream()
            .collect(Collectors.groupingBy(
                v -> v.getDateVente().toLocalDate(),
                Collectors.summingDouble(Vente::getMontantFinal)
            ));
        
        // Create series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Chiffre d'affaires");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        salesByDate.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                series.getData().add(new XYChart.Data<>(
                    entry.getKey().format(formatter),
                    entry.getValue()
                ));
            });
        
        chartVentes.getData().add(series);
    }
    
    /**
     * Load top products chart
     */
    private void loadTopProductsChart(LocalDate debut, LocalDate fin) {
        chartTopProduits.getData().clear();
        
        List<Produit> topProduits = produitDAO.findAll().stream()
            .sorted((p1, p2) -> Integer.compare(p2.getQuantiteStock(), p1.getQuantiteStock()))
            .limit(5)
            .toList();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Produits les plus vendus");
        
        for (Produit produit : topProduits) {
            // Use stock sold as proxy (would need sales data per product in real scenario)
            series.getData().add(new XYChart.Data<>(
                produit.getNom(),
                Math.max(0, 100 - produit.getQuantiteStock()) // Simple estimation
            ));
        }
        
        chartTopProduits.getData().add(series);
    }
    
    /**
     * Load categories distribution chart
     */
    private void loadCategoriesChart(LocalDate debut, LocalDate fin) {
        chartCategories.getData().clear();
        
        List<Produit> produits = produitDAO.findAll();
        
        // Group by category
        Map<String, Long> categoriesMap = produits.stream()
            .collect(Collectors.groupingBy(
                p -> p.getCategorie() != null ? p.getCategorie().getNom() : "AUTRE",
                Collectors.counting()
            ));
        
        // Add to pie chart
        categoriesMap.forEach((category, count) -> {
            PieChart.Data data = new PieChart.Data(category, count);
            chartCategories.getData().add(data);
        });
    }
    
    /**
     * Load recent sales into table
     */
    private void loadRecentSales() {
        List<Vente> recentVentes = venteDAO.findRecent(10);
        ObservableList<Vente> data = FXCollections.observableArrayList(recentVentes);
        tableRecentVentes.setItems(data);
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
