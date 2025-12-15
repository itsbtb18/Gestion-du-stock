package org.example.model.pattern.strategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankTransferPayment implements PaymentStrategy {
    
    public enum TransferStatus {
        PENDING("En attente"),
        CONFIRMED("Confirmé"),
        REJECTED("Rejeté"),
        CANCELLED("Annulé");
        
        private final String displayName;
        
        TransferStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String numeroReference;
    private String nomBanque;
    private String compteBancaire;
    private String nomTitulaire;
    private double montantPaye;
    private LocalDateTime dateTransfert;
    private TransferStatus status;
    private boolean valide;
    
    public BankTransferPayment(String numeroReference, String nomBanque) {
        this.numeroReference = numeroReference;
        this.nomBanque = nomBanque;
        this.dateTransfert = LocalDateTime.now();
        this.status = TransferStatus.PENDING;
        this.valide = false;
    }
    
    public BankTransferPayment(String numeroReference, String nomBanque, String compteBancaire, String nomTitulaire) {
        this(numeroReference, nomBanque);
        this.compteBancaire = compteBancaire;
        this.nomTitulaire = nomTitulaire;
    }
    
    @Override
    public boolean effectuerPaiement(double montant) {
        if (!valider()) {
            System.out.println("Échec: Données du virement invalides");
            return false;
        }
        
        System.out.println("Traitement du virement bancaire...");
        System.out.println("Référence: " + numeroReference);
        System.out.println("Banque: " + nomBanque);
        System.out.println("Montant: " + montant + " DZD");
        
        this.montantPaye = montant;
        this.status = TransferStatus.CONFIRMED;
        this.valide = true;
        
        System.out.println("Virement bancaire enregistré - En attente de confirmation bancaire");
        return true;
    }
    
    @Override
    public String getNomMethode() {
        return "Virement Bancaire";
    }
    
    @Override
    public boolean valider() {
        
        if (numeroReference == null || numeroReference.trim().isEmpty()) {
            return false;
        }
        
        if (numeroReference.length() < 5) {
            return false;
        }
        
        if (nomBanque == null || nomBanque.trim().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String getDetailsPaiement() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format(
            "Méthode: Virement Bancaire\n" +
            "Référence: %s\n" +
            "Banque: %s\n" +
            "Compte: %s\n" +
            "Titulaire: %s\n" +
            "Date: %s\n" +
            "Statut: %s\n" +
            "Montant: %.2f DZD",
            numeroReference,
            nomBanque,
            compteBancaire != null ? maskAccountNumber(compteBancaire) : "Non spécifié",
            nomTitulaire != null ? nomTitulaire : "Non spécifié",
            dateTransfert.format(formatter),
            status.getDisplayName(),
            montantPaye
        );
    }
    
    public void confirmTransfer() {
        this.status = TransferStatus.CONFIRMED;
        this.valide = true;
    }
    
    public void rejectTransfer() {
        this.status = TransferStatus.REJECTED;
        this.valide = false;
    }
    
    private String maskAccountNumber(String account) {
        if (account == null || account.length() < 4) {
            return "****";
        }
        return "****" + account.substring(account.length() - 4);
    }
    
    public String getNumeroReference() {
        return numeroReference;
    }
    
    public String getNomBanque() {
        return nomBanque;
    }
    
    public String getCompteBancaire() {
        return compteBancaire;
    }
    
    public String getNomTitulaire() {
        return nomTitulaire;
    }
    
    public double getMontantPaye() {
        return montantPaye;
    }
    
    public LocalDateTime getDateTransfert() {
        return dateTransfert;
    }
    
    public TransferStatus getStatus() {
        return status;
    }
    
    public boolean isValide() {
        return valide;
    }
    
    public void setCompteBancaire(String compteBancaire) {
        this.compteBancaire = compteBancaire;
    }
    
    public void setNomTitulaire(String nomTitulaire) {
        this.nomTitulaire = nomTitulaire;
    }
}
