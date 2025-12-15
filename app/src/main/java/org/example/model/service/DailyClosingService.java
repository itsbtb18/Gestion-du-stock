package org.example.model.service;

import org.example.app.AppConfig;
import org.example.dao.VenteDAO;
import org.example.model.entity.*;
import org.example.util.SessionManager;
import org.example.util.LoggerUtil;
import org.example.util.CurrencyUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DailyClosingService {
    
    private static DailyClosingService instance;
    private final VenteDAO venteDAO;
    
    private DailyClosingService() {
        this.venteDAO = new VenteDAO();
    }
    
    public static synchronized DailyClosingService getInstance() {
        if (instance == null) {
            instance = new DailyClosingService();
        }
        return instance;
    }
    
    public ZReport generateZReport(LocalDate date) {
        ZReport report = new ZReport();
        report.setReportDate(date);
        report.setGeneratedAt(LocalDateTime.now());
        report.setGeneratedBy(SessionManager.getInstance().getCurrentUserDisplayName());
        
        try {
            List<Vente> ventes = venteDAO.findByDateRange(date, date);
            
            report.setTotalTransactions(ventes.size());
            report.setGrossSales(ventes.stream()
                .filter(v -> "VALIDEE".equals(v.getStatut()))
                .mapToDouble(Vente::getMontantTotal)
                .sum());
            report.setTotalDiscounts(ventes.stream()
                .filter(v -> "VALIDEE".equals(v.getStatut()))
                .mapToDouble(Vente::getMontantRemise)
                .sum());
            report.setTotalTax(ventes.stream()
                .filter(v -> "VALIDEE".equals(v.getStatut()))
                .mapToDouble(Vente::getMontantTVA)
                .sum());
            report.setNetSales(ventes.stream()
                .filter(v -> "VALIDEE".equals(v.getStatut()))
                .mapToDouble(Vente::getMontantFinal)
                .sum());
            
            report.setCancelledTransactions((int) ventes.stream()
                .filter(v -> "ANNULEE".equals(v.getStatut()))
                .count());
            report.setCancelledAmount(ventes.stream()
                .filter(v -> "ANNULEE".equals(v.getStatut()))
                .mapToDouble(Vente::getMontantTotal)
                .sum());
            
            Map<String, Double> paymentBreakdown = new HashMap<>();
            Map<String, Integer> paymentCounts = new HashMap<>();
            
            for (Vente v : ventes) {
                if ("VALIDEE".equals(v.getStatut())) {
                    String method = v.getModePaiement() != null ? v.getModePaiement() : "Inconnu";
                    paymentBreakdown.merge(method, v.getMontantFinal(), Double::sum);
                    paymentCounts.merge(method, 1, Integer::sum);
                }
            }
            report.setPaymentBreakdown(paymentBreakdown);
            report.setPaymentCounts(paymentCounts);
            
            Map<Integer, Double> hourlyBreakdown = new TreeMap<>();
            Map<Integer, Integer> hourlyCounts = new TreeMap<>();
            
            for (int i = 0; i < 24; i++) {
                hourlyBreakdown.put(i, 0.0);
                hourlyCounts.put(i, 0);
            }
            
            for (Vente v : ventes) {
                if ("VALIDEE".equals(v.getStatut())) {
                    int hour = v.getDateVente().getHour();
                    hourlyBreakdown.merge(hour, v.getMontantFinal(), Double::sum);
                    hourlyCounts.merge(hour, 1, Integer::sum);
                }
            }
            report.setHourlySales(hourlyBreakdown);
            report.setHourlyTransactions(hourlyCounts);
            
            Map<String, Double> productSales = new HashMap<>();
            Map<String, Integer> productQuantities = new HashMap<>();
            
            for (Vente v : ventes) {
                if ("VALIDEE".equals(v.getStatut()) && v.getLignes() != null) {
                    for (LigneVente ligne : v.getLignes()) {
                        String produitNom = ligne.getProduit() != null ? ligne.getProduit().getNom() : "Inconnu";
                        productSales.merge(produitNom, ligne.getSousTotal(), Double::sum);
                        productQuantities.merge(produitNom, ligne.getQuantite(), Integer::sum);
                    }
                }
            }
            
            List<Map.Entry<String, Double>> topProducts = productSales.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
            
            report.setTopProducts(topProducts);
            report.setProductQuantities(productQuantities);
            
            if (report.getTotalTransactions() > 0) {
                report.setAverageTicket(report.getNetSales() / report.getTotalTransactions());
            }
            
            LoggerUtil.logInfo(DailyClosingService.class, 
                "Z-Report generated for " + date + ": " + report.getTotalTransactions() + " transactions, " + 
                CurrencyUtil.format(report.getNetSales()));
                
        } catch (Exception e) {
            LoggerUtil.logError(DailyClosingService.class, "Error generating Z-Report", e);
            report.setError("Erreur lors de la génération du rapport: " + e.getMessage());
        }
        
        return report;
    }
    
    public String generateZReportText(ZReport report) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(AppConfig.DATE_FORMAT);
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(AppConfig.TIME_FORMAT);
        
        String line = "═".repeat(48);
        String thinLine = "─".repeat(48);
        
        sb.append("\n").append(line).append("\n");
        sb.append(centerText("RAPPORT Z - FERMETURE DE CAISSE", 48)).append("\n");
        sb.append(centerText(AppConfig.APP_NAME, 48)).append("\n");
        sb.append(line).append("\n\n");
        
        sb.append(String.format("Date du rapport: %s%n", report.getReportDate().format(dateFormat)));
        sb.append(String.format("Généré le: %s à %s%n", 
            report.getGeneratedAt().format(dateFormat),
            report.getGeneratedAt().format(timeFormat)));
        sb.append(String.format("Opérateur: %s%n", report.getGeneratedBy()));
        sb.append("\n").append(thinLine).append("\n");
        
        sb.append(centerText("RÉSUMÉ DES VENTES", 48)).append("\n");
        sb.append(thinLine).append("\n");
        sb.append(formatLine("Nombre de transactions", String.valueOf(report.getTotalTransactions())));
        sb.append(formatLine("Ventes brutes", CurrencyUtil.format(report.getGrossSales())));
        sb.append(formatLine("Remises accordées", "-" + CurrencyUtil.format(report.getTotalDiscounts())));
        sb.append(formatLine("TVA collectée", CurrencyUtil.format(report.getTotalTax())));
        sb.append(thinLine).append("\n");
        sb.append(formatLine("VENTES NETTES", CurrencyUtil.format(report.getNetSales())));
        sb.append("\n");
        
        sb.append(formatLine("Ticket moyen", CurrencyUtil.format(report.getAverageTicket())));
        sb.append("\n").append(thinLine).append("\n");
        
        if (report.getCancelledTransactions() > 0) {
            sb.append(centerText("ANNULATIONS", 48)).append("\n");
            sb.append(formatLine("Transactions annulées", String.valueOf(report.getCancelledTransactions())));
            sb.append(formatLine("Montant annulé", CurrencyUtil.format(report.getCancelledAmount())));
            sb.append("\n").append(thinLine).append("\n");
        }
        
        sb.append(centerText("VENTILATION PAR MODE DE PAIEMENT", 48)).append("\n");
        sb.append(thinLine).append("\n");
        
        for (Map.Entry<String, Double> entry : report.getPaymentBreakdown().entrySet()) {
            int count = report.getPaymentCounts().getOrDefault(entry.getKey(), 0);
            sb.append(formatLine(
                entry.getKey() + " (" + count + ")", 
                CurrencyUtil.format(entry.getValue())
            ));
        }
        sb.append("\n").append(thinLine).append("\n");
        
        if (report.getTopProducts() != null && !report.getTopProducts().isEmpty()) {
            sb.append(centerText("TOP 10 PRODUITS", 48)).append("\n");
            sb.append(thinLine).append("\n");
            
            int rank = 1;
            for (Map.Entry<String, Double> entry : report.getTopProducts()) {
                int qty = report.getProductQuantities().getOrDefault(entry.getKey(), 0);
                String productName = entry.getKey();
                if (productName.length() > 25) {
                    productName = productName.substring(0, 22) + "...";
                }
                sb.append(String.format("%2d. %-25s x%-4d %10s%n", 
                    rank++, productName, qty, CurrencyUtil.format(entry.getValue())));
            }
            sb.append("\n").append(thinLine).append("\n");
        }
        
        sb.append(centerText("VENTES PAR HEURE", 48)).append("\n");
        sb.append(thinLine).append("\n");
        
        for (Map.Entry<Integer, Double> entry : report.getHourlySales().entrySet()) {
            if (entry.getValue() > 0) {
                int count = report.getHourlyTransactions().getOrDefault(entry.getKey(), 0);
                sb.append(String.format("%02d:00-%02d:00  %3d trans.  %15s%n", 
                    entry.getKey(), entry.getKey() + 1, count, CurrencyUtil.format(entry.getValue())));
            }
        }
        
        sb.append("\n").append(line).append("\n");
        sb.append(centerText("FIN DU RAPPORT Z", 48)).append("\n");
        sb.append(line).append("\n");
        
        return sb.toString();
    }
    
    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }
    
    private String formatLine(String label, String value) {
        int dots = 48 - label.length() - value.length() - 2;
        return label + " " + ".".repeat(Math.max(1, dots)) + " " + value + "\n";
    }
    
    public static class ZReport {
        private LocalDate reportDate;
        private LocalDateTime generatedAt;
        private String generatedBy;
        
        private int totalTransactions;
        private double grossSales;
        private double totalDiscounts;
        private double totalTax;
        private double netSales;
        private double averageTicket;
        
        private int cancelledTransactions;
        private double cancelledAmount;
        
        private Map<String, Double> paymentBreakdown;
        private Map<String, Integer> paymentCounts;
        private Map<Integer, Double> hourlySales;
        private Map<Integer, Integer> hourlyTransactions;
        private List<Map.Entry<String, Double>> topProducts;
        private Map<String, Integer> productQuantities;
        
        private String error;
        
        public LocalDate getReportDate() { return reportDate; }
        public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        public String getGeneratedBy() { return generatedBy; }
        public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
        public int getTotalTransactions() { return totalTransactions; }
        public void setTotalTransactions(int totalTransactions) { this.totalTransactions = totalTransactions; }
        public double getGrossSales() { return grossSales; }
        public void setGrossSales(double grossSales) { this.grossSales = grossSales; }
        public double getTotalDiscounts() { return totalDiscounts; }
        public void setTotalDiscounts(double totalDiscounts) { this.totalDiscounts = totalDiscounts; }
        public double getTotalTax() { return totalTax; }
        public void setTotalTax(double totalTax) { this.totalTax = totalTax; }
        public double getNetSales() { return netSales; }
        public void setNetSales(double netSales) { this.netSales = netSales; }
        public double getAverageTicket() { return averageTicket; }
        public void setAverageTicket(double averageTicket) { this.averageTicket = averageTicket; }
        public int getCancelledTransactions() { return cancelledTransactions; }
        public void setCancelledTransactions(int cancelledTransactions) { this.cancelledTransactions = cancelledTransactions; }
        public double getCancelledAmount() { return cancelledAmount; }
        public void setCancelledAmount(double cancelledAmount) { this.cancelledAmount = cancelledAmount; }
        public Map<String, Double> getPaymentBreakdown() { return paymentBreakdown; }
        public void setPaymentBreakdown(Map<String, Double> paymentBreakdown) { this.paymentBreakdown = paymentBreakdown; }
        public Map<String, Integer> getPaymentCounts() { return paymentCounts; }
        public void setPaymentCounts(Map<String, Integer> paymentCounts) { this.paymentCounts = paymentCounts; }
        public Map<Integer, Double> getHourlySales() { return hourlySales; }
        public void setHourlySales(Map<Integer, Double> hourlySales) { this.hourlySales = hourlySales; }
        public Map<Integer, Integer> getHourlyTransactions() { return hourlyTransactions; }
        public void setHourlyTransactions(Map<Integer, Integer> hourlyTransactions) { this.hourlyTransactions = hourlyTransactions; }
        public List<Map.Entry<String, Double>> getTopProducts() { return topProducts; }
        public void setTopProducts(List<Map.Entry<String, Double>> topProducts) { this.topProducts = topProducts; }
        public Map<String, Integer> getProductQuantities() { return productQuantities; }
        public void setProductQuantities(Map<String, Integer> productQuantities) { this.productQuantities = productQuantities; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public boolean hasError() { return error != null && !error.isEmpty(); }
    }
}
