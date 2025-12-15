package org.example.model.entity;

public class LigneVente {
    
    private Long id;
    private Vente vente;
    private Produit produit;
    private int quantite;
    private double prixUnitaire;
    private double remise;
    private double sousTotal;
    
    public LigneVente() {
    }
    
    public LigneVente(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getPrix();
        this.remise = 0.0;
        calculerSousTotal();
    }
    
    public LigneVente(Produit produit, int quantite, double remise) {
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getPrix();
        this.remise = remise;
        calculerSousTotal();
    }
    
    public void calculerSousTotal() {
        double montantBrut = prixUnitaire * quantite;
        double montantRemise = montantBrut * (remise / 100.0);
        this.sousTotal = montantBrut - montantRemise;
    }
    
    public void setQuantite(int quantite) {
        this.quantite = quantite;
        calculerSousTotal();
    }
    
    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculerSousTotal();
    }
    
    public void setRemise(double remise) {
        this.remise = remise;
        calculerSousTotal();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Vente getVente() {
        return vente;
    }
    
    public void setVente(Vente vente) {
        this.vente = vente;
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
    
    public double getPrixUnitaire() {
        return prixUnitaire;
    }
    
    public double getRemise() {
        return remise;
    }
    
    public double getSousTotal() {
        return sousTotal;
    }
    
    public void setSousTotal(double sousTotal) {
        this.sousTotal = sousTotal;
    }
}
