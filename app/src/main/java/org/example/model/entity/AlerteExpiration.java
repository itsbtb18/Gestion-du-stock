package org.example.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AlerteExpiration {
    
    private String id;
    private Produit produit;
    private LocalDate dateExpiration;
    private LocalDateTime dateAlerte;
    private long joursRestants;
    private String niveau; 
    private boolean traitee;
    private String message;
    
    public AlerteExpiration() {
        this.dateAlerte = LocalDateTime.now();
        this.traitee = false;
    }
    
    public AlerteExpiration(Produit produit, LocalDate dateExpiration) {
        this();
        this.produit = produit;
        this.dateExpiration = dateExpiration;
        this.joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), dateExpiration);
        this.niveau = determinerNiveau();
        this.message = genererMessage();
    }
    
    private String determinerNiveau() {
        if (joursRestants < 0) {
            return "EXPIRE";
        } else if (joursRestants <= 3) {
            return "CRITIQUE";
        } else if (joursRestants <= 7) {
            return "AVERTISSEMENT";
        } else if (joursRestants <= 30) {
            return "INFO";
        }
        return "NORMAL";
    }
    
    private String genererMessage() {
        if (produit == null || dateExpiration == null) return "";
        
        if (joursRestants < 0) {
            return "PRODUIT EXPIRE: " + produit.getNom() + " - Expiré depuis " + Math.abs(joursRestants) + " jour(s)";
        } else if (joursRestants == 0) {
            return "EXPIRE AUJOURD'HUI: " + produit.getNom();
        } else if (joursRestants == 1) {
            return "EXPIRE DEMAIN: " + produit.getNom();
        } else if (joursRestants <= 7) {
            return "Expire bientôt: " + produit.getNom() + " - Dans " + joursRestants + " jour(s)";
        } else {
            return "Expiration proche: " + produit.getNom() + " - Le " + dateExpiration;
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
    }
    
    public LocalDate getDateExpiration() {
        return dateExpiration;
    }
    
    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
    
    public LocalDateTime getDateAlerte() {
        return dateAlerte;
    }
    
    public void setDateAlerte(LocalDateTime dateAlerte) {
        this.dateAlerte = dateAlerte;
    }
    
    public long getJoursRestants() {
        return joursRestants;
    }
    
    public void setJoursRestants(long joursRestants) {
        this.joursRestants = joursRestants;
    }
    
    public String getNiveau() {
        return niveau;
    }
    
    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }
    
    public boolean isTraitee() {
        return traitee;
    }
    
    public void setTraitee(boolean traitee) {
        this.traitee = traitee;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "AlerteExpiration{" +
                "produit=" + (produit != null ? produit.getCode() : "N/A") +
                ", joursRestants=" + joursRestants +
                ", niveau='" + niveau + '\'' +
                '}';
    }
}
