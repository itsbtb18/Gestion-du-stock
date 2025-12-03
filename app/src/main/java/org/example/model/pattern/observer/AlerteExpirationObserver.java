package org.example.model.pattern.observer;

import org.example.model.entity.Produit;
import org.example.model.entity.AlerteExpiration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * AlerteExpirationObserver - Concrete observer for product expiration alerts
 * Monitors products and generates alerts for expiring items
 */
public class AlerteExpirationObserver implements StockObserver {
    
    private List<AlerteExpiration> alertesExpiration;
    private int joursAvance; // Days in advance to alert
    
    public AlerteExpirationObserver(int joursAvance) {
        this.alertesExpiration = new ArrayList<>();
        this.joursAvance = joursAvance;
    }
    
    public AlerteExpirationObserver() {
        this(30); // Default: 30 days in advance
    }
    
    @Override
    public void onStockChange(Produit produit, int ancienneQuantite, int nouvelleQuantite) {
        // Check expiration when stock changes (especially when added)
        verifierExpiration(produit);
    }
    
    @Override
    public void onStockBas(Produit produit, int quantiteActuelle, int seuil) {
        // When stock is low, also check if product is expiring
        verifierExpiration(produit);
    }
    
    @Override
    public void onRuptureStock(Produit produit) {
        // Remove expiration alerts for out-of-stock products
        alertesExpiration.removeIf(alerte -> 
            alerte.getProduit().getCode().equals(produit.getCode())
        );
    }
    
    /**
     * Check if a product is expiring soon and create alert
     */
    private void verifierExpiration(Produit produit) {
        if (produit.getDateExpiration() == null) {
            return; // No expiration date set
        }
        
        LocalDate today = LocalDate.now();
        LocalDate dateExpiration = produit.getDateExpiration();
        
        // Check if expiring within the advance period
        if (dateExpiration.minusDays(joursAvance).isBefore(today) || 
            dateExpiration.isEqual(today)) {
            
            // Create or update alert
            creerOuMettreAJourAlerte(produit);
        }
    }
    
    /**
     * Create or update expiration alert for a product
     */
    private void creerOuMettreAJourAlerte(Produit produit) {
        // Check if alert already exists
        AlerteExpiration alerteExistante = alertesExpiration.stream()
            .filter(a -> a.getProduit().getCode().equals(produit.getCode()))
            .findFirst()
            .orElse(null);
        
        if (alerteExistante == null) {
            // Create new alert
            AlerteExpiration nouvelleAlerte = new AlerteExpiration(
                produit, 
                produit.getDateExpiration()
            );
            alertesExpiration.add(nouvelleAlerte);
            
            // Log alert creation
            System.out.println("[ALERTE EXPIRATION] " + nouvelleAlerte.getMessage());
        }
    }
    
    /**
     * Scan all products for expiration (manual trigger)
     */
    public void scannerProduits(List<Produit> produits) {
        for (Produit produit : produits) {
            verifierExpiration(produit);
        }
    }
    
    /**
     * Get all expiration alerts
     */
    public List<AlerteExpiration> getAlertesExpiration() {
        return new ArrayList<>(alertesExpiration);
    }
    
    /**
     * Get only critical expiration alerts (expiring within 7 days)
     */
    public List<AlerteExpiration> getAlertesCritiques() {
        return alertesExpiration.stream()
            .filter(a -> a.getJoursRestants() <= 7)
            .toList();
    }
    
    /**
     * Get count of expiration alerts
     */
    public int getNombreAlertes() {
        return alertesExpiration.size();
    }
    
    /**
     * Clear all alerts
     */
    public void effacerAlertes() {
        alertesExpiration.clear();
    }
    
    /**
     * Set days in advance for alerts
     */
    public void setJoursAvance(int joursAvance) {
        this.joursAvance = joursAvance;
    }
    
    public int getJoursAvance() {
        return joursAvance;
    }
}
