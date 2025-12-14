package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import org.example.model.entity.Produit;
import org.example.model.entity.Vente;
import org.example.model.service.*;
import org.example.util.LoggerUtil;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

/**
 * DashboardController - Controller for the main dashboard view
 * Displays KPIs, charts, alerts, and recent activity
 */
public class DashboardController implements Initializable {
    
    private static final Logger logger = LoggerUtil.getLogger(DashboardController.class);
    
    // FXML Components - Header
    @FXML private ComboBox<String> periodCombo;
    @FXML private Button refreshButton;
    
    // FXML Components - KPI Cards
    @FXML private Label lblTodaySales;
    @FXML private Label lblSalesChange;
    @FXML private Label lblTodayRevenue;
    @FXML private Label lblRevenueChange;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblProductsChange;
    @FXML private Label lblLowStockAlert;
    @FXML private Label lblTotalCustomers;
    @FXML private Label lblNewCustomers;
    
    // FXML Components - Charts
    @FXML private LineChart<String, Number> salesChart;
    @FXML private CategoryAxis salesXAxis;
    @FXML private NumberAxis salesYAxis;
    
    @FXML private BarChart<String, Number> topProductsChart;
    @FXML private CategoryAxis productsXAxis;
    @FXML private NumberAxis productsYAxis;
    
    // FXML Components - Lists & Tables
    @FXML private ListView<String> alertsList;
    @FXML private TableView<Vente> recentSalesTable;
    @FXML private TableColumn<Vente, String> colSaleNumber;
    @FXML private TableColumn<Vente, String> colSaleTime;
    @FXML private TableColumn<Vente, String> colSaleCustomer;
    @FXML private TableColumn<Vente, String> colSaleAmount;
    
    // Services
    private final VenteService venteService = VenteService.getInstance();
    private final ProduitService produitService = ProduitService.getInstance();
    private final ClientService clientService = ClientService.getInstance();
    private final StockService stockService = StockService.getInstance();
    private final StatistiquesService statistiquesService = StatistiquesService.getInstance();
    
    // State
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize period combo items
        periodCombo.setItems(FXCollections.observableArrayList(
            "Aujourd'hui",
            "Cette semaine",
            "Ce mois",
            "Cette année",
            "Personnalisé"
        ));
        
        // Set default period to "Aujourd'hui"
        periodCombo.setValue("Aujourd'hui");
        periodCombo.setOnAction(event -> handlePeriodChange());
        
        // Initialize table columns
        setupRecentSalesTable();
        
        // Load initial data
        setDateRange("Aujourd'hui");
        loadDashboardData();
        
