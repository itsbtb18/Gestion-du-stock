package org.example.model.entity;

public enum PrioriteNotification {
    BASSE("Basse"),
    NORMALE("Normale"),
    HAUTE("Haute"),
    URGENTE("Urgente");
    
    private final String libelle;
    
    PrioriteNotification(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
