package org.example.model.pattern.strategy;

/**
 * PaymentStrategy - Strategy interface for different payment methods
 */
public interface PaymentStrategy {
    
    /**
     * Process payment for the given amount
     * @param montant Amount to pay
     * @return true if payment successful, false otherwise
     */
    boolean effectuerPaiement(double montant);
    
    /**
     * Get payment method name
     * @return Payment method name
     */
    String getNomMethode();
    
    /**
     * Validate payment details
     * @return true if details are valid
     */
    boolean valider();
    
    /**
     * Get payment receipt details
     * @return Receipt information
     */
    String getDetailsPaiement();
}
