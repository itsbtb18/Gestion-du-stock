package org.example.model.pattern.strategy;

/**
 * RemiseStrategy - Strategy interface for different discount strategies
 */
public interface RemiseStrategy {
    
    /**
     * Calculate discount amount
     * @param montantOriginal Original amount before discount
     * @return Discount amount
     */
    double calculerRemise(double montantOriginal);
    
    /**
     * Get discount rate (as percentage)
     * @return Discount rate (0.0 to 1.0)
     */
    double getTauxRemise();
    
    /**
     * Get discount description
     * @return Description of the discount
     */
    String getDescription();
    
    /**
     * Check if discount is applicable
     * @param montantOriginal Original amount
     * @return true if discount can be applied
     */
    boolean estApplicable(double montantOriginal);
}
