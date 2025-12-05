package org.example.model.entity;

/**
 * TypeRetour - Enum for return types
 */
public enum TypeRetour {
    TOTAL("Retour Total"),
    PARTIEL("Retour Partiel");
    
    private final String libelle;
    
    TypeRetour(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
