package org.example.model.entity;

import java.time.LocalDateTime;

/**
 * AlerteStock - Entity representing a low stock alert
 */
public class AlerteStock {
    
    private String id;
    private Produit produit;
    private int quantiteActuelle;
    private int seuilAlerte;
    private LocalDateTime dateAlerte;
    private String niveau; // CRITIQUE, AVERTISSEMENT, INFO
    private boolean traitee;
    private String message;
    
    public AlerteStock() {
        this.dateAlerte = LocalDateTime.now();
        this.traitee = false;
    }
    
    public AlerteStock(Produit produit, int quantiteActuelle, int seuilAlerte, String niveau) {
        this();
        this.produit = produit;
        this.quantiteActuelle = quantiteActuelle;
        this.seuilAlerte = seuilAlerte;
        this.niveau = niveau;
        this.message = genererMessage();
    }
    
    private String genererMessage() {
        if (produit == null) return "";
        
        if (quantiteActuelle == 0) {
            return "RUPTURE DE STOCK: " + produit.getNom() + " (Code: " + produit.getCode() + ")";
        } else if (quantiteActuelle < seuilAlerte / 2) {
            return "STOCK CRITIQUE: " + produit.getNom() + " - Quantité: " + quantiteActuelle + "/" + seuilAlerte;
        } else {
            return "Stock bas: " + produit.getNom() + " - Quantité: " + quantiteActuelle + "/" + seuilAlerte;
        }
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
    
    public int getQuantiteActuelle() {
        return quantiteActuelle;
    }
    
    public void setQuantiteActuelle(int quantiteActuelle) {
        this.quantiteActuelle = quantiteActuelle;
    }
    
    public int getSeuilAlerte() {
        return seuilAlerte;
    }
    
    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
    
    public LocalDateTime getDateAlerte() {
        return dateAlerte;
    }
    
    public void setDateAlerte(LocalDateTime dateAlerte) {
        this.dateAlerte = dateAlerte;
    }
    
    public String getNiveau() {
        return niveau;
    }
    
    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }
    
    public boolean isTraitee() {
        return traitee;
    }
    
    public void setTraitee(boolean traitee) {
        this.traitee = traitee;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "AlerteStock{" +
                "produit=" + (produit != null ? produit.getCode() : "N/A") +
                ", niveau='" + niveau + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
