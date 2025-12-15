package org.example.model.pattern.strategy;

public class CardPayment implements PaymentStrategy {
    
    private String numeroCarte;
    private String nomTitulaire;
    private String dateExpiration;
    private String typeCarte; 
    
    public CardPayment(String numeroCarte, String nomTitulaire, String dateExpiration) {
        this.numeroCarte = masquerNumeroCarte(numeroCarte);
        this.nomTitulaire = nomTitulaire;
        this.dateExpiration = dateExpiration;
        this.typeCarte = detecterTypeCarte(numeroCarte);
    }
    
    @Override
    public boolean effectuerPaiement(double montant) {
        if (!valider()) {
            System.out.println("Échec: Données de carte invalides");
            return false;
        }
        
        System.out.println("Traitement du paiement par carte...");
        System.out.println("Montant: " + montant + "€");
        System.out.println("Carte: " + typeCarte + " " + numeroCarte);
        
        boolean success = simulerTransactionBancaire(montant);
        
        if (success) {
            System.out.println("Paiement accepté !");
        } else {
            System.out.println("Paiement refusé");
        }
        
        return success;
    }
    
    @Override
    public String getNomMethode() {
        return "Carte Bancaire (" + typeCarte + ")";
    }
    
    @Override
    public boolean valider() {
        
        return numeroCarte != null && !numeroCarte.isEmpty()
            && nomTitulaire != null && !nomTitulaire.isEmpty()
            && dateExpiration != null && !dateExpiration.isEmpty();
    }
    
    @Override
    public String getDetailsPaiement() {
        return String.format(
            "Méthode: Carte Bancaire\n" +
            "Type: %s\n" +
            "Numéro: %s\n" +
            "Titulaire: %s",
            typeCarte, numeroCarte, nomTitulaire
        );
    }
    
    private String masquerNumeroCarte(String numero) {
        if (numero == null || numero.length() < 4) {
            return "****";
        }
        return "**** **** **** " + numero.substring(numero.length() - 4);
    }
    
    private String detecterTypeCarte(String numero) {
        if (numero == null || numero.isEmpty()) {
            return "UNKNOWN";
        }
        
        char premierChiffre = numero.charAt(0);
        switch (premierChiffre) {
            case '4':
                return "VISA";
            case '5':
                return "MASTERCARD";
            case '3':
                return "AMERICAN EXPRESS";
            default:
                return "OTHER";
        }
    }
    
    private boolean simulerTransactionBancaire(double montant) {
        
        return Math.random() > 0.05;
    }
    
    public String getNumeroCarte() {
        return numeroCarte;
    }
    
    public String getNomTitulaire() {
        return nomTitulaire;
    }
    
    public String getTypeCarte() {
        return typeCarte;
    }
}
