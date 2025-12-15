package org.example.model.entity;

import java.time.LocalDateTime;

public class AuditLog {
    
    private Long id;
    private LocalDateTime dateHeure;
    private Utilisateur utilisateur;
    private TypeAction action;
    private String entite; 
    private Long entiteId; 
    private String description;
    private String valeurAvant; 
    private String valeurApres; 
    private String adresseIP;
    private boolean succes;
    
    public AuditLog() {
        this.dateHeure = LocalDateTime.now();
        this.succes = true;
    }
    
    public AuditLog(Utilisateur utilisateur, TypeAction action, String entite, Long entiteId, String description) {
        this();
        this.utilisateur = utilisateur;
        this.action = action;
        this.entite = entite;
        this.entiteId = entiteId;
        this.description = description;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getDateHeure() {
        return dateHeure;
    }
    
    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public TypeAction getAction() {
        return action;
    }
    
    public void setAction(TypeAction action) {
        this.action = action;
    }
    
    public String getEntite() {
        return entite;
    }
    
    public void setEntite(String entite) {
        this.entite = entite;
    }
    
    public Long getEntiteId() {
        return entiteId;
    }
    
    public void setEntiteId(Long entiteId) {
        this.entiteId = entiteId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getValeurAvant() {
        return valeurAvant;
    }
    
    public void setValeurAvant(String valeurAvant) {
        this.valeurAvant = valeurAvant;
    }
    
    public String getValeurApres() {
        return valeurApres;
    }
    
    public void setValeurApres(String valeurApres) {
        this.valeurApres = valeurApres;
    }
    
    public String getAdresseIP() {
        return adresseIP;
    }
    
    public void setAdresseIP(String adresseIP) {
        this.adresseIP = adresseIP;
    }
    
    public boolean isSucces() {
        return succes;
    }
    
    public void setSucces(boolean succes) {
        this.succes = succes;
    }
    
    @Override
    public String toString() {
        return dateHeure + " - " + utilisateur.getUsername() + " - " + action.getLibelle();
    }
}
