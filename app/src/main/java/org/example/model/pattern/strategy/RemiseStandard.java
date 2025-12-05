package org.example.model.pattern.strategy;

/**
 * RemiseStandard - Standard discount strategy (no discount)
 */
public class RemiseStandard implements RemiseStrategy {
    
    @Override
    public double calculerRemise(double montantOriginal) {
        return 0.0; // No discount for standard customers
    }
    
    @Override
    public double getTauxRemise() {
        return 0.0;
    }
    
    @Override
    public String getDescription() {
        return "Tarif normal (aucune remise)";
    }
    
    @Override
    public boolean estApplicable(double montantOriginal) {
        return true; // Always applicable
    }
}
