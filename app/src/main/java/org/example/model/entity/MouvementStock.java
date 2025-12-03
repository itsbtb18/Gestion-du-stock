package org.example.model.entity;

import java.time.LocalDateTime;

/**
 * MouvementStock - Entity representing stock movements
 */
public class MouvementStock {
    
    private String id;
    private Produit produit;
    private String type; // ENTREE, SORTIE, AJUSTEMENT, RETOUR
    private int quantite;
    private LocalDateTime date;
    private String motif;
    private String utilisateur;
    private int stockAvant;
    private int stockApres;
    
    public MouvementStock() {
        this.date = LocalDateTime.now();
    }
    
    public MouvementStock(Produit produit, String type, int quantite, String motif) {
        this();
        this.produit = produit;
        this.type = type;
        this.quantite = quantite;
        this.motif = motif;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getQuantite() {
        return quantite;
    }
    
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public String getUtilisateur() {
        return utilisateur;
    }
    
    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public int getStockAvant() {
        return stockAvant;
    }
    
    public void setStockAvant(int stockAvant) {
        this.stockAvant = stockAvant;
    }
    
    public int getStockApres() {
        return stockApres;
    }
    
    public void setStockApres(int stockApres) {
        this.stockApres = stockApres;
    }
    
    @Override
    public String toString() {
        return "MouvementStock{" +
                "type='" + type + '\'' +
                ", quantite=" + quantite +
                ", date=" + date +
                '}';
    }
}
