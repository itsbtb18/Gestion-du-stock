package org.example.model.entity;

public enum TypeNotification {
    INFO("Information"),
    WARNING("Avertissement"),
    ERROR("Erreur"),
    SUCCESS("Succès"),
    ALERTE("Alerte");
    
    private final String libelle;
    
    TypeNotification(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
