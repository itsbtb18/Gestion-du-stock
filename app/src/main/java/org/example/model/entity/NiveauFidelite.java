package org.example.model.entity;

public enum NiveauFidelite {
    BRONZE("Bronze", 0, 999, 5.0, 0),
    ARGENT("Argent", 1000, 2499, 7.0, 5),
    OR("Or", 2500, 4999, 10.0, 10),
    PLATINE("Platine", 5000, Integer.MAX_VALUE, 15.0, 20);
    
    private final String libelle;
    private final int seuilMin; 
    private final int seuilMax; 
    private final double pourcentageRemise; 
    private final int pointsBonus; 
    
    NiveauFidelite(String libelle, int seuilMin, int seuilMax, double pourcentageRemise, int pointsBonus) {
        this.libelle = libelle;
        this.seuilMin = seuilMin;
        this.seuilMax = seuilMax;
        this.pourcentageRemise = pourcentageRemise;
        this.pointsBonus = pointsBonus;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public int getSeuilMin() {
        return seuilMin;
    }
    
    public int getSeuilMax() {
        return seuilMax;
    }
    
    public double getPourcentageRemise() {
        return pourcentageRemise;
    }
    
    public int getPointsBonus() {
        return pointsBonus;
    }
    
    public static NiveauFidelite getNiveauFromMontant(double montantTotal) {
        if (montantTotal >= PLATINE.seuilMin) return PLATINE;
        if (montantTotal >= OR.seuilMin) return OR;
        if (montantTotal >= ARGENT.seuilMin) return ARGENT;
        return BRONZE;
    }
}
