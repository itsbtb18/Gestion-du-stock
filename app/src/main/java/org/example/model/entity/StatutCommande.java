package org.example.model.entity;

/**
 * StatutCommande - Purchase order status enum
 */
public enum StatutCommande {
    BROUILLON("Brouillon"),
    VALIDEE("Validée"),
    ENVOYEE("Envoyée"),
    PARTIELLE("Partiellement Reçue"),
    RECUE("Reçue"),
    ANNULEE("Annulée");
    
    private final String libelle;
    
    StatutCommande(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
