package org.example.model.entity;

public class LigneBonCommande {
    
    private Long id;
    private BonCommande bonCommande;
    private Produit produit;
    private int quantiteCommandee;
    private int quantiteRecue;
    private Double prixUnitaire;
    private Double montantLigne;
    private String commentaire;
    
    public LigneBonCommande() {
        this.quantiteRecue = 0;
    }
    
    public LigneBonCommande(Produit produit, int quantiteCommandee, Double prixUnitaire) {
        this();
        this.produit = produit;
        this.quantiteCommandee = quantiteCommandee;
        this.prixUnitaire = prixUnitaire;
        calculerMontant();
    }
    
    public void calculerMontant() {
        this.montantLigne = quantiteCommandee * prixUnitaire;
    }
    
    public boolean estCompletementRecu() {
        return quantiteRecue >= quantiteCommandee;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BonCommande getBonCommande() {
        return bonCommande;
    }
    
    public void setBonCommande(BonCommande bonCommande) {
        this.bonCommande = bonCommande;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public int getQuantiteCommandee() {
        return quantiteCommandee;
    }
    
    public void setQuantiteCommandee(int quantiteCommandee) {
        this.quantiteCommandee = quantiteCommandee;
        calculerMontant();
    }
    
    public int getQuantiteRecue() {
        return quantiteRecue;
    }
    
    public void setQuantiteRecue(int quantiteRecue) {
        this.quantiteRecue = quantiteRecue;
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
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
