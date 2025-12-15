package org.example.model.pattern.strategy;

public class CashPayment implements PaymentStrategy {
    
    private double montantRecu;
    private double montantAPayer;
    private double monnaieRendue;
    
    public CashPayment(double montantRecu) {
        this.montantRecu = montantRecu;
    }
    
    @Override
    public boolean effectuerPaiement(double montant) {
        this.montantAPayer = montant;
        
        if (montantRecu >= montant) {
            this.monnaieRendue = montantRecu - montant;
            System.out.println("Paiement en espèces accepté: " + montant + "€");
            if (monnaieRendue > 0) {
                System.out.println("Monnaie à rendre: " + monnaieRendue + "€");
            }
            return true;
        }
        
        System.out.println("Montant insuffisant. Manque: " + (montant - montantRecu) + "€");
        return false;
    }
    
    @Override
    public String getNomMethode() {
        return "Espèces";
    }
    
    @Override
    public boolean valider() {
        return montantRecu > 0;
    }
    
    @Override
    public String getDetailsPaiement() {
        return String.format(
            "Méthode: Espèces\n" +
            "Montant à payer: %.2f DZD\n" +
            "Montant reçu: %.2f DZD\n" +
            "Monnaie rendue: %.2f DZD",
            montantAPayer, montantRecu, monnaieRendue
        );
    }
    
    public double getMontantRecu() {
        return montantRecu;
    }
    
    public double getMontantAPayer() {
        return montantAPayer;
    }
    
    public double getMonnaieRendue() {
        return monnaieRendue;
    }
}
