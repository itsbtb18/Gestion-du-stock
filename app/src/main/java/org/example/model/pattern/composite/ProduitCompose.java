package org.example.model.pattern.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * ProduitCompose - Composite class in Composite pattern
 * Represents a bundle or pack of products (can contain simple products or other bundles)
 */
public class ProduitCompose extends ProduitComponent {
    
    private List<ProduitComponent> produits;
    private double remise; // Discount percentage for the bundle
    
    /**
     * Constructor for ProduitCompose
     */
    public ProduitCompose(String code, String nom, String description, double remise) {
        super(code, nom, description);
        this.produits = new ArrayList<>();
        this.remise = remise;
    }
    
    /**
     * Constructor without discount
     */
    public ProduitCompose(String code, String nom, String description) {
        this(code, nom, description, 0.0);
    }
    
    /**
     * Add a product to this bundle
     */
    @Override
    public void ajouter(ProduitComponent produit) {
        if (produit != null) {
            produits.add(produit);
        }
    }
    
    /**
     * Remove a product from this bundle
     */
    @Override
    public void retirer(ProduitComponent produit) {
        produits.remove(produit);
    }
    
    /**
     * Get a child product at specific index
     */
    @Override
    public ProduitComponent getEnfant(int index) {
        if (index >= 0 && index < produits.size()) {
            return produits.get(index);
        }
        throw new IndexOutOfBoundsException("Index invalide: " + index);
    }
    
    /**
     * Get all child products
     */
    @Override
    public List<ProduitComponent> getEnfants() {
        return new ArrayList<>(produits);
    }
    
    /**
     * Calculate total price of the bundle (sum of all products with discount)
     */
    @Override
    public double getPrix() {
        double total = 0.0;
        for (ProduitComponent produit : produits) {
            total += produit.getPrix();
        }
        // Apply discount
        return total * (1 - remise / 100.0);
    }
    
    /**
     * Get minimum stock quantity among all products in the bundle
     */
    @Override
    public int getQuantiteStock() {
        if (produits.isEmpty()) {
            return 0;
        }
        
        int minStock = Integer.MAX_VALUE;
        for (ProduitComponent produit : produits) {
            int stock = produit.getQuantiteStock();
            if (stock < minStock) {
                minStock = stock;
            }
        }
        return minStock;
    }
    
    /**
     * Display information about this bundle and all its products
     */
    @Override
    public void afficher(String indent) {
        System.out.println(indent + "[Pack/Bundle]");
        System.out.println(indent + "Code: " + code);
        System.out.println(indent + "Nom: " + nom);
        System.out.println(indent + "Description: " + description);
        System.out.println(indent + "Remise: " + remise + "%");
        System.out.println(indent + "Prix total: " + getPrix() + " DH");
        System.out.println(indent + "Stock disponible: " + getQuantiteStock() + " packs");
        System.out.println(indent + "Produits inclus (" + produits.size() + "):");
        
        for (ProduitComponent produit : produits) {
            produit.afficher(indent + "  ");
        }
    }
    
    /**
     * This is a composite, so return true
     */
    @Override
    public boolean isComposite() {
        return true;
    }
    
    /**
     * Get the number of products in this bundle
     */
    public int getNombreProduits() {
        return produits.size();
    }
    
    /**
     * Get discount percentage
     */
    public double getRemise() {
        return remise;
    }
    
    /**
     * Set discount percentage
     */
    public void setRemise(double remise) {
        this.remise = remise;
    }
    
    /**
     * Calculate total price without discount
     */
    public double getPrixSansRemise() {
        double total = 0.0;
        for (ProduitComponent produit : produits) {
            total += produit.getPrix();
        }
        return total;
    }
    
    /**
     * Get the amount saved with the discount
     */
    public double getEconomie() {
        return getPrixSansRemise() - getPrix();
    }
    
    @Override
    public String toString() {
        return "ProduitCompose{" +
                "code='" + code + '\'' +
                ", nom='" + nom + '\'' +
                ", nbProduits=" + produits.size() +
                ", prix=" + getPrix() +
                ", remise=" + remise + "%" +
                '}';
    }
}
