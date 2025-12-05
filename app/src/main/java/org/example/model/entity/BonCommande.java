package org.example.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * BonCommande - Purchase Order entity
 */
public class BonCommande {
    
    private Long id;
    private String numero;
    private LocalDateTime dateCommande;
    private LocalDate dateLivraisonPrevue;
    private LocalDate dateLivraisonReelle;
    private Fournisseur fournisseur;
    private Utilisateur commandeParUser;
    private StatutCommande statut;
    private Double montantTotal;
    private Double montantPaye;
    private String modePaiement;
    private List<LigneBonCommande> lignes;
    private String commentaire;
    
    public BonCommande() {
        this.dateCommande = LocalDateTime.now();
        this.lignes = new ArrayList<>();
        this.statut = StatutCommande.BROUILLON;
        this.montantPaye = 0.0;
    }
    
    public void calculerMontantTotal() {
        this.montantTotal = lignes.stream()
            .mapToDouble(LigneBonCommande::getMontantLigne)
            .sum();
    }
    
    public void ajouterLigne(LigneBonCommande ligne) {
        this.lignes.add(ligne);
        calculerMontantTotal();
    }
    
    public Double getSolde() {
        return montantTotal - montantPaye;
    }
    
    public boolean estPayeCompletement() {
        return montantPaye >= montantTotal;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }
    
    public LocalDate getDateLivraisonPrevue() {
        return dateLivraisonPrevue;
    }
    
    public void setDateLivraisonPrevue(LocalDate dateLivraisonPrevue) {
        this.dateLivraisonPrevue = dateLivraisonPrevue;
    }
    
    public LocalDate getDateLivraisonReelle() {
        return dateLivraisonReelle;
    }
    
    public void setDateLivraisonReelle(LocalDate dateLivraisonReelle) {
        this.dateLivraisonReelle = dateLivraisonReelle;
    }
    
    public Fournisseur getFournisseur() {
        return fournisseur;
    }
    
    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }
    
    public Utilisateur getCommandeParUser() {
        return commandeParUser;
    }
    
    public void setCommandeParUser(Utilisateur commandeParUser) {
        this.commandeParUser = commandeParUser;
    }
    
    public StatutCommande getStatut() {
        return statut;
    }
    
    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }
    
    public Double getMontantTotal() {
        return montantTotal;
    }
    
    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }
    
    public Double getMontantPaye() {
        return montantPaye;
    }
    
    public void setMontantPaye(Double montantPaye) {
        this.montantPaye = montantPaye;
    }
    
    public String getModePaiement() {
        return modePaiement;
    }
    
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }
    
    public List<LigneBonCommande> getLignes() {
        return lignes;
    }
    
    public void setLignes(List<LigneBonCommande> lignes) {
        this.lignes = lignes;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    @Override
    public String toString() {
        return numero + " - " + fournisseur.getNom();
    }
    
    /**
     * Builder for BonCommande - implements Builder Pattern
     * Usage:
     *   BonCommande bon = new BonCommande.Builder()
     *       .withFournisseur(fournisseur)
     *       .withCommandeParUser(user)
     *       .addLigne(ligne1)
     *       .addLigne(ligne2)
     *       .withDateLivraisonPrevue(date)
     *       .withModePaiement("VIREMENT")
     *       .build();
     */
    public static class Builder {
        private final BonCommande bonCommande;
        
        public Builder() {
            this.bonCommande = new BonCommande();
        }
        
        public Builder withNumero(String numero) {
            bonCommande.numero = numero;
            return this;
        }
        
        public Builder withDateCommande(LocalDateTime dateCommande) {
            bonCommande.dateCommande = dateCommande;
            return this;
        }
        
        public Builder withDateLivraisonPrevue(LocalDate date) {
            bonCommande.dateLivraisonPrevue = date;
            return this;
        }
        
        public Builder withDateLivraisonReelle(LocalDate date) {
            bonCommande.dateLivraisonReelle = date;
            return this;
        }
        
        public Builder withFournisseur(Fournisseur fournisseur) {
            bonCommande.fournisseur = fournisseur;
            return this;
        }
        
        public Builder withCommandeParUser(Utilisateur user) {
            bonCommande.commandeParUser = user;
            return this;
        }
        
        public Builder withStatut(StatutCommande statut) {
            bonCommande.statut = statut;
            return this;
        }
        
        public Builder withLignes(List<LigneBonCommande> lignes) {
            bonCommande.lignes = new ArrayList<>(lignes);
            bonCommande.calculerMontantTotal();
            return this;
        }
        
        public Builder addLigne(LigneBonCommande ligne) {
            bonCommande.lignes.add(ligne);
            return this;
        }
        
        public Builder withModePaiement(String modePaiement) {
            bonCommande.modePaiement = modePaiement;
            return this;
        }
        
        public Builder withMontantPaye(Double montantPaye) {
            bonCommande.montantPaye = montantPaye;
            return this;
        }
        
        public Builder withCommentaire(String commentaire) {
            bonCommande.commentaire = commentaire;
            return this;
        }
        
        /**
         * Build the BonCommande instance
         * Automatically calculates total amount
         */
        public BonCommande build() {
            if (!bonCommande.lignes.isEmpty()) {
                bonCommande.calculerMontantTotal();
            }
            return bonCommande;
        }
        
        /**
         * Build without recalculating (for loading from database)
         */
        public BonCommande buildFromDB() {
            return bonCommande;
        }
    }
}
