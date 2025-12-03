package org.example.model.pattern.observer;

import org.example.model.entity.Produit;
import java.util.ArrayList;
import java.util.List;

/**
 * StockSubject - Subject/Observable for stock changes
 * Manages observers and notifies them of stock changes
 */
public class StockSubject {
    
    private List<StockObserver> observers;
    
    public StockSubject() {
        this.observers = new ArrayList<>();
    }
    
    /**
     * Attach an observer to this subject
     */
    public void attach(StockObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Detach an observer from this subject
     */
    public void detach(StockObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notify all observers of stock change
     */
    public void notifyStockChange(Produit produit, int ancienneQuantite, int nouvelleQuantite) {
        for (StockObserver observer : observers) {
            observer.onStockChange(produit, ancienneQuantite, nouvelleQuantite);
        }
        
        // Check for low stock or rupture
        if (nouvelleQuantite == 0) {
            notifyRuptureStock(produit);
        } else if (nouvelleQuantite <= produit.getSeuilAlerte()) {
            notifyStockBas(produit, nouvelleQuantite, produit.getSeuilAlerte());
        }
    }
    
    /**
     * Notify all observers of low stock
     */
    public void notifyStockBas(Produit produit, int quantiteActuelle, int seuil) {
        for (StockObserver observer : observers) {
            observer.onStockBas(produit, quantiteActuelle, seuil);
        }
    }
    
    /**
     * Notify all observers of stock rupture (out of stock)
     */
    public void notifyRuptureStock(Produit produit) {
        for (StockObserver observer : observers) {
            observer.onRuptureStock(produit);
        }
    }
    
    /**
     * Get the number of attached observers
     */
    public int getObserverCount() {
        return observers.size();
    }
    
    /**
     * Remove all observers
     */
    public void clearObservers() {
        observers.clear();
    }
}
