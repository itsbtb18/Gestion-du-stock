package org.example.model.service;

import org.example.dao.LotDAO;
import org.example.dao.ProduitDAO;
import org.example.model.entity.Lot;
import org.example.model.entity.Produit;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LotService {
    
    private static LotService instance;
    private final LotDAO lotDAO;
    private final ProduitDAO produitDAO;
    
    private LotService() {
        this.lotDAO = new LotDAO();
        this.produitDAO = new ProduitDAO();
    }
    
    public static synchronized LotService getInstance() {
        if (instance == null) {
            instance = new LotService();
        }
        return instance;
    }
    
    public Lot createLot(Lot lot) throws SQLException {
        
        if (lot.getNumeroLot() == null || lot.getNumeroLot().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de lot est requis");
        }
        
        if (lot.getProduit() == null) {
            throw new IllegalArgumentException("Le produit est requis");
        }
        
        if (lot.getQuantite() <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        
        Optional<Produit> produitOpt = produitDAO.findById(lot.getProduit().getId());
        if (produitOpt.isEmpty()) {
            throw new IllegalArgumentException("Produit introuvable");
        }
        
        if (lot.getDateExpiration() != null && lot.getDateExpiration().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date d'expiration ne peut pas être dans le passé");
        }
        
        return lotDAO.save(lot);
    }
    
    public boolean updateQuantite(Long lotId, Integer nouvelleQuantite) throws SQLException {
        if (nouvelleQuantite < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
        
        lotDAO.updateQuantite(lotId, nouvelleQuantite);
        return true;
    }
    
    public boolean consumeFromLot(Long produitId, int quantite) throws SQLException {
        
        List<Lot> lots = lotDAO.findByProduit(produitId);
        
        int remaining = quantite;
        
        for (Lot lot : lots) {
            if (remaining <= 0) break;
            
            if (lot.getQuantite() > 0) {
                int toConsume = Math.min(remaining, lot.getQuantite());
                int newQuantity = lot.getQuantite() - toConsume;
                
                lotDAO.updateQuantite(lot.getId(), newQuantity);
                remaining -= toConsume;
            }
        }
        
        return remaining == 0; 
    }
    
    public List<Lot> getExpiredLots() throws SQLException {
        return lotDAO.findExpires();
    }
    
    public List<Lot> getLotsExpiringSoon(int daysAdvance) throws SQLException {
        return lotDAO.findExpirantBientot(daysAdvance);
    }
    
    public List<Lot> getLotsByProduit(Long produitId) throws SQLException {
        return lotDAO.findByProduit(produitId);
    }
    
    public List<Lot> getLotsByEmplacement(Long emplacementId) throws SQLException {
        return lotDAO.findByEmplacement(String.valueOf(emplacementId));
    }
    
    public List<Lot> getActiveLots() throws SQLException {
        return lotDAO.findActive();
    }
    
    public List<Lot> getAllLots() throws SQLException {
        return lotDAO.findAll();
    }
    
    public Optional<Lot> getLotById(Long id) throws SQLException {
        return lotDAO.findById(id);
    }
    
    public Optional<Lot> getLotByNumero(String numero) throws SQLException {
        return lotDAO.findByNumero(numero);
    }
    
    public boolean deleteLot(Long lotId) throws SQLException {
        lotDAO.delete(lotId);
        return true;
    }
    
    public boolean hasSufficientQuantity(Long produitId, int requiredQuantity) throws SQLException {
        List<Lot> lots = lotDAO.findByProduit(produitId);
        int totalAvailable = lots.stream()
            .mapToInt(Lot::getQuantite)
            .sum();
        return totalAvailable >= requiredQuantity;
    }
}
