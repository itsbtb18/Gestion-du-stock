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

public class DashboardController implements Initializable {
    
    private static final Logger logger = LoggerUtil.getLogger(DashboardController.class);
    
    @FXML private ComboBox<String> periodCombo;
    @FXML private Button refreshButton;
    
    @FXML private Label lblTodaySales;
    @FXML private Label lblSalesChange;
    @FXML private Label lblTodayRevenue;
    @FXML private Label lblRevenueChange;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblProductsChange;
    @FXML private Label lblLowStockAlert;
    @FXML private Label lblTotalCustomers;
    @FXML private Label lblNewCustomers;
    
    @FXML private LineChart<String, Number> salesChart;
    @FXML private CategoryAxis salesXAxis;
    @FXML private NumberAxis salesYAxis;
    
    @FXML private BarChart<String, Number> topProductsChart;
    @FXML private CategoryAxis productsXAxis;
    @FXML private NumberAxis productsYAxis;
    
    @FXML private ListView<String> alertsList;
    @FXML private TableView<Vente> recentSalesTable;
    @FXML private TableColumn<Vente, String> colSaleNumber;
    @FXML private TableColumn<Vente, String> colSaleTime;
    @FXML private TableColumn<Vente, String> colSaleCustomer;
    @FXML private TableColumn<Vente, String> colSaleAmount;
    
    private final VenteService venteService = VenteService.getInstance();
    private final ProduitService produitService = ProduitService.getInstance();
    private final ClientService clientService = ClientService.getInstance();
    private final StockService stockService = StockService.getInstance();
    private final StatistiquesService statistiquesService = StatistiquesService.getInstance();
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        periodCombo.setItems(FXCollections.observableArrayList(
            "Aujourd'hui",
            "Cette semaine",
            "Ce mois",
            "Cette année",
            "Personnalisé"
        ));
        
        periodCombo.setValue("Aujourd'hui");
        periodCombo.setOnAction(event -> handlePeriodChange());
        
        setupRecentSalesTable();
        
        setDateRange("Aujourd'hui");
        loadDashboardData();
        
