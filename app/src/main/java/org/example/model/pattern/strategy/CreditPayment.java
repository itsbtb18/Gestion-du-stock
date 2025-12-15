package org.example.model.pattern.strategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreditPayment implements PaymentStrategy {
    
    private Long clientId;
    private String clientNom;
    private double montantCredit;
    private double montantUtilise;
    private double soldeRestant;
    private LocalDateTime dateUtilisation;
    private String numeroAvoir;
    private boolean valide;
    
    public CreditPayment(Long clientId, double montantCredit) {
        this.clientId = clientId;
        this.montantCredit = montantCredit;
        this.soldeRestant = montantCredit;
        this.dateUtilisation = LocalDateTime.now();
        this.numeroAvoir = generateNumeroAvoir();
        this.valide = false;
    }
    
    public CreditPayment(Long clientId, String clientNom, double montantCredit) {
        this(clientId, montantCredit);
        this.clientNom = clientNom;
    }
    
    @Override
    public boolean effectuerPaiement(double montant) {
        if (!valider()) {
            System.out.println("Échec: Données d'avoir invalides");
            return false;
        }
        
        if (montantCredit < montant) {
            System.out.println("Échec: Crédit insuffisant");
            System.out.println("Crédit disponible: " + montantCredit + " DZD");
            System.out.println("Montant requis: " + montant + " DZD");
            return false;
        }
        
        System.out.println("Traitement du paiement par avoir...");
        System.out.println("Client ID: " + clientId);
        System.out.println("Crédit disponible: " + montantCredit + " DZD");
        System.out.println("Montant à payer: " + montant + " DZD");
        
        this.montantUtilise = montant;
        this.soldeRestant = montantCredit - montant;
        this.valide = true;
        
        System.out.println("Paiement par avoir accepté !");
        System.out.println("Solde restant: " + soldeRestant + " DZD");
        
        return true;
    }
    
    @Override
    public String getNomMethode() {
        return "Avoir Client";
    }
    
    @Override
    public boolean valider() {
        
        if (clientId == null || clientId <= 0) {
            return false;
        }
        
        if (montantCredit <= 0) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getDetailsPaiement() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format(
            "Méthode: Avoir Client\n" +
            "N° Avoir: %s\n" +
            "Client: %s (ID: %d)\n" +
            "Crédit initial: %.2f DZD\n" +
            "Montant utilisé: %.2f DZD\n" +
            "Solde restant: %.2f DZD\n" +
            "Date: %s",
            numeroAvoir,
            clientNom != null ? clientNom : "Non spécifié",
            clientId,
            montantCredit,
            montantUtilise,
            soldeRestant,
            dateUtilisation.format(formatter)
        );
    }
    
    private String generateNumeroAvoir() {
        return "AV-" + System.currentTimeMillis();
    }
    
    public boolean canMakePartialPayment(double montant) {
        return montantCredit > 0;
    }
    
    public double getCoverableAmount(double montant) {
        return Math.min(montantCredit, montant);
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public String getClientNom() {
        return clientNom;
    }
    
    public double getMontantCredit() {
        return montantCredit;
    }
    
    public double getMontantUtilise() {
        return montantUtilise;
    }
    
    public double getSoldeRestant() {
        return soldeRestant;
    }
    
    public LocalDateTime getDateUtilisation() {
        return dateUtilisation;
    }
    
    public String getNumeroAvoir() {
        return numeroAvoir;
    }
    
    public boolean isValide() {
        return valide;
    }
    
    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }
}
