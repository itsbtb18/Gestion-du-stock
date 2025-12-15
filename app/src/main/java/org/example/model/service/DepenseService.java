package org.example.model.service;

import org.example.dao.DepenseDAO;
import org.example.model.entity.CategorieDepense;
import org.example.model.entity.Depense;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DepenseService {
    
    private static DepenseService instance;
    private final DepenseDAO depenseDAO;
    
    private DepenseService() {
        this.depenseDAO = new DepenseDAO();
    }
    
    public static synchronized DepenseService getInstance() {
        if (instance == null) {
            instance = new DepenseService();
        }
        return instance;
    }
    
    public Depense createDepense(Depense depense) throws SQLException {
        
        if (depense.getMontant() == null || depense.getMontant() <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        
        if (depense.getCategorie() == null) {
            throw new IllegalArgumentException("La catégorie est requise");
        }
        
        if (depense.getDescription() == null || depense.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La description est requise");
        }
        
        if (depense.getNumero() == null || depense.getNumero().isEmpty()) {
            depense.setNumero(generateDepenseNumero());
        }
        
        if (depense.getDateDepense() == null) {
            depense.setDateDepense(LocalDate.now());
        }
        
        return depenseDAO.save(depense);
    }
    
    public boolean updateDepense(Depense depense) throws SQLException {
        if (depense.getId() == null) {
            throw new IllegalArgumentException("L'ID de la dépense est requis");
        }
        
        Optional<Depense> existing = depenseDAO.findById(depense.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Dépense introuvable");
        }
        
        depenseDAO.update(depense);
        return true;
    }
    
    public boolean deleteDepense(Long depenseId) throws SQLException {
        depenseDAO.delete(depenseId);
        return true;
    }
    
    public Double getTotalByCategorie(CategorieDepense categorie, LocalDate debut, LocalDate fin) throws SQLException {
        return depenseDAO.getTotalByCategorie(categorie, debut, fin);
    }
    
    public Double getTotalByPeriode(LocalDate debut, LocalDate fin) throws SQLException {
        return depenseDAO.getTotalByPeriode(debut, fin);
    }
    
    public Map<CategorieDepense, Double> getExpenseBreakdown(LocalDate debut, LocalDate fin) throws SQLException {
        Map<CategorieDepense, Double> breakdown = new HashMap<>();
        
        for (CategorieDepense categorie : CategorieDepense.values()) {
            Double total = depenseDAO.getTotalByCategorie(categorie, debut, fin);
            if (total != null && total > 0) {
                breakdown.put(categorie, total);
            }
        }
        
        return breakdown;
    }
    
    public Double getMonthlyExpenses(int year, int month) throws SQLException {
        LocalDate debut = LocalDate.of(year, month, 1);
        LocalDate fin = debut.plusMonths(1).minusDays(1);
        return getTotalByPeriode(debut, fin);
    }
    
    public Double getYearlyExpenses(int year) throws SQLException {
        LocalDate debut = LocalDate.of(year, 1, 1);
        LocalDate fin = LocalDate.of(year, 12, 31);
        return getTotalByPeriode(debut, fin);
    }
    
    public List<Depense> getRecurringExpenses() throws SQLException {
        return depenseDAO.findRecurrentes();
    }
    
    public List<Depense> getExpensesByCategorie(CategorieDepense categorie) throws SQLException {
        return depenseDAO.findByCategorie(categorie);
    }
    
    public List<Depense> getDepensesByCategorie(CategorieDepense categorie, LocalDate debut, LocalDate fin) throws SQLException {
        return depenseDAO.findByCategorie(categorie).stream()
            .filter(d -> !d.getDateDepense().isBefore(debut) && !d.getDateDepense().isAfter(fin))
            .toList();
    }
    
    public List<Depense> getExpensesByPeriode(LocalDate debut, LocalDate fin) throws SQLException {
        return depenseDAO.findByPeriode(debut, fin);
    }
    
    public List<Depense> getDepensesByPeriode(LocalDate debut, LocalDate fin) throws SQLException {
        return depenseDAO.findByPeriode(debut, fin);
    }
    
    public List<Depense> getAllExpenses() throws SQLException {
        return depenseDAO.findAll();
    }
    
    public List<Depense> getAllDepenses() throws SQLException {
        return depenseDAO.findAll();
    }
    
    public Optional<Depense> getExpenseById(Long id) throws SQLException {
        return depenseDAO.findById(id);
    }
    
    public Optional<Depense> getExpenseByNumero(String numero) throws SQLException {
        return depenseDAO.findByNumero(numero);
    }
    
    private String generateDepenseNumero() {
        return "DEP-" + System.currentTimeMillis();
    }
    
    public Double getAverageMonthlyExpense(CategorieDepense categorie, int months) throws SQLException {
        LocalDate fin = LocalDate.now();
        LocalDate debut = fin.minusMonths(months);
        
        Double total = getTotalByCategorie(categorie, debut, fin);
        return total != null ? total / months : 0.0;
    }
    
    public List<Map.Entry<CategorieDepense, Double>> getTopCategories(LocalDate debut, LocalDate fin, int limit) throws SQLException {
        Map<CategorieDepense, Double> breakdown = getExpenseBreakdown(debut, fin);
        
        return breakdown.entrySet().stream()
            .sorted(Map.Entry.<CategorieDepense, Double>comparingByValue().reversed())
            .limit(limit)
            .toList();
    }
}
