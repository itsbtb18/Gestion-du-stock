package org.example.model.entity;

import java.time.LocalDateTime;

public class Fournisseur {
    
    private Long id;
    private String code;
    private String nom;
    private String contact;
    private String telephone;
    private String email;
    private String adresse;
    private String ville;
    private String pays;
    private String codePostal;
    private String siteWeb;
    private String numeroTVA;
    private String conditions_paiement; 
    private Double notePerforme; 
    private LocalDateTime dateCreation;
    private boolean actif;
    private String commentaire;
    
    public Fournisseur() {
        this.dateCreation = LocalDateTime.now();
        this.actif = true;
        this.notePerforme = 3.0;
    }
    
    public Fournisseur(String code, String nom, String contact, String telephone) {
        this();
        this.code = code;
        this.nom = nom;
        this.contact = contact;
        this.telephone = telephone;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getContact() {
        return contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getVille() {
        return ville;
    }
    
    public void setVille(String ville) {
        this.ville = ville;
    }
    
    public String getPays() {
        return pays;
    }
    
    public void setPays(String pays) {
        this.pays = pays;
    }
    
    public String getCodePostal() {
        return codePostal;
    }
    
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
    
    public String getSiteWeb() {
        return siteWeb;
    }
    
    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }
    
    public String getNumeroTVA() {
        return numeroTVA;
    }
    
    public void setNumeroTVA(String numeroTVA) {
        this.numeroTVA = numeroTVA;
    }
    
    public String getConditionsPaiement() {
        return conditions_paiement;
    }
    
    public void setConditionsPaiement(String conditionsPaiement) {
        this.conditions_paiement = conditionsPaiement;
    }
    
    public Double getNotePerforme() {
        return notePerforme;
    }
    
    public void setNotePerforme(Double notePerforme) {
        this.notePerforme = notePerforme;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
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
    
    public String getNotes() {
        return commentaire;
    }
    
    public void setNotes(String notes) {
        this.commentaire = notes;
    }
    
    @Override
    public String toString() {
        return code + " - " + nom;
    }
}
