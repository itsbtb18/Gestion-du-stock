package org.example.model.pattern.composite;

import java.util.ArrayList;
import java.util.List;

public class ProduitCompose extends ProduitComponent {
    
    private List<ProduitComponent> produits;
    private double remise; 
    
    public ProduitCompose(String code, String nom, String description, double remise) {
        super(code, nom, description);
        this.produits = new ArrayList<>();
        this.remise = remise;
    }
    
    public ProduitCompose(String code, String nom, String description) {
        this(code, nom, description, 0.0);
    }
    
    @Override
    public void ajouter(ProduitComponent produit) {
        if (produit != null) {
            produits.add(produit);
        }
    }
    
    @Override
    public void retirer(ProduitComponent produit) {
        produits.remove(produit);
    }
    
    @Override
    public ProduitComponent getEnfant(int index) {
        if (index >= 0 && index < produits.size()) {
            return produits.get(index);
        }
        throw new IndexOutOfBoundsException("Index invalide: " + index);
    }
    
    @Override
    public List<ProduitComponent> getEnfants() {
        return new ArrayList<>(produits);
    }
    
    @Override
    public double getPrix() {
        double total = 0.0;
        for (ProduitComponent produit : produits) {
            total += produit.getPrix();
        }
        
        return total * (1 - remise / 100.0);
    }
    
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
    
    @Override
    public boolean isComposite() {
        return true;
    }
    
    public int getNombreProduits() {
        return produits.size();
    }
    
    public double getRemise() {
        return remise;
    }
    
    public void setRemise(double remise) {
        this.remise = remise;
    }
    
    public double getPrixSansRemise() {
        double total = 0.0;
        for (ProduitComponent produit : produits) {
            total += produit.getPrix();
        }
        return total;
    }
    
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
