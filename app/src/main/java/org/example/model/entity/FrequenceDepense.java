package org.example.model.entity;

public enum FrequenceDepense {
    QUOTIDIEN("Quotidien"),
    HEBDOMADAIRE("Hebdomadaire"),
    MENSUEL("Mensuel"),
    TRIMESTRIEL("Trimestriel"),
    ANNUEL("Annuel");
    
    private final String libelle;
    
    FrequenceDepense(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
