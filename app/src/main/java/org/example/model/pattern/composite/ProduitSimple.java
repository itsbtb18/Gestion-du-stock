package org.example.model.pattern.composite;

import org.example.model.entity.Produit;

/**
 * ProduitSimple - Leaf class in Composite pattern
 * Represents a single, individual product
 * Wraps the Produit entity to work with Composite pattern
 */
public class ProduitSimple extends ProduitComponent {
    
    private Produit produit; // Composition: wraps the entity
    
    /**
     * Constructor that wraps a Produit entity
     */
    public ProduitSimple(Produit produit) {
        super(produit.getCode(), produit.getNom(), produit.getDescription());
        this.produit = produit;
    }
    
    /**
     * Get the price of this simple product (delegates to entity)
     */
    @Override
    public double getPrix() {
        return produit.getPrix();
    }
    
    /**
     * Get the stock quantity of this simple product (delegates to entity)
     */
    @Override
    public int getQuantiteStock() {
        return produit.getQuantiteStock();
    }
    
    /**
     * Get the wrapped Produit entity
     */
    public Produit getProduit() {
        return produit;
    }
    
    /**
     * Display information about this product
     */
    @Override
    public void afficher(String indent) {
        System.out.println(indent + "[Produit Simple]");
        System.out.println(indent + "Code: " + produit.getCode());
        System.out.println(indent + "Nom: " + produit.getNom());
        System.out.println(indent + "Description: " + produit.getDescription());
        System.out.println(indent + "Prix: " + produit.getPrix() + " DH");
        System.out.println(indent + "Stock: " + produit.getQuantiteStock() + " " + produit.getUnite());
        if (produit.getCategorie() != null) {
            System.out.println(indent + "Catégorie: " + produit.getCategorie().getNom());
        }
    }
    
    /**
     * This is not a composite, so return false
     */
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public String toString() {
        return "ProduitSimple{produit=" + produit.toString() + '}';
    }
}
