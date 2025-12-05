package org.example.model.entity;

import java.time.LocalDateTime;

/**
 * Emplacement - Location/warehouse entity for multi-location inventory
 */
public class Emplacement {
    
    private Long id;
    private String code;
    private String nom;
    private TypeEmplacement type; // MAGASIN, ENTREPOT, RESERVE
    private String adresse;
    private String ville;
    private String responsable;
    private String telephone;
    private LocalDateTime dateCreation;
    private boolean actif;
    private String commentaire;
    
    public Emplacement() {
        this.dateCreation = LocalDateTime.now();
        this.actif = true;
    }
    
    // Getters and Setters
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
    
    public TypeEmplacement getType() {
        return type;
    }
    
    public void setType(TypeEmplacement type) {
        this.type = type;
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
    
    public String getResponsable() {
        return responsable;
    }
    
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
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
    
    @Override
    public String toString() {
        return code + " - " + nom;
    }
}
