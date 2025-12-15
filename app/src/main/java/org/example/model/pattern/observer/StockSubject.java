package org.example.model.pattern.observer;

import org.example.model.entity.Produit;
import java.util.ArrayList;
import java.util.List;

public class StockSubject {
    
    private List<StockObserver> observers;
    
    public StockSubject() {
        this.observers = new ArrayList<>();
    }
    
    public void attach(StockObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    public void detach(StockObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyStockChange(Produit produit, int ancienneQuantite, int nouvelleQuantite) {
        for (StockObserver observer : observers) {
            observer.onStockChange(produit, ancienneQuantite, nouvelleQuantite);
        }
        
        if (nouvelleQuantite == 0) {
            notifyRuptureStock(produit);
        } else if (nouvelleQuantite <= produit.getSeuilAlerte()) {
            notifyStockBas(produit, nouvelleQuantite, produit.getSeuilAlerte());
        }
    }
    
    public void notifyStockBas(Produit produit, int quantiteActuelle, int seuil) {
        for (StockObserver observer : observers) {
            observer.onStockBas(produit, quantiteActuelle, seuil);
        }
    }
    
    public void notifyRuptureStock(Produit produit) {
        for (StockObserver observer : observers) {
            observer.onRuptureStock(produit);
        }
    }
    
    public int getObserverCount() {
        return observers.size();
    }
    
    public void clearObservers() {
        observers.clear();
    }
}
