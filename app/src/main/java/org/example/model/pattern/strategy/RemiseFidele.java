package org.example.model.pattern.strategy;

public class RemiseFidele implements RemiseStrategy {
    
    private static final double TAUX_REMISE_BASE = 0.05; 
    private static final double TAUX_REMISE_VIP = 0.10; 
    private static final double SEUIL_VIP = 1000.0; 
    
    private double totalAchatsClient;
    
    public RemiseFidele(double totalAchatsClient) {
        this.totalAchatsClient = totalAchatsClient;
    }
    
    @Override
    public double calculerRemise(double montantOriginal) {
        return montantOriginal * getTauxRemise();
    }
    
    @Override
    public double getTauxRemise() {
        
        if (totalAchatsClient >= SEUIL_VIP) {
            return TAUX_REMISE_VIP;
        }
        return TAUX_REMISE_BASE;
    }
    
    @Override
    public String getDescription() {
        if (totalAchatsClient >= SEUIL_VIP) {
            return "Remise client VIP (10%)";
        }
        return "Remise client fidèle (5%)";
    }
    
    @Override
    public boolean estApplicable(double montantOriginal) {
        return montantOriginal > 0; 
    }
    
    public double getTotalAchatsClient() {
        return totalAchatsClient;
    }
    
    public boolean isVIP() {
        return totalAchatsClient >= SEUIL_VIP;
    }
}