        logger.info("Dashboard initialized successfully");
    }
    
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
            new SimpleStringProperty(String.format("%.2f %s", cellData.getValue().getMontantFinal(), org.example.app.AppConfig.CURRENCY_CODE)));

        recentSalesTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }
    
    @FXML
    private void handlePeriodChange() {
        String period = periodCombo.getValue();
        setDateRange(period);
        loadDashboardData();
    }
    
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
                
                showCustomDateRangeDialog();
                break;
            default:
                startDate = today;
                endDate = today;
        }
    }
    
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
    
    private void loadKPIs() {
        try {
            
            List<Vente> ventes = venteService.getVentesByDateRange(startDate, endDate);
            lblTodaySales.setText(String.valueOf(ventes.size()));
            
            int periodDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
            LocalDate prevStart = startDate.minusDays(periodDays);
            LocalDate prevEnd = startDate.minusDays(1);
            List<Vente> prevVentes = venteService.getVentesByDateRange(prevStart, prevEnd);
            
            if (prevVentes.size() > 0) {
                double changePercent = ((double)(ventes.size() - prevVentes.size()) / prevVentes.size()) * 100;
                String changeText = String.format("%+.0f%% vs période précédente", changePercent);
                lblSalesChange.setText(changeText);
                if (changePercent >= 0) {
                    lblSalesChange.setStyle("-fx-text-fill: #27ae60;");
                } else {
                    lblSalesChange.setStyle("-fx-text-fill: #e74c3c;");
                }
            } else {
                lblSalesChange.setText("Pas de données précédentes");
            }
            
            double totalRevenue = ventes.stream()
                .mapToDouble(v -> v.getMontantFinal())
                .sum();
            lblTodayRevenue.setText(String.format("%.2f DZD", totalRevenue));
            
            double prevRevenue = prevVentes.stream()
                .mapToDouble(v -> v.getMontantFinal())
                .sum();
            if (prevRevenue > 0) {
                double revenueChangePercent = ((totalRevenue - prevRevenue) / prevRevenue) * 100;
                lblRevenueChange.setText(String.format("%+.0f%% vs période précédente", revenueChangePercent));
                if (revenueChangePercent >= 0) {
                    lblRevenueChange.setStyle("-fx-text-fill: #27ae60;");
                } else {
                    lblRevenueChange.setStyle("-fx-text-fill: #e74c3c;");
                }
            }
            
            int totalProducts = produitService.getProduitCount();
            lblTotalProducts.setText(String.valueOf(totalProducts));
            
            List<Produit> lowStock = produitService.getLowStockProduits();
            lblLowStockAlert.setText(String.valueOf(lowStock.size()));
            if (lowStock.size() > 0) {
                lblProductsChange.setStyle("-fx-text-fill: #e74c3c;");
            }
            
            int totalClients = clientService.getClientCount();
            lblTotalCustomers.setText(String.valueOf(totalClients));
            
            lblNewCustomers.setText("Ce mois");
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error loading KPIs", e);
        }
    }
    
    private void loadSalesChart() {
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Ventes");
            
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
    
    private void loadTopProductsChart() {
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Quantité vendue");
            
            Map<String, Integer> topProductsMap = statistiquesService.getTopProductsSold(startDate, endDate, 5);
            
            if (topProductsMap.isEmpty()) {
                
                List<Produit> lowStockProducts = produitService.getLowStockProduits();
                if (lowStockProducts.isEmpty()) {
                    lowStockProducts = produitService.getAllProduits().stream().limit(5).toList();
                }
                for (Produit p : lowStockProducts.stream().limit(5).toList()) {
                    series.getData().add(new XYChart.Data<>(
                        truncateName(p.getNom(), 15), 
                        Math.max(1, 50 - p.getQuantiteStock())
                    ));
                }
            } else {
                topProductsMap.forEach((produitNom, quantite) -> {
                    series.getData().add(new XYChart.Data<>(truncateName(produitNom, 15), quantite));
                });
            }
            
            topProductsChart.getData().clear();
            topProductsChart.getData().add(series);
            
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error loading top products chart", e);
        }
    }
    
    private String truncateName(String name, int maxLen) {
        if (name == null) return "";
        return name.length() > maxLen ? name.substring(0, maxLen) + "..." : name;
    }
    
    private void loadAlerts() {
        try {
            ObservableList<String> alerts = FXCollections.observableArrayList();
            
            List<Produit> lowStockProducts = produitService.getLowStockProduits();
            if (!lowStockProducts.isEmpty()) {
                alerts.add("⚠️ " + lowStockProducts.size() + " produits en rupture de stock");
                
                lowStockProducts.stream().limit(3).forEach(p -> 
                    alerts.add("   • " + p.getNom() + " (Stock: " + p.getQuantiteStock() + ")")
                );
            }
            
            try {
                List<Produit> allProducts = produitService.getAllProduits();
                long expiringCount = allProducts.stream()
                    .filter(p -> p.getDateExpiration() != null)
                    .filter(p -> p.getDateExpiration().isBefore(LocalDate.now().plusDays(30)))
                    .count();
                if (expiringCount > 0) {
                    alerts.add("📅 " + expiringCount + " produit(s) expire(nt) dans les 30 jours");
                }
            } catch (Exception ignored) {
                
            }
            
            try {
                List<org.example.model.entity.TransfertStock> pendingTransfers = 
                    stockService.getPendingTransferts();
                if (pendingTransfers != null && !pendingTransfers.isEmpty()) {
                    alerts.add("🔄 " + pendingTransfers.size() + " transfert(s) en attente");
                }
            } catch (Exception ignored) {
                
            }
            
            if (alerts.isEmpty()) {
                alerts.add("✅ Aucune alerte");
            }
            
            alertsList.setItems(alerts);
            
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error loading alerts", e);
        }
    }
    
    private void loadRecentSales() {
        try {
            List<Vente> recentVentes = venteService.getRecentVentes(10);
            recentSalesTable.setItems(FXCollections.observableArrayList(recentVentes));
            
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error loading recent sales", e);
        }
    }
    
    @FXML
    private void handleRefresh() {
        logger.info("Refreshing dashboard data");
        loadDashboardData();
    }
    
    @FXML
    private void handleViewAllSales() {
        logger.info("Navigating to rapport view");
        try {
            
            logger.warning("Full navigation requires MainController reference - consider using event bus pattern");
        } catch (Exception e) {
            LoggerUtil.logError(DashboardController.class, "Error navigating to reports", e);
        }
    }
    
    private void showCustomDateRangeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Période personnalisée");
        dialog.setHeaderText("Sélectionnez la période de dates");
        
        DatePicker startPicker = new DatePicker(startDate != null ? startDate : LocalDate.now().minusMonths(1));
        DatePicker endPicker = new DatePicker(endDate != null ? endDate : LocalDate.now());
        
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        grid.add(new Label("Date de début:"), 0, 0);
        grid.add(startPicker, 1, 0);
        grid.add(new Label("Date de fin:"), 0, 1);
        grid.add(endPicker, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            startDate = startPicker.getValue();
            endDate = endPicker.getValue();
            
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                
                LocalDate temp = startDate;
                startDate = endDate;
                endDate = temp;
            }
            
            logger.info("Custom date range selected: " + startDate + " to " + endDate);
            loadDashboardData();
        } else {
            
            periodCombo.setValue("Aujourd'hui");
            setDateRange("Aujourd'hui");
        }
    }
}
