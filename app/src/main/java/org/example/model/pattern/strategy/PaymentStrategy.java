package org.example.model.pattern.strategy;

public interface PaymentStrategy {
    
    boolean effectuerPaiement(double montant);
    
    String getNomMethode();
    
    boolean valider();
    
    String getDetailsPaiement();
}
