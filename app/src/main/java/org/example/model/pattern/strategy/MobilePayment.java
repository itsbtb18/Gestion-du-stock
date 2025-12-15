package org.example.model.pattern.strategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MobilePayment implements PaymentStrategy {
    
    public enum Provider {
        TPE("Terminal de Paiement"),
        DAHABIA("Dahabia (Algérie Poste)"),
        BARIDI_MOB("BaridiMob (CCP)"),
        EDAHABIA("eDahabia"),
        SATIM("SATIM"),
        APPLE_PAY("Apple Pay"),
        GOOGLE_PAY("Google Pay"),
        OTHER("Autre");
        
        private final String displayName;
        
        Provider(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String provider;
    private String transactionId;
    private String phoneNumber;
    private double montantPaye;
    private LocalDateTime dateTransaction;
    private boolean valide;
    
    public MobilePayment(String provider, String transactionId) {
        this.provider = provider;
        this.transactionId = transactionId;
        this.dateTransaction = LocalDateTime.now();
        this.valide = false;
    }
    
    public MobilePayment(String provider, String transactionId, String phoneNumber) {
        this(provider, transactionId);
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public boolean effectuerPaiement(double montant) {
        if (!valider()) {
            System.out.println("Échec: Données de paiement mobile invalides");
            return false;
        }
        
        System.out.println("Traitement du paiement mobile...");
        System.out.println("Fournisseur: " + provider);
        System.out.println("Transaction: " + transactionId);
        System.out.println("Montant: " + montant + " DZD");
        
        boolean verified = verifyTransaction(transactionId, montant);
        
        if (verified) {
            this.montantPaye = montant;
            this.valide = true;
            System.out.println("Paiement mobile accepté !");
            return true;
        } else {
            System.out.println("Échec: Transaction non vérifiée");
            return false;
        }
    }
    
    @Override
    public String getNomMethode() {
        return "Paiement Mobile (" + provider + ")";
    }
    
    @Override
    public boolean valider() {
        
        if (provider == null || provider.trim().isEmpty()) {
            return false;
        }
        
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return false;
        }
        
        if (transactionId.length() < 6) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getDetailsPaiement() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format(
            "Méthode: Paiement Mobile\n" +
            "Fournisseur: %s\n" +
            "ID Transaction: %s\n" +
            "Téléphone: %s\n" +
            "Date: %s\n" +
            "Montant: %.2f DZD",
            provider,
            transactionId,
            phoneNumber != null ? maskPhoneNumber(phoneNumber) : "Non spécifié",
            dateTransaction.format(formatter),
            montantPaye
        );
    }
    
    private boolean verifyTransaction(String transactionId, double montant) {
        
        return transactionId != null && !transactionId.isEmpty();
    }
    
    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "****" + phone.substring(phone.length() - 4);
    }
    
    public String getProvider() {
        return provider;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public double getMontantPaye() {
        return montantPaye;
    }
    
    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }
    
    public boolean isValide() {
        return valide;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
