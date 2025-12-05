package org.example.model.entity;

import java.time.LocalDateTime;

/**
 * MouvementStock - Entity representing stock movements
 */
public class MouvementStock {
    
    private Long id;
    private Produit produit;
    private TypeMouvement typeMouvement;
    private int quantite;
    private LocalDateTime dateMouvement;
    private String motif;
    private Long utilisateurId;
    private int stockAvant;
    private int stockApres;
    
    public MouvementStock() {
        this.dateMouvement = LocalDateTime.now();
    }
    
    public MouvementStock(Produit produit, TypeMouvement typeMouvement, int quantite, String motif) {
        this();
        this.produit = produit;
        this.typeMouvement = typeMouvement;
        this.quantite = quantite;
        this.motif = motif;
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
    
    public TypeMouvement getTypeMouvement() {
        return typeMouvement;
    }
    
    public void setTypeMouvement(TypeMouvement typeMouvement) {
        this.typeMouvement = typeMouvement;
    }
    
    public int getQuantite() {
        return quantite;
    }
    
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    
    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }
    
    public void setDateMouvement(LocalDateTime dateMouvement) {
        this.dateMouvement = dateMouvement;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public Long getUtilisateurId() {
        return utilisateurId;
    }
    
    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
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
                "typeMouvement='" + typeMouvement + '\'' +
                ", quantite=" + quantite +
                ", dateMouvement=" + dateMouvement +
                '}';
    }
}
