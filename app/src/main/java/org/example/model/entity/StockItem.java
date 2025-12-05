package org.example.model.entity;

import java.time.LocalDate;

/**
 * StockItem - Entity representing a stock inventory item snapshot
 * Used for inventory tracking and historical stock levels
 */
public class StockItem {
    
    private Long id;
    private Produit produit;
    private int quantite;
    private LocalDate dateInventaire;
    private String emplacement;
    private String commentaire;
    private Utilisateur inventoriste;
    
    // Constructors
    public StockItem() {
        this.dateInventaire = LocalDate.now();
    }
    
    public StockItem(Produit produit, int quantite, String emplacement) {
        this();
        this.produit = produit;
        this.quantite = quantite;
        this.emplacement = emplacement;
    }
    
    // Business logic
    public int calculerEcart(int quantiteTheorique) {
        return this.quantite - quantiteTheorique;
    }
    
    public double getValeurStock() {
        if (produit != null) {
            return produit.getPrix() * quantite;
        }
        return 0.0;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public int getQuantite() {
        return quantite;
    }
    
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    
    public LocalDate getDateInventaire() {
        return dateInventaire;
    }
    
    public void setDateInventaire(LocalDate dateInventaire) {
        this.dateInventaire = dateInventaire;
    }
    
    public String getEmplacement() {
        return emplacement;
    }
    
    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public Utilisateur getInventoriste() {
        return inventoriste;
    }
    
    public void setInventoriste(Utilisateur inventoriste) {
        this.inventoriste = inventoriste;
    }
}
