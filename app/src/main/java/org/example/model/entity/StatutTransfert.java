package org.example.model.entity;

/**
 * StatutTransfert - Stock transfer status enum
 */
public enum StatutTransfert {
    EN_ATTENTE("En Attente"),
    APPROUVE("Approuvé"),
    EN_TRANSIT("En Transit"),
    RECU("Reçu"),
    REFUSE("Refusé"),
    ANNULE("Annulé");
    
    private final String libelle;
    
    StatutTransfert(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