        logger.info("Dashboard initialized successfully");
    }
    
    /**
     * Setup recent sales table columns
     */
    private void setupRecentSalesTable() {
        colSaleNumber.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNumero()));
        
        colSaleTime.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getDateVente();
            String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            return new SimpleStringProperty(time);
        });
        
        colSaleCustomer.setCellValueFactory(cellData -> {
            String customerName = cellData.getValue().getClient() != null 
                ? cellData.getValue().getClient().getNom()
                : "Client anonyme";
            return new SimpleStringProperty(customerName);
        });
        
        colSaleAmount.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("%.2f DH", cellData.getValue().getMontantFinal())));

        // Fix: Remove extra empty column by using unconstrained resize policy
        recentSalesTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }
    
    /**
     * Handle period selection change
     */
    @FXML
    private void handlePeriodChange() {
        String period = periodCombo.getValue();
        setDateRange(period);
        loadDashboardData();
    }
    
    /**
     * Set date range based on period selection
     */
    private void setDateRange(String period) {
        LocalDate today = LocalDate.now();
        
        switch (period) {
            case "Aujourd'hui":
                startDate = today;
                endDate = today;
                break;
            case "Cette semaine":
                startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
                endDate = today;
                break;
            case "Ce mois":
                startDate = today.withDayOfMonth(1);
                endDate = today;
                break;
            case "Cette année":
                startDate = today.withDayOfYear(1);
                endDate = today;
                break;
            case "Personnalisé":
                // TODO: Show date picker dialog for custom range
                logger.info("Custom date range selected - dialog not yet implemented");
                break;
            default:
                startDate = today;
                endDate = today;
        }
    }
    
    /**
     * Load all dashboard data
     */
    private void loadDashboardData() {
        try {
            loadKPIs();
            loadSalesChart();
            loadTopProductsChart();
            loadAlerts();
            loadRecentSales();
            
            logger.info("Dashboard data loaded successfully for period: " + periodCombo.getValue());
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error loading dashboard data", e);
        }
    }
    
    /**
     * Load KPI values
     */
    private void loadKPIs() {
        try {
            // Sales KPI - count ventes in date range
            List<Vente> ventes = venteService.getVentesByDateRange(startDate, endDate);
            lblTodaySales.setText(String.valueOf(ventes.size()));
            
            // TODO: Calculate sales change percentage vs previous period
            lblSalesChange.setText("+0% vs hier");
            // Revenue KPI - sum up ventes
            double totalRevenue = ventes.stream()
                .mapToDouble(v -> v.getMontantFinal())
                .sum();
            lblTodayRevenue.setText(String.format("%.2f DH", totalRevenue));
            
            // TODO: Calculate revenue change percentage vs previous period
            
            // Products KPI
            int totalProducts = produitService.getProduitCount();
            lblTotalProducts.setText(String.valueOf(totalProducts));
            
            // Low stock alerts
            List<Produit> lowStock = produitService.getLowStockProduits();
            lblLowStockAlert.setText(String.valueOf(lowStock.size()));
            if (lowStock.size() > 0) {
                lblProductsChange.setStyle("-fx-text-fill: #e74c3c;");
            }
            
            // Customers KPI
            int totalClients = clientService.getClientCount();
            lblTotalCustomers.setText(String.valueOf(totalClients));
            
            // TODO: Calculate new customers this month
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error loading KPIs", e);
        }
    }
    
    /**
     * Load sales trend chart
     */
    private void loadSalesChart() {
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Ventes");
            
            // Get sales for last 7 days
            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                List<Vente> ventes = venteService.getVentesByDateRange(date, date);
                
                String dateLabel = date.format(DateTimeFormatter.ofPattern("dd/MM"));
                series.getData().add(new XYChart.Data<>(dateLabel, ventes.size()));
            }
            
            salesChart.getData().clear();
            salesChart.getData().add(series);
            
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error loading sales chart", e);
        }
    }
    
    /**
     * Load top products chart
     */
    private void loadTopProductsChart() {
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Quantité vendue");
            
            // TODO: Get top 5 products from VenteService or StatistiquesService
            // For now, using placeholder data
            series.getData().add(new XYChart.Data<>("Produit 1", 45));
            series.getData().add(new XYChart.Data<>("Produit 2", 38));
            series.getData().add(new XYChart.Data<>("Produit 3", 32));
            series.getData().add(new XYChart.Data<>("Produit 4", 28));
            series.getData().add(new XYChart.Data<>("Produit 5", 25));
            
            topProductsChart.getData().clear();
            topProductsChart.getData().add(series);
            
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error loading top products chart", e);
        }
    }
    
    /**
     * Load system alerts
     */
    private void loadAlerts() {
        try {
            ObservableList<String> alerts = FXCollections.observableArrayList();
            
            // Low stock alerts
            List<Produit> lowStockProducts = produitService.getLowStockProduits();;
            if (!lowStockProducts.isEmpty()) {
                alerts.add("⚠️ " + lowStockProducts.size() + " produits en rupture de stock");
            }
            
            // TODO: Add expiration alerts from LotService
            // TODO: Add pending transfer alerts from TransfertService
            // TODO: Add pending order alerts from BonCommandeService
            
            if (alerts.isEmpty()) {
                alerts.add("✅ Aucune alerte");
            }
            
            alertsList.setItems(alerts);
            
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error loading alerts", e);
        }
    }
    
    /**
     * Load recent sales
     */
    private void loadRecentSales() {
        try {
            List<Vente> recentVentes = venteService.getRecentVentes(10);
            recentSalesTable.setItems(FXCollections.observableArrayList(recentVentes));
            
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error loading recent sales", e);
        }
    }
    
    /**
     * Handle refresh button click
     */
    @FXML
    private void handleRefresh() {
        logger.info("Refreshing dashboard data");
        loadDashboardData();
    }
    
    /**
     * Handle view all sales button click
     */
    @FXML
    private void handleViewAllSales() {
        logger.info("Navigating to rapport view");
        try {
            // Get the main controller and load rapport view
            // This assumes the dashboard is loaded in MainController's contentArea
            // The button would ideally trigger MainController.handleRapportsView()
            // For now, log the action - full navigation requires MainController reference
            logger.warning("Full navigation requires MainController reference - consider using event bus pattern");
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error navigating to reports", e);
        }
    }
}
