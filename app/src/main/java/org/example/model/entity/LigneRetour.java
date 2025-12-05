package org.example.model.entity;

/**
 * LigneRetour - Line item for a return
 */
public class LigneRetour {
    
    private Long id;
    private Retour retour;
    private LigneVente ligneVenteOriginale; // Original sale line
    private Produit produit;
    private int quantiteRetournee;
    private int quantiteOriginale;
    private Double prixUnitaire;
    private Double montantLigne;
    private String raisonRetour;
    private boolean produitEndommage;
    private String commentaire;
    
    public LigneRetour() {
    }
    
    public LigneRetour(Produit produit, int quantiteRetournee, Double prixUnitaire) {
        this.produit = produit;
        this.quantiteRetournee = quantiteRetournee;
        this.prixUnitaire = prixUnitaire;
        calculerMontant();
    }
    
    public void calculerMontant() {
        this.montantLigne = quantiteRetournee * prixUnitaire;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Retour getRetour() {
        return retour;
    }
    
    public void setRetour(Retour retour) {
        this.retour = retour;
    }
    
    public LigneVente getLigneVenteOriginale() {
        return ligneVenteOriginale;
    }
    
    public void setLigneVenteOriginale(LigneVente ligneVenteOriginale) {
        this.ligneVenteOriginale = ligneVenteOriginale;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public int getQuantiteRetournee() {
        return quantiteRetournee;
    }
    
    public void setQuantiteRetournee(int quantiteRetournee) {
        this.quantiteRetournee = quantiteRetournee;
        calculerMontant();
    }
    
    public int getQuantiteOriginale() {
        return quantiteOriginale;
    }
    
    public void setQuantiteOriginale(int quantiteOriginale) {
        this.quantiteOriginale = quantiteOriginale;
    }
    
    public Double getPrixUnitaire() {
        return prixUnitaire;
    }
    
    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculerMontant();
    }
    
    public Double getMontantLigne() {
        return montantLigne;
    }
    
    public void setMontantLigne(Double montantLigne) {
        this.montantLigne = montantLigne;
    }
    
    public String getRaisonRetour() {
        return raisonRetour;
    }
    
    public void setRaisonRetour(String raisonRetour) {
        this.raisonRetour = raisonRetour;
    }
    
    public boolean isProduitEndommage() {
        return produitEndommage;
    }
    
    public void setProduitEndommage(boolean produitEndommage) {
        this.produitEndommage = produitEndommage;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
