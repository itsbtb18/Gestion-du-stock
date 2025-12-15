package org.example.model.service;

import org.example.dao.MouvementStockDAO;
import org.example.dao.ProduitDAO;
import org.example.model.entity.MouvementStock;
import org.example.model.entity.Produit;
import org.example.model.entity.TypeMouvement;
import org.example.model.entity.TransfertStock;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StockService {
    
    private static StockService instance;
    private final MouvementStockDAO mouvementDAO;
    private final ProduitDAO produitDAO;
    
    private StockService() {
        this.mouvementDAO = new MouvementStockDAO();
        this.produitDAO = new ProduitDAO();
    }
    
    public static synchronized StockService getInstance() {
        if (instance == null) {
            instance = new StockService();
        }
        return instance;
    }
    
    public MouvementStock recordMouvement(MouvementStock mouvement) {
        if (mouvement.getProduit() == null || mouvement.getProduit().getId() == null) {
            throw new IllegalArgumentException("Le produit est requis");
        }
        
        if (mouvement.getTypeMouvement() == null) {
            throw new IllegalArgumentException("Le type de mouvement est requis");
        }
        
        if (mouvement.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        
        Optional<Produit> produitOpt = produitDAO.findById(mouvement.getProduit().getId());
        if (produitOpt.isEmpty()) {
            throw new IllegalArgumentException("Produit non trouvé");
        }
        
        Produit produit = produitOpt.get();
        
        if (mouvement.getTypeMouvement().getCoefficient() < 0) {
            int newStock = produit.getQuantiteStock() + (mouvement.getQuantite() * mouvement.getTypeMouvement().getCoefficient());
            if (newStock < 0) {
                throw new IllegalArgumentException("Stock insuffisant pour cette opération");
            }
        }
        
        if (mouvement.getDateMouvement() == null) {
            mouvement.setDateMouvement(LocalDateTime.now());
        }
        
        mouvement.setStockAvant(produit.getQuantiteStock());
        int stockChange = mouvement.getQuantite() * mouvement.getTypeMouvement().getCoefficient();
        mouvement.setStockApres(produit.getQuantiteStock() + stockChange);
        
        return mouvementDAO.save(mouvement);
    }
    
    public MouvementStock addStock(Long produitId, int quantite, String motif, Long utilisateurId) {
        Optional<Produit> produitOpt = produitDAO.findById(produitId);
        if (produitOpt.isEmpty()) {
            throw new IllegalArgumentException("Produit non trouvé");
        }
        
        MouvementStock mouvement = new MouvementStock();
        mouvement.setProduit(produitOpt.get());
        mouvement.setTypeMouvement(TypeMouvement.ENTREE);
        mouvement.setQuantite(quantite);
        mouvement.setMotif(motif);
        mouvement.setUtilisateurId(utilisateurId);
        
        return recordMouvement(mouvement);
    }
    
    public MouvementStock removeStock(Long produitId, int quantite, String motif, Long utilisateurId) {
        Optional<Produit> produitOpt = produitDAO.findById(produitId);
        if (produitOpt.isEmpty()) {
            throw new IllegalArgumentException("Produit non trouvé");
        }
        
        MouvementStock mouvement = new MouvementStock();
        mouvement.setProduit(produitOpt.get());
        mouvement.setTypeMouvement(TypeMouvement.SORTIE);
        mouvement.setQuantite(quantite);
        mouvement.setMotif(motif);
        mouvement.setUtilisateurId(utilisateurId);
        
        return recordMouvement(mouvement);
    }
    
    public MouvementStock adjustStock(Long produitId, int newQuantite, String motif, Long utilisateurId) {
        Optional<Produit> produitOpt = produitDAO.findById(produitId);
        if (produitOpt.isEmpty()) {
            throw new IllegalArgumentException("Produit non trouvé");
        }
        
        Produit produit = produitOpt.get();
        int difference = newQuantite - produit.getQuantiteStock();
        
        if (difference == 0) {
            throw new IllegalArgumentException("Aucun ajustement nécessaire");
        }
        
        MouvementStock mouvement = new MouvementStock();
        mouvement.setProduit(produit);
        mouvement.setTypeMouvement(TypeMouvement.AJUSTEMENT);
        mouvement.setQuantite(Math.abs(difference));
        mouvement.setMotif(motif);
        mouvement.setUtilisateurId(utilisateurId);
        
        return recordMouvement(mouvement);
    }
    
    public List<MouvementStock> getAllMouvements() {
        return mouvementDAO.findAll();
    }
    
    public List<MouvementStock> getMouvementsByProduit(Long produitId) {
        return mouvementDAO.findByProduit(produitId);
    }
    
    public List<MouvementStock> getMouvementsByDateRange(LocalDate startDate, LocalDate endDate) {
        return mouvementDAO.findByDateRange(startDate, endDate);
    }
    
    public List<MouvementStock> getMouvementsByType(TypeMouvement type) {
        return mouvementDAO.findByType(type);
    }
    
    public List<MouvementStock> getTodayMouvements() {
        return mouvementDAO.findToday();
    }
    
    public List<Object[]> getStatisticsByType(LocalDate startDate, LocalDate endDate) {
        return mouvementDAO.getStatisticsByType(startDate, endDate);
    }
    
    public int getMouvementCount() {
        return mouvementDAO.count();
    }
    
    public List<TransfertStock> getPendingTransferts() {
        try {
            return TransfertService.getInstance().getPendingTransferts();
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }
}
