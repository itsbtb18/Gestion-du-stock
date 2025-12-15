package org.example.model.pattern.observer;

import org.example.model.entity.Produit;
import org.example.model.entity.AlerteExpiration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlerteExpirationObserver implements StockObserver {
    
    private List<AlerteExpiration> alertesExpiration;
    private int joursAvance; 
    
    public AlerteExpirationObserver(int joursAvance) {
        this.alertesExpiration = new ArrayList<>();
        this.joursAvance = joursAvance;
    }
    
    public AlerteExpirationObserver() {
        this(30); 
    }
    
    @Override
    public void onStockChange(Produit produit, int ancienneQuantite, int nouvelleQuantite) {
        
        verifierExpiration(produit);
    }
    
    @Override
    public void onStockBas(Produit produit, int quantiteActuelle, int seuil) {
        
        verifierExpiration(produit);
    }
    
    @Override
    public void onRuptureStock(Produit produit) {
        
        alertesExpiration.removeIf(alerte -> 
            alerte.getProduit().getCode().equals(produit.getCode())
        );
    }
    
    private void verifierExpiration(Produit produit) {
        if (produit.getDateExpiration() == null) {
            return; 
        }
        
        LocalDate today = LocalDate.now();
        LocalDate dateExpiration = produit.getDateExpiration();
        
        if (dateExpiration.minusDays(joursAvance).isBefore(today) || 
            dateExpiration.isEqual(today)) {
            
            creerOuMettreAJourAlerte(produit);
        }
    }
    
    private void creerOuMettreAJourAlerte(Produit produit) {
        
        AlerteExpiration alerteExistante = alertesExpiration.stream()
            .filter(a -> a.getProduit().getCode().equals(produit.getCode()))
            .findFirst()
            .orElse(null);
        
        if (alerteExistante == null) {
            
            AlerteExpiration nouvelleAlerte = new AlerteExpiration(
                produit, 
                produit.getDateExpiration()
            );
            alertesExpiration.add(nouvelleAlerte);
            
            System.out.println("[ALERTE EXPIRATION] " + nouvelleAlerte.getMessage());
        }
    }
    
    public void scannerProduits(List<Produit> produits) {
        for (Produit produit : produits) {
            verifierExpiration(produit);
        }
    }
    
    public List<AlerteExpiration> getAlertesExpiration() {
        return new ArrayList<>(alertesExpiration);
    }
    
    public List<AlerteExpiration> getAlertesCritiques() {
        return alertesExpiration.stream()
            .filter(a -> a.getJoursRestants() <= 7)
            .toList();
    }
    
    public int getNombreAlertes() {
        return alertesExpiration.size();
    }
    
    public void effacerAlertes() {
        alertesExpiration.clear();
    }
    
    public void setJoursAvance(int joursAvance) {
        this.joursAvance = joursAvance;
    }
    
    public int getJoursAvance() {
        return joursAvance;
    }
}
