package org.example.model.entity;

import java.time.LocalDateTime;

/**
 * TransfertStock - Stock transfer between locations
 */
public class TransfertStock {
    
    private Long id;
    private String numeroTransfert;
    private LocalDateTime dateTransfert;
    private Emplacement emplacementSource;
    private Emplacement emplacementDestination;
    private Produit produit;
    private Lot lot; // Optional, if tracking by lot
    private int quantite;
    private Utilisateur demandePar;
    private Utilisateur validePar;
    private StatutTransfert statut;
    private String motif;
    private String commentaire;
    
    public TransfertStock() {
        this.dateTransfert = LocalDateTime.now();
        this.statut = StatutTransfert.EN_ATTENTE;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumeroTransfert() {
        return numeroTransfert;
    }
    
    public void setNumeroTransfert(String numeroTransfert) {
        this.numeroTransfert = numeroTransfert;
    }
    
    public LocalDateTime getDateTransfert() {
        return dateTransfert;
    }
    
    public void setDateTransfert(LocalDateTime dateTransfert) {
        this.dateTransfert = dateTransfert;
    }
    
    public Emplacement getEmplacementSource() {
        return emplacementSource;
    }
    
    public void setEmplacementSource(Emplacement emplacementSource) {
        this.emplacementSource = emplacementSource;
    }
    
    public Emplacement getEmplacementDestination() {
        return emplacementDestination;
    }
    
    public void setEmplacementDestination(Emplacement emplacementDestination) {
        this.emplacementDestination = emplacementDestination;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public Lot getLot() {
        return lot;
    }
    
    public void setLot(Lot lot) {
        this.lot = lot;
    }
    
    public int getQuantite() {
        return quantite;
    }
    
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
    
    public Utilisateur getDemandePar() {
        return demandePar;
    }
    
    public void setDemandePar(Utilisateur demandePar) {
        this.demandePar = demandePar;
    }
    
    public Utilisateur getValidePar() {
        return validePar;
    }
    
    public void setValidePar(Utilisateur validePar) {
        this.validePar = validePar;
    }
    
    public StatutTransfert getStatut() {
        return statut;
    }
    
    public void setStatut(StatutTransfert statut) {
        this.statut = statut;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    // Alias methods for compatibility
    public Utilisateur getDemandeur() {
        return demandePar;
    }
    
    public Long getValidateurId() {
        return validePar != null ? validePar.getId() : null;
    }
    
    public String getNotes() {
        return commentaire;
    }
    
    @Override
    public String toString() {
        return numeroTransfert + " - " + produit.getNom();
    }
}
