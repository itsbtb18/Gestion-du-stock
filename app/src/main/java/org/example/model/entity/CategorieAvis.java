package org.example.model.entity;

public enum CategorieAvis {
    SERVICE("Service"),
    PRODUIT("Produit"),
    PRIX("Prix"),
    PROPRETE("Propreté"),
    ATTENTE("Temps d'Attente"),
    GENERAL("Général");
    
    private final String libelle;
    
    CategorieAvis(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
