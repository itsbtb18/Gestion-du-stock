package org.example.model.entity;

/**
 * TypeAction - Audit log action types
 */
public enum TypeAction {
    CREATION("Création"),
    MODIFICATION("Modification"),
    SUPPRESSION("Suppression"),
    CONSULTATION("Consultation"),
    CONNEXION("Connexion"),
    DECONNEXION("Déconnexion"),
    VALIDATION("Validation"),
    ANNULATION("Annulation"),
    EXPORT("Export"),
    IMPORT("Import");
    
    private final String libelle;
    
    TypeAction(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
