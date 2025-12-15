package org.example.model.entity;

public enum StatutRetour {
    EN_COURS("En Cours"),
    APPROUVE("Approuvé"),
    REFUSE("Refusé"),
    COMPLETE("Complété"),
    ANNULE("Annulé");
    
    private final String libelle;
    
    StatutRetour(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
