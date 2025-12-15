package org.example.model.service;

import org.example.model.entity.Produit;
import org.example.model.entity.AlerteStock;
import org.example.model.entity.AlerteExpiration;
import org.example.model.pattern.observer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlerteService implements StockObserver {
    
    private static AlerteService instance;
    private List<AlerteStock> alertesStock;
    private AlerteExpirationObserver alerteExpirationObserver;
    private StockSubject stockSubject;
    
    private AlerteService() {
        this.alertesStock = new ArrayList<>();
        this.alerteExpirationObserver = new AlerteExpirationObserver(30);
        this.stockSubject = new StockSubject();
        
        stockSubject.attach(this);
        stockSubject.attach(alerteExpirationObserver);
    }
    
    public static synchronized AlerteService getInstance() {
        if (instance == null) {
            instance = new AlerteService();
        }
        return instance;
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
    
    public void notifierChangementStock(Produit produit, int ancienneQuantite, int nouvelleQuantite) {
        stockSubject.notifyStockChange(produit, ancienneQuantite, nouvelleQuantite);
    }
    
    public List<AlerteStock> getAlertesStock() {
        return new ArrayList<>(alertesStock);
    }
    
    public List<AlerteStock> getAlertesStockCritiques() {
        return alertesStock.stream()
            .filter(a -> a.getNiveau().equals("CRITIQUE"))
            .collect(Collectors.toList());
    }
    
    public List<AlerteExpiration> getAlertesExpiration() {
        return alerteExpirationObserver.getAlertesExpiration();
    }
    
    public List<AlerteExpiration> getAlertesExpirationCritiques() {
        return alerteExpirationObserver.getAlertesCritiques();
    }
    
    public void scannerExpirations(List<Produit> produits) {
        alerteExpirationObserver.scannerProduits(produits);
    }
    
    public int getNombreTotalAlertes() {
        return alertesStock.size() + alerteExpirationObserver.getNombreAlertes();
    }
    
    public void marquerTraitee(AlerteStock alerte) {
        alerte.setTraitee(true);
    }
    
    public void effacerToutesLesAlertes() {
        alertesStock.clear();
        alerteExpirationObserver.effacerAlertes();
    }
    
    public StockSubject getStockSubject() {
        return stockSubject;
    }
}
