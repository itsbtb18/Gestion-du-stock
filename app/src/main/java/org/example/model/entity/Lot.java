package org.example.model.entity;

import java.time.LocalDate;

/**
 * Lot - Batch/Lot tracking for products
 */
public class Lot {
    
    private Long id;
    private String numeroLot;
    private Produit produit;
    private int quantite;
    private LocalDate dateFabrication;
    private LocalDate dateExpiration;
    private Fournisseur fournisseur;
    private BonCommande bonCommande;
    private String emplacement;
    private boolean actif;
    private String commentaire;
    
    public Lot() {
        this.actif = true;
    }
    
    public boolean estPerime() {
        return dateExpiration != null && dateExpiration.isBefore(LocalDate.now());
    }
    
    public boolean expireBientot(int joursAvance) {
        return dateExpiration != null && 
               dateExpiration.isBefore(LocalDate.now().plusDays(joursAvance));
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumeroLot() {
        return numeroLot;
    }
    
    public void setNumeroLot(String numeroLot) {
        this.numeroLot = numeroLot;
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
    
    public LocalDate getDateFabrication() {
        return dateFabrication;
    }
    
    public void setDateFabrication(LocalDate dateFabrication) {
        this.dateFabrication = dateFabrication;
    }
    
    public LocalDate getDateExpiration() {
        return dateExpiration;
    }
    
    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
    
    public Fournisseur getFournisseur() {
        return fournisseur;
    }
    
    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }
    
    public BonCommande getBonCommande() {
        return bonCommande;
    }
    
    public void setBonCommande(BonCommande bonCommande) {
        this.bonCommande = bonCommande;
    }
    
    public String getEmplacement() {
        return emplacement;
    }
    
    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
    
    public boolean isActif() {
        return actif;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    @Override
    public String toString() {
        return numeroLot + " - " + produit.getNom();
    }
}
