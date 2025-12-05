package org.example.model.service;

import org.example.dao.VenteDAO;
import org.example.dao.ProduitDAO;
import org.example.dao.ClientDAO;
import org.example.dao.MouvementStockDAO;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * StatistiquesService - Business logic for statistics and reports
 * Singleton service for handling statistics operations
 */
public class StatistiquesService {
    
    private static StatistiquesService instance;
    private final VenteDAO venteDAO;
    private final ProduitDAO produitDAO;
    private final ClientDAO clientDAO;
    private final MouvementStockDAO mouvementDAO;
    
    private StatistiquesService() {
        this.venteDAO = new VenteDAO();
        this.produitDAO = new ProduitDAO();
        this.clientDAO = new ClientDAO();
        this.mouvementDAO = new MouvementStockDAO();
    }
    
    public static synchronized StatistiquesService getInstance() {
        if (instance == null) {
            instance = new StatistiquesService();
        }
        return instance;
    }
    
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Today's statistics
        LocalDate today = LocalDate.now();
        double[] todayStats = venteDAO.getStatistics(today, today);
        stats.put("ventesAujourdhui", (int) todayStats[0]);
        stats.put("chiffreAffairesAujourdhui", todayStats[1]);
        stats.put("panierMoyen", todayStats[2]);
        
        // This month statistics
        LocalDate startOfMonth = today.withDayOfMonth(1);
        double[] monthStats = venteDAO.getStatistics(startOfMonth, today);
        stats.put("ventesMois", (int) monthStats[0]);
        stats.put("chiffreAffairesMois", monthStats[1]);
        
        // Product statistics
        stats.put("totalProduits", produitDAO.count());
        stats.put("produitsStockBas", produitDAO.findLowStock().size());
        stats.put("produitsExpireSoon", produitDAO.findExpiringSoon(30).size());
        
        // Client statistics
        stats.put("totalClients", clientDAO.count());
        stats.put("clientsVIP", clientDAO.findVIPClients(1000.0).size());
        
        return stats;
    }
    
    public double[] getSalesStatistics(LocalDate startDate, LocalDate endDate) {
        return venteDAO.getStatistics(startDate, endDate);
    }
    
    public Map<String, Integer> getStockMovementStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> stats = new HashMap<>();
        for (Object[] stat : mouvementDAO.getStatisticsByType(startDate, endDate)) {
            stats.put((String) stat[0], (Integer) stat[1]);
        }
        return stats;
    }
}

