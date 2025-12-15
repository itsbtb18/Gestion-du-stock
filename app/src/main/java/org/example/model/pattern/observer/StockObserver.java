package org.example.model.pattern.observer;

import org.example.model.entity.Produit;

public interface StockObserver {
    
    void onStockChange(Produit produit, int ancienneQuantite, int nouvelleQuantite);
    
    void onStockBas(Produit produit, int quantiteActuelle, int seuil);
    
    void onRuptureStock(Produit produit);
}
