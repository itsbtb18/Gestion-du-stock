package org.example.model.entity;

import java.time.LocalDate;

public class Depense {
    
    private Long id;
    private String numero;
    private LocalDate dateDepense;
    private CategorieDepense categorie;
    private String description;
    private Double montant;
    private String modePaiement;
    private Fournisseur fournisseur;
    private Utilisateur saisiParUser;
    private String numeroFacture;
    private boolean recurrente; 
    private FrequenceDepense frequence; 
    private String justificatif; 
    private String commentaire;
    
    public Depense() {
        this.dateDepense = LocalDate.now();
        this.recurrente = false;
    }
    
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
    
    public LocalDate getDateDepense() {
        return dateDepense;
    }
    
    public void setDateDepense(LocalDate dateDepense) {
        this.dateDepense = dateDepense;
    }
    
    public CategorieDepense getCategorie() {
        return categorie;
    }
    
    public void setCategorie(CategorieDepense categorie) {
        this.categorie = categorie;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getMontant() {
        return montant;
    }
    
    public void setMontant(Double montant) {
        this.montant = montant;
    }
    
    public String getModePaiement() {
        return modePaiement;
    }
    
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }
    
    public Fournisseur getFournisseur() {
        return fournisseur;
    }
    
    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }
    
    public Utilisateur getSaisiParUser() {
        return saisiParUser;
    }
    
    public void setSaisiParUser(Utilisateur saisiParUser) {
        this.saisiParUser = saisiParUser;
    }
    
    public String getNumeroFacture() {
        return numeroFacture;
    }
    
    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }
    
    public boolean isRecurrente() {
        return recurrente;
    }
    
    public boolean isRecurrent() {
        return recurrente;
    }
    
    public void setRecurrente(boolean recurrente) {
        this.recurrente = recurrente;
    }
    
    public void setRecurrent(boolean recurrent) {
        this.recurrente = recurrent;
    }
    
    public FrequenceDepense getFrequence() {
        return frequence;
    }
    
    public void setFrequence(FrequenceDepense frequence) {
        this.frequence = frequence;
    }
    
    public String getJustificatif() {
        return justificatif;
    }
    
    public void setJustificatif(String justificatif) {
        this.justificatif = justificatif;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public String getBeneficiaire() {
        return fournisseur != null ? fournisseur.getNom() : "";
    }
    
    public void setBeneficiaire(String beneficiaire) {
        
    }
    
    public String getNotes() {
        return commentaire;
    }
    
    public void setNotes(String notes) {
        this.commentaire = notes;
    }
    
    @Override
    public String toString() {
        return numero + " - " + description + " (" + montant + "€)";
    }
}
