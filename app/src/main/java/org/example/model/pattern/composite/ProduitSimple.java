package org.example.model.pattern.composite;

import org.example.model.entity.Produit;

public class ProduitSimple extends ProduitComponent {
    
    private Produit produit; 
    
    public ProduitSimple(Produit produit) {
        super(produit.getCode(), produit.getNom(), produit.getDescription());
        this.produit = produit;
    }
    
    @Override
    public double getPrix() {
        return produit.getPrix();
    }
    
    @Override
    public int getQuantiteStock() {
        return produit.getQuantiteStock();
    }
    
    public Produit getProduit() {
        return produit;
    }
    
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
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public String toString() {
        return "ProduitSimple{produit=" + produit.toString() + '}';
    }
}
