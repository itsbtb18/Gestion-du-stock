package org.example.model.entity;

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
    IMPORT("Import"),
    
    VENTE("Vente"),
    RETOUR("Retour"),
    REMBOURSEMENT("Remboursement"),
    
    MOUVEMENT_STOCK("Mouvement de stock"),
    AJUSTEMENT_STOCK("Ajustement de stock"),
    TRANSFERT_STOCK("Transfert de stock"),
    
    OUVERTURE_CAISSE("Ouverture de caisse"),
    FERMETURE_CAISSE("Fermeture de caisse"),
    
    PARAMETRE_MODIFIE("Paramètre modifié"),
    
    ERREUR("Erreur"),
    ALERTE("Alerte");
    
    private final String libelle;
    
    TypeAction(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
