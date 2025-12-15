package org.example.model.pattern.strategy;

public interface RemiseStrategy {
    
    double calculerRemise(double montantOriginal);
    
    double getTauxRemise();
    
    String getDescription();
    
    boolean estApplicable(double montantOriginal);
}
