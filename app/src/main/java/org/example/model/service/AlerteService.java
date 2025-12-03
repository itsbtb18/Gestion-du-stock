package org.example.model.service;

import org.example.model.entity.Produit;
import org.example.model.entity.AlerteStock;
import org.example.model.entity.AlerteExpiration;
import org.example.model.pattern.observer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AlerteService - Service for managing alerts
 * Integrates with Observer pattern for real-time notifications
 */
public class AlerteService implements StockObserver {
    
    private List<AlerteStock> alertesStock;
    private AlerteExpirationObserver alerteExpirationObserver;
    private StockSubject stockSubject;
    
    public AlerteService() {
        this.alertesStock = new ArrayList<>();
        this.alerteExpirationObserver = new AlerteExpirationObserver(30);
        this.stockSubject = new StockSubject();
        
        // Register observers
        stockSubject.attach(this);
        stockSubject.attach(alerteExpirationObserver);
    }
    
    @Override
    public void onStockChange(Produit produit, int ancienneQuantite, int nouvelleQuantite) {
        System.out.println("[ALERTE SERVICE] Stock changé: " + produit.getNom() + 
                          " (" + ancienneQuantite + " -> " + nouvelleQuantite + ")");
    }
    
    @Override
    public void onStockBas(Produit produit, int quantiteActuelle, int seuil) {
        String niveau;
        if (quantiteActuelle == 0) {
            niveau = "CRITIQUE";
        } else if (quantiteActuelle < seuil / 2) {
            niveau = "AVERTISSEMENT";
        } else {
            niveau = "INFO";
        }
        
        AlerteStock alerte = new AlerteStock(produit, quantiteActuelle, seuil, niveau);
        alertesStock.add(alerte);
        
        System.out.println("[ALERTE STOCK] " + alerte.getMessage());
    }
    
    @Override
    public void onRuptureStock(Produit produit) {
        AlerteStock alerte = new AlerteStock(produit, 0, produit.getSeuilAlerte(), "CRITIQUE");
        alertesStock.add(alerte);
        
        System.out.println("[RUPTURE DE STOCK] " + produit.getNom());
    }
    
    /**
     * Notify observers of stock change
     */
    public void notifierChangementStock(Produit produit, int ancienneQuantite, int nouvelleQuantite) {
        stockSubject.notifyStockChange(produit, ancienneQuantite, nouvelleQuantite);
    }
    
    /**
     * Get all stock alerts
     */
    public List<AlerteStock> getAlertesStock() {
        return new ArrayList<>(alertesStock);
    }
    
    /**
     * Get critical stock alerts only
     */
    public List<AlerteStock> getAlertesStockCritiques() {
        return alertesStock.stream()
            .filter(a -> a.getNiveau().equals("CRITIQUE"))
            .collect(Collectors.toList());
    }
    
    /**
     * Get all expiration alerts
     */
    public List<AlerteExpiration> getAlertesExpiration() {
        return alerteExpirationObserver.getAlertesExpiration();
    }
    
    /**
     * Get critical expiration alerts
     */
    public List<AlerteExpiration> getAlertesExpirationCritiques() {
        return alerteExpirationObserver.getAlertesCritiques();
    }
    
    /**
     * Scan products for expiration alerts
     */
    public void scannerExpirations(List<Produit> produits) {
        alerteExpirationObserver.scannerProduits(produits);
    }
    
    /**
     * Get total count of all alerts
     */
    public int getNombreTotalAlertes() {
        return alertesStock.size() + alerteExpirationObserver.getNombreAlertes();
    }
    
    /**
     * Mark alert as processed
     */
    public void marquerTraitee(AlerteStock alerte) {
        alerte.setTraitee(true);
    }
    
    /**
     * Clear all alerts
     */
    public void effacerToutesLesAlertes() {
        alertesStock.clear();
        alerteExpirationObserver.effacerAlertes();
    }
    
    /**
     * Get StockSubject for manual observer registration
     */
    public StockSubject getStockSubject() {
        return stockSubject;
    }
}
