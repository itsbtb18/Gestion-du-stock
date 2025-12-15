package org.example.model.entity;

public enum StatutCaisse {
    OUVERTE("Ouverte"),
    FERMEE("Fermée"),
    SUSPENDUE("Suspendue");
    
    private final String libelle;
    
    StatutCaisse(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
