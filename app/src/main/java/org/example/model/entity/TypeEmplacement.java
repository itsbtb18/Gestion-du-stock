package org.example.model.entity;

public enum TypeEmplacement {
    MAGASIN("Magasin"),
    ENTREPOT("Entrepôt"),
    RESERVE("Réserve"),
    SHOWROOM("Showroom");
    
    private final String libelle;
    
    TypeEmplacement(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
