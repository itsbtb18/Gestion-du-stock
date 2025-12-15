package org.example.model.entity;

public enum TypeClient {
    NORMAL("Normal", 0.0),
    FIDELE("Fidèle", 0.05), 
    VIP("VIP", 0.10), 
    PROFESSIONNEL("Professionnel", 0.15); 
    
    private final String libelle;
    private final double tauxRemise;
    
    TypeClient(String libelle, double tauxRemise) {
        this.libelle = libelle;
        this.tauxRemise = tauxRemise;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public double getTauxRemise() {
        return tauxRemise;
    }
    
    public double calculerRemise(double montant) {
        return montant * tauxRemise;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
