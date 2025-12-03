package org.example.model.pattern.observer;

import org.example.model.entity.Produit;

/**
 * StockObserver - Observer interface for stock changes
 * Part of the Observer pattern implementation
 */
public interface StockObserver {
    
    /**
     * Called when stock quantity changes
     * @param produit The product whose stock changed
     * @param ancienneQuantite Previous stock quantity
     * @param nouvelleQuantite New stock quantity
     */
    void onStockChange(Produit produit, int ancienneQuantite, int nouvelleQuantite);
    
    /**
     * Called when stock falls below threshold
     * @param produit The product with low stock
     * @param quantiteActuelle Current stock quantity
     * @param seuil Alert threshold
     */
    void onStockBas(Produit produit, int quantiteActuelle, int seuil);
    
    /**
     * Called when stock reaches zero (out of stock)
     * @param produit The product that is out of stock
     */
    void onRuptureStock(Produit produit);
}
