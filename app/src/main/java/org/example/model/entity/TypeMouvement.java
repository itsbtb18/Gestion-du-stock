package org.example.model.entity;

public enum TypeMouvement {
    ENTREE("Entrée", "Ajout de stock", 1),
    SORTIE("Sortie", "Retrait de stock", -1),
    AJUSTEMENT("Ajustement", "Correction de stock", 0),
    RETOUR("Retour", "Retour client", 1),
    INVENTAIRE("Inventaire", "Comptage d'inventaire", 0),
    PERTE("Perte", "Perte ou casse", -1),
    TRANSFERT("Transfert", "Transfert entre emplacements", 0);
    
    private final String libelle;
    private final String description;
    private final int coefficient; 
    
    TypeMouvement(String libelle, String description, int coefficient) {
        this.libelle = libelle;
        this.description = description;
        this.coefficient = coefficient;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getCoefficient() {
        return coefficient;
    }
    
    public boolean isAugmentation() {
        return coefficient > 0;
    }
    
    public boolean isDiminution() {
        return coefficient < 0;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
