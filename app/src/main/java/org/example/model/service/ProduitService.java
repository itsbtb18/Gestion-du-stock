package org.example.model.service;

import org.example.dao.ProduitDAO;
import org.example.model.entity.Produit;
import org.example.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

public class ProduitService {
    
    private static ProduitService instance;
    private final ProduitDAO produitDAO;
    
    private ProduitService() {
        this.produitDAO = new ProduitDAO();
    }
    
    public static synchronized ProduitService getInstance() {
        if (instance == null) {
            instance = new ProduitService();
        }
        return instance;
    }
    
    public Produit createProduit(Produit produit) {
        if (!ValidationUtil.estNonVide(produit.getCode())) {
            throw new IllegalArgumentException("Le code produit est requis");
        }
        
        if (!ValidationUtil.estNonVide(produit.getNom())) {
            throw new IllegalArgumentException("Le nom du produit est requis");
        }
        
        if (!ValidationUtil.estPositif(produit.getPrix())) {
            throw new IllegalArgumentException("Le prix doit être positif");
        }
        
        Optional<Produit> existing = produitDAO.findByCode(produit.getCode());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Un produit avec ce code existe déjà");
        }
        
        return produitDAO.save(produit);
    }
    
    public boolean updateProduit(Produit produit) {
        if (produit.getId() == null) {
            throw new IllegalArgumentException("L'ID du produit est requis");
        }
        return produitDAO.update(produit);
    }
    
    public List<Produit> searchProduits(String query) {
        if (!ValidationUtil.estNonVide(query)) {
            return produitDAO.findAll();
        }
        return produitDAO.search(query);
    }
    
    public List<Produit> getAllProduits() {
        return produitDAO.findAll();
    }
    
    public Optional<Produit> getProduitById(Long id) {
        return produitDAO.findById(id);
    }
    
    public Optional<Produit> getProduitByCode(String code) {
        return produitDAO.findByCode(code);
    }
    
    public List<Produit> getLowStockProduits() {
        return produitDAO.findLowStock();
    }
    
    public List<Produit> getExpiringProduits(int days) {
        return produitDAO.findExpiringSoon(days);
    }
    
    public boolean updateStock(Long produitId, int newQuantity) {
        if (!ValidationUtil.estPositif(newQuantity)) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        return produitDAO.updateStock(produitId, newQuantity);
    }
    
    public boolean deleteProduit(Long id) {
        return produitDAO.delete(id);
    }
    
    public int getProduitCount() {
        return produitDAO.count();
    }
}
